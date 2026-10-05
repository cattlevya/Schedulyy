package com.pemmob.schedulyy.data.remote.gemini

import com.pemmob.schedulyy.data.util.ProcessedImage
import com.pemmob.schedulyy.domain.model.ScanReviewData
import com.pemmob.schedulyy.domain.model.ScanScheduleCandidate
import com.pemmob.schedulyy.domain.model.ScanTaskCandidate
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiScheduleExtractor @Inject constructor(
    private val geminiClient: GeminiClient,
    private val json: Json
) {
    suspend fun extractFromImages(
        apiKey: String,
        model: String,
        images: List<ProcessedImage>,
        spaceName: String,
        existingCourses: List<String>
    ): Result<ScanReviewData> = withContext(Dispatchers.IO) {
        if (images.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Tidak ada gambar yang dipilih."))
        }

        val prompt = GeminiPromptBuilder.buildSystemPrompt(spaceName, existingCourses)

        val parts = mutableListOf<GeminiPartDto>()
        parts.add(GeminiPartDto(text = prompt))
        for (img in images) {
            parts.add(
                GeminiPartDto(
                    inlineData = GeminiInlineDataDto(
                        mimeType = img.mimeType,
                        data = img.base64Data
                    )
                )
            )
        }

        val request = GeminiGenerateContentRequest(
            contents = listOf(GeminiContentDto(parts = parts)),
            generationConfig = GeminiGenerationConfigDto(
                responseMimeType = "application/json",
                temperature = 0.1f
            )
        )

        val firstAttemptResult = geminiClient.generateContent(apiKey, model, request)
        if (firstAttemptResult.isFailure) {
            return@withContext Result.failure(firstAttemptResult.exceptionOrNull() ?: Exception("Gagal menghubungi Gemini API"))
        }

        val response = firstAttemptResult.getOrNull()
        val rawJsonText = response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text

        if (rawJsonText.isNullOrBlank()) {
            return@withContext Result.failure(Exception("Gemini tidak mengembalikan teks atau respons kosong."))
        }

        // Parse JSON (mencoba parse pertama)
        val parsed = parseJsonTolerant(rawJsonText)
        if (parsed != null) {
            return@withContext Result.success(mapDtoToScanReview(parsed))
        }

        // Retry 1x (§6.6: bila JSON rusak atau terpotong, coba ulang 1 kali)
        val retryParts = parts.toMutableList()
        retryParts.add(GeminiPartDto(text = "PENTING: Respons JSON sebelumnya tidak valid atau terpotong. Tolong ulangi dan keluarkan HANYA JSON murni yang lengkap dan valid sesuai format skema."))

        val retryRequest = GeminiGenerateContentRequest(
            contents = listOf(GeminiContentDto(parts = retryParts)),
            generationConfig = GeminiGenerationConfigDto(
                responseMimeType = "application/json",
                temperature = 0.1f
            )
        )

        val retryResponse = geminiClient.generateContent(apiKey, model, retryRequest).getOrNull()
        val retryJsonText = retryResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text

        if (!retryJsonText.isNullOrBlank()) {
            val retryParsed = parseJsonTolerant(retryJsonText)
            if (retryParsed != null) {
                return@withContext Result.success(mapDtoToScanReview(retryParsed))
            }
        }

        Result.failure(Exception("Gagal membaca struktur jadwal dari gambar. Pastikan gambar cukup jelas dan coba lagi."))
    }

    internal fun parseJsonTolerant(raw: String): GeminiExtractionResponseDto? {
        val clean = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            json.decodeFromString<GeminiExtractionResponseDto>(clean)
        } catch (e: Exception) {
            // Coba ambil substring dari kurung kurawal pertama ke terakhir
            val startIndex = clean.indexOf('{')
            val endIndex = clean.lastIndexOf('}')
            if (startIndex != -1 && endIndex > startIndex) {
                val candidate = clean.substring(startIndex, endIndex + 1)
                try {
                    json.decodeFromString<GeminiExtractionResponseDto>(candidate)
                } catch (e2: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun mapDtoToScanReview(dto: GeminiExtractionResponseDto): ScanReviewData {
        val schedules = dto.schedules.map { s ->
            ScanScheduleCandidate(
                courseName = s.courseName.ifBlank { "Mata Kuliah" },
                courseCode = s.courseCode,
                dayOfWeek = s.dayOfWeek.coerceIn(1, 7),
                startTime = normalizeTime(s.startTime, "08:00"),
                endTime = s.endTime?.let { normalizeTime(it, "09:40") },
                room = s.room,
                lecturer = s.lecturer,
                confidence = s.confidence,
                isSelected = true
            )
        }

        val tasks = dto.tasks.map { t ->
            val priority = when (t.priorityHint?.uppercase()) {
                "HIGH", "TINGGI" -> TaskPriority.HIGH
                "LOW", "RENDAH" -> TaskPriority.LOW
                else -> TaskPriority.MEDIUM
            }

            ScanTaskCandidate(
                title = t.title.ifBlank { "Tugas Baru" },
                courseName = t.courseName,
                description = t.description,
                deadlineDate = t.deadlineDate,
                deadlineTime = t.deadlineTime?.let { normalizeTime(it, "23:59") },
                priority = priority,
                sourceSnippet = t.sourceSnippet,
                confidence = t.confidence,
                isSelected = true
            )
        }

        return ScanReviewData(
            schedules = schedules,
            tasks = tasks,
            warnings = dto.warnings
        )
    }

    private fun normalizeTime(raw: String, fallback: String): String {
        val trimmed = raw.trim().replace(".", ":").replace(" ", "")
        val match = Regex("""(\d{1,2}):(\d{2})""").find(trimmed)
        return if (match != null) {
            val (h, m) = match.destructured
            val hh = h.padStart(2, '0')
            "$hh:$m"
        } else {
            fallback
        }
    }
}
