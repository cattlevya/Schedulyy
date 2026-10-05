package com.pemmob.schedulyy.data.remote.gemini

import kotlinx.serialization.Serializable

@Serializable
data class GeminiExtractionResponseDto(
    val schedules: List<ExtractedScheduleDto> = emptyList(),
    val tasks: List<ExtractedTaskDto> = emptyList(),
    val warnings: List<String> = emptyList()
)

@Serializable
data class ExtractedScheduleDto(
    val courseName: String = "",
    val courseCode: String? = null,
    val dayOfWeek: Int = 1, // 1 = Senin, ... 7 = Minggu
    val startTime: String = "", // HH:mm
    val endTime: String? = null,
    val room: String? = null,
    val lecturer: String? = null,
    val confidence: Float = 1.0f
)

@Serializable
data class ExtractedTaskDto(
    val title: String = "",
    val courseName: String? = null,
    val description: String? = null,
    val deadlineDate: String? = null, // YYYY-MM-DD
    val deadlineTime: String? = null, // HH:mm
    val priorityHint: String? = null, // LOW, MEDIUM, HIGH
    val sourceSnippet: String? = null,
    val confidence: Float = 1.0f
)
