package com.pemmob.schedulyy.domain.util

import com.pemmob.schedulyy.domain.model.DeadlineGroup
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

object DeadlineGroupUtil {

    /**
     * Mengelompokkan deadline tugas sesuai §4.4 PROJECT_BRIEF:
     * - TERLAMBAT (Overdue)
     * - HARI INI (Today)
     * - MINGGU INI (This Week)
     * - NANTI (Later)
     * - TANPA DEADLINE (No Deadline)
     *
     * Memperhitungkan flag `hasTime`: tugas tanpa jam pada hari ini tidak dianggap terlambat
     * sampai hari tersebut berganti.
     */
    fun classify(
        deadline: Instant?,
        hasTime: Boolean,
        now: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): DeadlineGroup {
        if (deadline == null) return DeadlineGroup.NO_DEADLINE

        val taskDate = deadline.atZone(zoneId).toLocalDate()
        val today = now.atZone(zoneId).toLocalDate()

        if (taskDate.isBefore(today)) {
            return DeadlineGroup.OVERDUE
        }

        if (taskDate.isEqual(today)) {
            return if (hasTime && deadline.isBefore(now)) {
                DeadlineGroup.OVERDUE
            } else {
                DeadlineGroup.TODAY
            }
        }

        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return if (!taskDate.isAfter(endOfWeek)) {
            DeadlineGroup.THIS_WEEK
        } else {
            DeadlineGroup.LATER
        }
    }
}
