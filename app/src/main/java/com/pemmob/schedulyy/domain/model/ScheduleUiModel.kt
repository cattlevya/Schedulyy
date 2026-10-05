package com.pemmob.schedulyy.domain.model

import java.time.LocalTime

enum class ScheduleViewMode {
    DAY,
    WEEK
}

data class ClassSessionUiModel(
    val sessionId: Long,
    val courseId: Long,
    val courseName: String,
    val courseCode: String? = null,
    val lecturer: String? = null,
    val room: String? = null,
    val sks: Int? = null,
    val colorKey: String = "sky",
    val iconKey: String = "school",
    val dayOfWeek: Int, // 1=Senin ... 7=Minggu
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isConflict: Boolean = false,
    val conflictIndex: Int = 0,
    val totalConflicts: Int = 1
)
