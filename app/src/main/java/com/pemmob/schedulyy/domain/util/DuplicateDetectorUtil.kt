package com.pemmob.schedulyy.domain.util

import com.pemmob.schedulyy.data.local.dao.CourseWithSessions
import com.pemmob.schedulyy.data.local.entity.TaskEntity
import com.pemmob.schedulyy.domain.model.ScanScheduleCandidate
import com.pemmob.schedulyy.domain.model.ScanTaskCandidate
import com.pemmob.schedulyy.domain.model.TaskPriority
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DuplicateDetectorUtil {

    fun normalizeTitle(title: String): String {
        return title.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .trim()
    }

    fun checkScheduleDuplicates(
        candidates: List<ScanScheduleCandidate>,
        existingCourses: List<CourseWithSessions>
    ): List<ScanScheduleCandidate> {
        return candidates.map { candidate ->
            val normCandidateName = candidate.courseName.trim().lowercase()

            val duplicateSession = existingCourses.flatMap { courseWithSessions ->
                val courseName = courseWithSessions.course.name.trim().lowercase()
                courseWithSessions.sessions.map { session ->
                    courseName to session
                }
            }.firstOrNull { (courseName, session) ->
                val sameName = courseName == normCandidateName
                val sameDay = session.dayOfWeek == candidate.dayOfWeek
                val sameTime = session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")) == candidate.startTime
                sameName && sameDay && sameTime
            }

            if (duplicateSession != null) {
                candidate.copy(
                    isDuplicate = true,
                    duplicateWarning = "Sesi kelas ${candidate.courseName} pada hari ini pukul ${candidate.startTime} sudah ada di jadwal kamu.",
                    isSelected = false // Default unselected jika duplikat
                )
            } else {
                candidate
            }
        }
    }

    fun checkTaskDuplicates(
        candidates: List<ScanTaskCandidate>,
        existingTasks: List<TaskEntity>
    ): List<ScanTaskCandidate> {
        val zone = ZoneId.systemDefault()

        return candidates.map { candidate ->
            val normCandidateTitle = normalizeTitle(candidate.title)
            val candidateDateStr = candidate.deadlineDate

            val duplicateTask = existingTasks.firstOrNull { existing ->
                val normExistingTitle = normalizeTitle(existing.title)
                val sameTitle = normExistingTitle == normCandidateTitle

                val sameDate = if (candidateDateStr != null && existing.deadline != null) {
                    val existingDate = existing.deadline.atZone(zone).toLocalDate()
                    existingDate.toString() == candidateDateStr
                } else if (candidateDateStr == null && existing.deadline == null) {
                    true
                } else {
                    false
                }

                sameTitle && sameDate
            }

            if (duplicateTask != null) {
                candidate.copy(
                    isDuplicate = true,
                    duplicateWarning = "Tugas '${candidate.title}' sudah ada dengan batas waktu yang serupa.",
                    isSelected = false // Default unselected jika duplikat
                )
            } else {
                candidate
            }
        }
    }
}
