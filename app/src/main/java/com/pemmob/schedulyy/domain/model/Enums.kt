package com.pemmob.schedulyy.domain.model

enum class SpaceType {
    KULIAH,
    ORGANISASI,
    KEPANITIAAN,
    PRIBADI,
    CUSTOM
}

enum class ProkerStatus {
    RENCANA,
    BERJALAN,
    SELESAI
}

enum class EventType {
    RAPAT,
    PROKER,
    ACARA,
    DEADLINE,
    LAINNYA
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}

enum class TaskSource {
    MANUAL,
    AI_SCAN
}

enum class ReminderTargetType {
    TASK,
    CLASS_SESSION,
    EVENT
}

enum class DeadlineGroup {
    OVERDUE,
    TODAY,
    THIS_WEEK,
    LATER,
    NO_DEADLINE
}
