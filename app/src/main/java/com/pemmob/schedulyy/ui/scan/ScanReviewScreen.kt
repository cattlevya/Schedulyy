package com.pemmob.schedulyy.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.domain.model.ScanReviewData
import com.pemmob.schedulyy.domain.model.ScanScheduleCandidate
import com.pemmob.schedulyy.domain.model.ScanTaskCandidate
import com.pemmob.schedulyy.ui.components.NeoButton
import com.pemmob.schedulyy.ui.scan.components.ScanScheduleReviewCard
import com.pemmob.schedulyy.ui.scan.components.ScanTaskReviewCard
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReviewScreen(
    reviewData: ScanReviewData,
    targetSpaceName: String,
    onToggleSchedule: (String) -> Unit,
    onUpdateSchedule: (ScanScheduleCandidate) -> Unit,
    onToggleTask: (String) -> Unit,
    onUpdateTask: (ScanTaskCandidate) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val bgGrouped = Color(0xFFF2F2F7)

    val selectedSchedulesCount = reviewData.schedules.count { it.isSelected }
    val selectedTasksCount = reviewData.tasks.count { it.isSelected }
    val totalSelected = selectedSchedulesCount + selectedTasksCount

    Scaffold(
        containerColor = bgGrouped,
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D1D6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoButton(
                        text = "Batal",
                        onClick = onCancel,
                        modifier = Modifier.weight(0.8f),
                        backgroundColor = Color.White
                    )

                    NeoButton(
                        text = if (totalSelected > 0) "Simpan ($totalSelected)" else "Pilih Item",
                        onClick = onSave,
                        enabled = totalSelected > 0,
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = SchedulyyTheme.extendedColors.btnPeriwinkle
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Batal",
                            tint = inkColor
                        )
                    }
                    Column {
                        Text(
                            text = "Periksa Hasil Pindai",
                            fontFamily = InstrumentSerif,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                        Text(
                            text = "Folder tujuan: $targetSpaceName • Edit jika ada data yang kurang pas",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                }
            }

            // Peringatan Gemini jika ada
            if (reviewData.warnings.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SchedulyyTheme.extendedColors.pastelButter.copy(alpha = 0.5f))
                            .border(1.dp, inkColor, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Catatan Ekstraksi AI",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = inkColor
                            )
                        }
                        reviewData.warnings.forEach { warning ->
                            Text(
                                text = "• $warning",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = inkColor
                            )
                        }
                    }
                }
            }

            // Seksi Jadwal Kuliah
            if (reviewData.schedules.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Jadwal Kelas (${reviewData.schedules.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = inkColor
                        )
                    }
                }

                items(reviewData.schedules, key = { it.id }) { scheduleCandidate ->
                    ScanScheduleReviewCard(
                        candidate = scheduleCandidate,
                        onToggleSelect = { onToggleSchedule(scheduleCandidate.id) },
                        onUpdate = onUpdateSchedule
                    )
                }
            }

            // Seksi Tugas & Deadline
            if (reviewData.tasks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Tugas & Deadline (${reviewData.tasks.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = inkColor
                        )
                    }
                }

                items(reviewData.tasks, key = { it.id }) { taskCandidate ->
                    ScanTaskReviewCard(
                        candidate = taskCandidate,
                        onToggleSelect = { onToggleTask(taskCandidate.id) },
                        onUpdate = onUpdateTask
                    )
                }
            }

            if (reviewData.schedules.isEmpty() && reviewData.tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ditemukan jadwal atau tugas yang jelas pada screenshot ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
