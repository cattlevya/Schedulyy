package com.pemmob.schedulyy.domain.util

import com.pemmob.schedulyy.domain.model.ClassSessionUiModel
import java.time.LocalTime

object ScheduleConflictUtil {

    /**
     * Menghitung overlap/bentrok sesi dalam hari yang sama dan
     * menentukan kolom indeks serta total kolom paralel (side-by-side)
     * agar layout jadwal tidak bertumpuk secara visual.
     */
    fun calculateConflicts(sessions: List<ClassSessionUiModel>): List<ClassSessionUiModel> {
        if (sessions.isEmpty()) return emptyList()

        // Kelompokkan per hari (1 = Senin ... 7 = Minggu)
        val sessionsByDay = sessions.groupBy { it.dayOfWeek }
        val result = mutableListOf<ClassSessionUiModel>()

        for ((_, daySessions) in sessionsByDay) {
            val sorted = daySessions.sortedWith(
                compareBy<ClassSessionUiModel> { it.startTime }
                    .thenBy { it.endTime }
            )

            val clusters = mutableListOf<MutableList<ClassSessionUiModel>>()
            var currentCluster = mutableListOf<ClassSessionUiModel>()
            var clusterEnd: LocalTime? = null

            for (session in sorted) {
                if (currentCluster.isEmpty()) {
                    currentCluster.add(session)
                    clusterEnd = session.endTime
                } else {
                    val end = clusterEnd ?: session.endTime
                    // Overlap jika session berikutnya mulai sebelum batas akhir cluster saat ini
                    if (session.startTime < end) {
                        currentCluster.add(session)
                        if (session.endTime > end) {
                            clusterEnd = session.endTime
                        }
                    } else {
                        clusters.add(currentCluster)
                        currentCluster = mutableListOf(session)
                        clusterEnd = session.endTime
                    }
                }
            }
            if (currentCluster.isNotEmpty()) {
                clusters.add(currentCluster)
            }

            // Hitung track index untuk tiap cluster
            for (cluster in clusters) {
                if (cluster.size == 1) {
                    result.add(
                        cluster[0].copy(
                            isConflict = false,
                            conflictIndex = 0,
                            totalConflicts = 1
                        )
                    )
                } else {
                    val trackEndTimes = mutableListOf<LocalTime>()
                    val assignments = mutableListOf<Pair<ClassSessionUiModel, Int>>()

                    for (session in cluster) {
                        var assignedTrack = -1
                        for (i in trackEndTimes.indices) {
                            if (!session.startTime.isBefore(trackEndTimes[i])) { // startTime >= trackEndTime
                                assignedTrack = i
                                trackEndTimes[i] = session.endTime
                                break
                            }
                        }
                        if (assignedTrack == -1) {
                            assignedTrack = trackEndTimes.size
                            trackEndTimes.add(session.endTime)
                        }
                        assignments.add(session to assignedTrack)
                    }

                    val totalTracks = trackEndTimes.size
                    val hasActualOverlap = totalTracks > 1

                    for ((session, trackIndex) in assignments) {
                        result.add(
                            session.copy(
                                isConflict = hasActualOverlap,
                                conflictIndex = if (hasActualOverlap) trackIndex else 0,
                                totalConflicts = if (hasActualOverlap) totalTracks else 1
                            )
                        )
                    }
                }
            }
        }

        return result.sortedWith(
            compareBy<ClassSessionUiModel> { it.dayOfWeek }
                .thenBy { it.startTime }
                .thenBy { it.conflictIndex }
        )
    }
}
