package com.pemmob.schedulyy.domain.model

sealed interface GeminiConnectionResult {
    data class Success(val message: String, val availableModels: List<String>) : GeminiConnectionResult
    data class InvalidKey(val message: String) : GeminiConnectionResult
    data class RateLimit(val message: String) : GeminiConnectionResult
    data class NetworkError(val message: String) : GeminiConnectionResult
    data class GeneralError(val code: Int, val message: String) : GeminiConnectionResult
}
