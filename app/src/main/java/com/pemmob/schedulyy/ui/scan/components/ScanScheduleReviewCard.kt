package com.pemmob.schedulyy.ui.scan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.domain.model.ScanScheduleCandidate
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@Composable
fun ScanScheduleReviewCard(
    candidate: ScanScheduleCandidate,
    onToggleSelect: () -> Unit,
    onUpdate: (ScanScheduleCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    var isExpanded by remember { mutableStateOf(false) }

    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val dayNamesFull = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (candidate.isSelected) Color.White else Color(0xFFF9F9FB))
            .border(
                1.5.dp,
                if (candidate.isSelected) inkColor else Color(0xFFD1D1D6),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        // Baris Atas: Checkbox + Judul Matkul + Badge Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (candidate.isSelected) inkColor else Color.White)
                    .border(2.dp, inkColor, RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggleSelect),
                contentAlignment = Alignment.Center
            ) {
                if (candidate.isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Pilih",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = candidate.courseName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = if (candidate.isSelected) inkColor else SchedulyyTheme.extendedColors.textSecondary
                    )

                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = inkColor.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { isExpanded = !isExpanded }
                    )
                }

                // Info singkat: Hari & Jam
                val dayStr = dayNamesFull.getOrElse(candidate.dayOfWeek - 1) { "Senin" }
                val timeStr = if (candidate.endTime != null) "${candidate.startTime} - ${candidate.endTime}" else candidate.startTime
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = SchedulyyTheme.extendedColors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$dayStr, $timeStr",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = SchedulyyTheme.extendedColors.textSecondary
                    )

                    if (!candidate.room.isNullOrBlank()) {
                        Text(
                            text = "• ${candidate.room}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                }

                // Badges: Duplikat & Confidence Rendah
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (candidate.isDuplicate) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SchedulyyTheme.extendedColors.pastelButter)
                                .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Mungkin Duplikat",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = inkColor
                            )
                        }
                    }

                    if (candidate.isLowConfidence) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SchedulyyTheme.extendedColors.pastelPink)
                                .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Perlu Dicek",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = inkColor
                            )
                        }
                    }
                }

                if (candidate.isDuplicate && candidate.duplicateWarning != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = candidate.duplicateWarning,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    )
                }
            }
        }

        // Form Edit jika di-expand
        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F7))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit Nama Matkul
                OutlinedTextField(
                    value = candidate.courseName,
                    onValueChange = { onUpdate(candidate.copy(courseName = it)) },
                    label = { Text("Nama Mata Kuliah", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = inkColor,
                        unfocusedBorderColor = Color(0xFFD1D1D6)
                    )
                )

                // Pilih Hari (Chip 1..7)
                Text(
                    text = "Hari Kuliah",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = inkColor
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(days) { index, day ->
                        val dayNum = index + 1
                        val isSelected = candidate.dayOfWeek == dayNum
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else Color.White)
                                .border(1.dp, inkColor, RoundedCornerShape(12.dp))
                                .clickable { onUpdate(candidate.copy(dayOfWeek = dayNum)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = inkColor
                            )
                        }
                    }
                }

                // Jam Mulai & Selesai
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = candidate.startTime,
                        onValueChange = { onUpdate(candidate.copy(startTime = it)) },
                        label = { Text("Mulai (HH:mm)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )

                    OutlinedTextField(
                        value = candidate.endTime ?: "",
                        onValueChange = { onUpdate(candidate.copy(endTime = it.ifBlank { null })) },
                        label = { Text("Selesai (HH:mm)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )
                }

                // Ruang & Dosen
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = candidate.room ?: "",
                        onValueChange = { onUpdate(candidate.copy(room = it.ifBlank { null })) },
                        label = { Text("Ruangan", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )

                    OutlinedTextField(
                        value = candidate.lecturer ?: "",
                        onValueChange = { onUpdate(candidate.copy(lecturer = it.ifBlank { null })) },
                        label = { Text("Dosen", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )
                }
            }
        }
    }
}
