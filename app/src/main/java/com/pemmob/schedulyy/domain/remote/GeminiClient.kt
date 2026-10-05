package com.pemmob.schedulyy.domain.remote

import com.pemmob.schedulyy.data.remote.gemini.GeminiGenerateContentRequest
import com.pemmob.schedulyy.data.remote.gemini.GeminiGenerateContentResponse
import com.pemmob.schedulyy.domain.model.GeminiConnectionResult

interface GeminiClient {
    suspend fun testConnection(apiKey: String): GeminiConnectionResult
    suspend fun getAvailableModels(apiKey: String): Result<List<String>>
    suspend fun generateContent(
        apiKey: String,
        model: String,
        request: GeminiGenerateContentRequest
    ): Result<GeminiGenerateContentResponse>
}
