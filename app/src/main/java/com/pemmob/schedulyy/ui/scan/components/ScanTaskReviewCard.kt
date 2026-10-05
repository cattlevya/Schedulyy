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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatQuote
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.domain.model.ScanTaskCandidate
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@Composable
fun ScanTaskReviewCard(
    candidate: ScanTaskCandidate,
    onToggleSelect: () -> Unit,
    onUpdate: (ScanTaskCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    var isExpanded by remember { mutableStateOf(false) }

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
        // Baris Atas: Checkbox + Judul Tugas + Badge Status
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
                        text = candidate.title,
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

                // Info: Matkul & Deadline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    if (!candidate.courseName.isNullOrBlank()) {
                        Text(
                            text = candidate.courseName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = SchedulyyTheme.extendedColors.tintCobalt
                        )
                        Text(text = "•", color = SchedulyyTheme.extendedColors.textSecondary)
                    }

                    if (!candidate.deadlineDate.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = SchedulyyTheme.extendedColors.textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        val deadlineDisplay = if (candidate.deadlineTime != null) {
                            "${candidate.deadlineDate} ${candidate.deadlineTime}"
                        } else {
                            "${candidate.deadlineDate} (Tanpa jam)"
                        }
                        Text(
                            text = deadlineDisplay,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    } else {
                        Text(
                            text = "Tanpa deadline",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.sp
                            ),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                }

                // Source Snippet Quote jika ada
                if (!candidate.sourceSnippet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF2F2F7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FormatQuote,
                            contentDescription = null,
                            tint = SchedulyyTheme.extendedColors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "\"${candidate.sourceSnippet}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 14.sp
                            ),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                }

                // Badges: Duplikat & Confidence Rendah & Prioritas
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge Prioritas
                    val priorityColor = when (candidate.priority) {
                        TaskPriority.HIGH -> SchedulyyTheme.extendedColors.danger.copy(alpha = 0.2f)
                        TaskPriority.MEDIUM -> SchedulyyTheme.extendedColors.pastelButter
                        TaskPriority.LOW -> SchedulyyTheme.extendedColors.pastelMint
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor)
                            .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = candidate.priority.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = inkColor
                        )
                    }

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
                // Edit Judul Tugas
                OutlinedTextField(
                    value = candidate.title,
                    onValueChange = { onUpdate(candidate.copy(title = it)) },
                    label = { Text("Judul Tugas", fontSize = 11.sp) },
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

                // Edit Matkul Terkait & Prioritas
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = candidate.courseName ?: "",
                        onValueChange = { onUpdate(candidate.copy(courseName = it.ifBlank { null })) },
                        label = { Text("Matkul", fontSize = 11.sp) },
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

                    // Pilihan Prioritas Chip
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prioritas",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = inkColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TaskPriority.entries.forEach { p ->
                                val isSelected = candidate.priority == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else Color.White)
                                        .border(1.dp, inkColor, RoundedCornerShape(8.dp))
                                        .clickable { onUpdate(candidate.copy(priority = p)) }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = p.name.take(1),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        ),
                                        color = inkColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Deadline Date & Time
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = candidate.deadlineDate ?: "",
                        onValueChange = { onUpdate(candidate.copy(deadlineDate = it.ifBlank { null })) },
                        label = { Text("Tanggal (YYYY-MM-DD)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )

                    OutlinedTextField(
                        value = candidate.deadlineTime ?: "",
                        onValueChange = { onUpdate(candidate.copy(deadlineTime = it.ifBlank { null })) },
                        label = { Text("Jam (HH:mm)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = Color(0xFFD1D1D6)
                        )
                    )
                }

                // Deskripsi / Catatan
                OutlinedTextField(
                    value = candidate.description ?: "",
                    onValueChange = { onUpdate(candidate.copy(description = it.ifBlank { null })) },
                    label = { Text("Catatan / Deskripsi", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
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
