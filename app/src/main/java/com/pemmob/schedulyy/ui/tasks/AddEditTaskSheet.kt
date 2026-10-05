package com.pemmob.schedulyy.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    viewModel: TaskViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val state by viewModel.sheetState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeSheet() },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(inkColor.copy(0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.editingTask == null)
                        stringResource(R.string.sheet_add_task)
                    else
                        stringResource(R.string.sheet_edit_task),
                    fontFamily = InstrumentSerif,
                    fontSize = 26.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.closeSheet() }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close), tint = inkColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = inkColor.copy(0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Judul
            SheetSectionLabel(stringResource(R.string.sheet_label_title))
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.updateSheetTitle(it) },
                placeholder = {
                    Text(
                        stringResource(R.string.sheet_placeholder_title),
                        color = inkColor.copy(0.35f)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = inkColor,
                    unfocusedBorderColor = inkColor.copy(0.25f),
                    focusedTextColor = inkColor,
                    unfocusedTextColor = inkColor
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Catatan
            SheetSectionLabel(stringResource(R.string.sheet_label_notes))
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.updateSheetNotes(it) },
                placeholder = {
                    Text(
                        stringResource(R.string.sheet_placeholder_notes),
                        color = inkColor.copy(0.35f)
                    )
                },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = inkColor,
                    unfocusedBorderColor = inkColor.copy(0.25f),
                    focusedTextColor = inkColor,
                    unfocusedTextColor = inkColor
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Deadline
            SheetSectionLabel(stringResource(R.string.sheet_label_deadline))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pilih tanggal
                val deadlineDate = state.deadline?.atZone(zone)?.toLocalDate()
                val dateLabel = deadlineDate?.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id")))
                    ?: stringResource(R.string.sheet_no_date)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (deadlineDate != null) SchedulyyTheme.extendedColors.pastelButter else Color(0xFFF5F5F5))
                        .border(1.5.dp, inkColor.copy(if (deadlineDate != null) 1f else 0.2f), RoundedCornerShape(10.dp))
                        .clickable {
                            val today = LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newDate = LocalDate.of(y, m + 1, d)
                                    val time = state.deadline?.atZone(zone)?.toLocalTime() ?: LocalTime.NOON
                                    viewModel.updateSheetDeadline(
                                        newDate.atTime(time).atZone(zone).toInstant()
                                    )
                                },
                                today.year, today.monthValue - 1, today.dayOfMonth
                            ).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CalendarToday, null, tint = inkColor.copy(0.6f), modifier = Modifier.size(16.dp))
                        Text(dateLabel, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = inkColor)
                    }
                }

                // Toggle jam
                if (state.deadline != null) {
                    val timeLabel = if (state.hasTime) {
                        state.deadline!!.atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm"))
                    } else {
                        stringResource(R.string.sheet_add_time)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (state.hasTime) SchedulyyTheme.extendedColors.pastelMint else Color(0xFFF5F5F5))
                            .border(1.5.dp, inkColor.copy(if (state.hasTime) 1f else 0.2f), RoundedCornerShape(10.dp))
                            .clickable {
                                if (state.hasTime) {
                                    viewModel.updateSheetHasTime(false)
                                } else {
                                    val t = state.deadline!!.atZone(zone).toLocalTime()
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            val date = state.deadline!!.atZone(zone).toLocalDate()
                                            viewModel.updateSheetDeadline(
                                                date.atTime(h, m).atZone(zone).toInstant()
                                            )
                                            viewModel.updateSheetHasTime(true)
                                        },
                                        t.hour, t.minute, true
                                    ).show()
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(timeLabel, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = inkColor)
                    }

                    // Hapus deadline
                    IconButton(
                        onClick = {
                            viewModel.updateSheetDeadline(null)
                            viewModel.updateSheetHasTime(false)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.action_remove_deadline), tint = inkColor.copy(0.5f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Prioritas
            SheetSectionLabel(stringResource(R.string.sheet_label_priority))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { p ->
                    val label = when (p) {
                        TaskPriority.LOW    -> stringResource(R.string.priority_low)
                        TaskPriority.MEDIUM -> stringResource(R.string.priority_medium)
                        TaskPriority.HIGH   -> stringResource(R.string.priority_high)
                    }
                    val bg = when (p) {
                        TaskPriority.LOW    -> SchedulyyTheme.extendedColors.pastelMint
                        TaskPriority.MEDIUM -> SchedulyyTheme.extendedColors.pastelButter
                        TaskPriority.HIGH   -> Color(0xFFFFD6D6)
                    }
                    val selected = state.priority == p
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (selected) bg else Color(0xFFF5F5F5))
                            .border(if (selected) 2.dp else 1.dp, inkColor.copy(if (selected) 1f else 0.2f), CircleShape)
                            .clickable { viewModel.updateSheetPriority(p) }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = inkColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Subtugas
            SheetSectionLabel(stringResource(R.string.sheet_label_subtasks))
            state.subTasks.forEachIndexed { i, sub ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mini checkbox
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, inkColor.copy(0.4f), CircleShape)
                            .background(if (sub.done) SchedulyyTheme.extendedColors.pastelMint else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (sub.done) {
                            Icon(Icons.Outlined.Check, null, tint = inkColor, modifier = Modifier.size(12.dp))
                        }
                    }
                    OutlinedTextField(
                        value = sub.title,
                        onValueChange = { viewModel.updateSubTask(i, it) },
                        placeholder = { Text(stringResource(R.string.sheet_subtask_placeholder), color = inkColor.copy(0.3f)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = inkColor.copy(0.4f),
                            unfocusedBorderColor = inkColor.copy(0.1f),
                            focusedTextColor = inkColor,
                            unfocusedTextColor = inkColor
                        )
                    )
                    IconButton(onClick = { viewModel.removeSubTask(i) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = inkColor.copy(0.4f), modifier = Modifier.size(16.dp))
                    }
                }
            }
            // Tambah subtugas
            Row(
                modifier = Modifier
                    .clickable { viewModel.addSubTask() }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = SchedulyyTheme.extendedColors.tintCobalt, modifier = Modifier.size(18.dp))
                Text(
                    stringResource(R.string.sheet_add_subtask),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = SchedulyyTheme.extendedColors.tintCobalt
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            val canSave = state.title.isNotBlank() && !state.isSaving
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(start = 3.dp, top = 3.dp)
                        .background(inkColor, RoundedCornerShape(14.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (canSave) SchedulyyTheme.extendedColors.btnPeriwinkle else Color(0xFFE5E5EA),
                            RoundedCornerShape(14.dp)
                        )
                        .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                        .clickable(enabled = canSave) { viewModel.saveTask() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.isSaving) stringResource(R.string.sheet_saving)
                               else stringResource(R.string.sheet_save),
                        fontWeight = FontWeight.SemiBold,
                        color = inkColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SheetSectionLabel(text: String) {
    Text(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = SchedulyyTheme.extendedColors.ink.copy(alpha = 0.55f),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
