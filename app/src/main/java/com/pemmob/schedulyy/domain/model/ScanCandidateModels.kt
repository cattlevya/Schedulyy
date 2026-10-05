package com.pemmob.schedulyy.domain.model

import java.util.UUID

data class ScanScheduleCandidate(
    val id: String = UUID.randomUUID().toString(),
    val courseName: String,
    val courseCode: String? = null,
    val dayOfWeek: Int, // 1=Senin .. 7=Minggu
    val startTime: String, // HH:mm
    val endTime: String? = null,
    val room: String? = null,
    val lecturer: String? = null,
    val confidence: Float = 1.0f,
    val isDuplicate: Boolean = false,
    val duplicateWarning: String? = null,
    val isSelected: Boolean = true
) {
    val isLowConfidence: Boolean get() = confidence < 0.75f
}

data class ScanTaskCandidate(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val courseName: String? = null,
    val description: String? = null,
    val deadlineDate: String? = null, // YYYY-MM-DD
    val deadlineTime: String? = null, // HH:mm (null if none)
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val sourceSnippet: String? = null,
    val confidence: Float = 1.0f,
    val isDuplicate: Boolean = false,
    val duplicateWarning: String? = null,
    val isSelected: Boolean = true
) {
    val isLowConfidence: Boolean get() = confidence < 0.75f
}

data class ScanReviewData(
    val schedules: List<ScanScheduleCandidate> = emptyList(),
    val tasks: List<ScanTaskCandidate> = emptyList(),
    val warnings: List<String> = emptyList()
)
