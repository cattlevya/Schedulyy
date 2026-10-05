package com.pemmob.schedulyy.data.remote.gemini

import com.pemmob.schedulyy.domain.model.GeminiConnectionResult
import com.pemmob.schedulyy.domain.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRestClientImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) : GeminiClient {

    override suspend fun testConnection(apiKey: String): GeminiConnectionResult = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return@withContext GeminiConnectionResult.InvalidKey("API key belum diisi.")
        }

        val request = Request.Builder()
            .url("$BASE_URL/models")
            .header(HEADER_API_KEY, trimmedKey)
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                when (response.code) {
                    200 -> {
                        val parsed = try {
                            json.decodeFromString<GeminiModelListResponse>(bodyString)
                        } catch (e: Exception) {
                            GeminiModelListResponse()
                        }
                        val models = parsed.models
                            .filter { it.supportedGenerationMethods.contains("generateContent") }
                            .map { it.modelId }
                            .ifEmpty { listOf("gemini-1.5-flash", "gemini-2.0-flash") }

                        GeminiConnectionResult.Success(
                            message = "Berhasil terhubung ke Gemini API.",
                            availableModels = models
                        )
                    }
                    400, 403 -> {
                        val errorMessage = extractErrorMessage(bodyString) ?: "API key ditolak atau tidak valid."
                        GeminiConnectionResult.InvalidKey(errorMessage)
                    }
                    429 -> {
                        GeminiConnectionResult.RateLimit("Kuota API key habis atau mencapai batas penggunaan. Coba lagi beberapa saat lagi.")
                    }
                    in 500..599 -> {
                        GeminiConnectionResult.GeneralError(response.code, "Layanan Gemini sedang mengalami gangguan (${response.code}). Coba lagi nanti.")
                    }
                    else -> {
                        GeminiConnectionResult.GeneralError(response.code, "Terjadi kesalahan (${response.code}): ${response.message}")
                    }
                }
            }
        } catch (e: UnknownHostException) {
            GeminiConnectionResult.NetworkError("Tidak ada koneksi internet. Periksa koneksi data atau Wi-Fi kamu.")
        } catch (e: SocketTimeoutException) {
            GeminiConnectionResult.NetworkError("Koneksi timeout (waktu habis). Periksa koneksi internet kamu.")
        } catch (e: IOException) {
            GeminiConnectionResult.NetworkError("Gagal terhubung: ${e.localizedMessage ?: "Kesalahan jaringan"}")
        } catch (e: Exception) {
            GeminiConnectionResult.GeneralError(-1, "Terjadi kesalahan: ${e.localizedMessage ?: "Tidak diketahui"}")
        }
    }

    override suspend fun getAvailableModels(apiKey: String): Result<List<String>> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("API key belum diisi."))
        }

        val request = Request.Builder()
            .url("$BASE_URL/models")
            .header(HEADER_API_KEY, trimmedKey)
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<GeminiModelListResponse>(bodyString)
                    val models = parsed.models
                        .filter { it.supportedGenerationMethods.contains("generateContent") }
                        .map { it.modelId }
                    Result.success(models)
                } else {
                    val err = extractErrorMessage(bodyString) ?: "HTTP ${response.code}"
                    Result.failure(Exception(err))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateContent(
        apiKey: String,
        model: String,
        request: GeminiGenerateContentRequest
    ): Result<GeminiGenerateContentResponse> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("API key belum diisi."))
        }

        val url = "$BASE_URL/models/$model:generateContent"
        val jsonPayload = json.encodeToString(GeminiGenerateContentRequest.serializer(), request)
        val body = jsonPayload.toRequestBody(MEDIA_TYPE_JSON)

        val httpRequest = Request.Builder()
            .url(url)
            .header(HEADER_API_KEY, trimmedKey)
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            okHttpClient.newCall(httpRequest).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<GeminiGenerateContentResponse>(bodyString)
                    Result.success(parsed)
                } else {
                    val errMsg = extractErrorMessage(bodyString) ?: "HTTP ${response.code}: ${response.message}"
                    Result.failure(Exception(errMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(bodyString: String): String? {
        return try {
            val errDto = json.decodeFromString<GeminiErrorResponse>(bodyString)
            errDto.error?.message
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val HEADER_API_KEY = "x-goog-api-key"
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}
