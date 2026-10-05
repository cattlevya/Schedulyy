package com.pemmob.schedulyy.data.remote.gemini

import kotlinx.serialization.Serializable

@Serializable
data class GeminiModelListResponse(
    val models: List<GeminiModelDto> = emptyList()
)

@Serializable
data class GeminiModelDto(
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val supportedGenerationMethods: List<String> = emptyList()
) {
    /**
     * Mengembalikan id model tanpa prefix "models/", contoh "gemini-1.5-flash"
     */
    val modelId: String
        get() = name.removePrefix("models/")
}

@Serializable
data class GeminiErrorResponse(
    val error: GeminiErrorDetails? = null
)

@Serializable
data class GeminiErrorDetails(
    val code: Int = 0,
    val message: String = "",
    val status: String = ""
)

// DTO untuk GenerateContent (digunakan di Phase 5 tes koneksi & Phase 6 ekstraksi)
@Serializable
data class GeminiGenerateContentRequest(
    val contents: List<GeminiContentDto>,
    val generationConfig: GeminiGenerationConfigDto? = null
)

@Serializable
data class GeminiContentDto(
    val parts: List<GeminiPartDto>,
    val role: String? = null
)

@Serializable
data class GeminiPartDto(
    val text: String? = null,
    val inlineData: GeminiInlineDataDto? = null
)

@Serializable
data class GeminiInlineDataDto(
    val mimeType: String,
    val data: String
)

@Serializable
data class GeminiGenerationConfigDto(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@Serializable
data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidateDto> = emptyList(),
    val usageMetadata: GeminiUsageMetadataDto? = null
)

@Serializable
data class GeminiCandidateDto(
    val content: GeminiContentDto? = null,
    val finishReason: String? = null
)

@Serializable
data class GeminiUsageMetadataDto(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)
