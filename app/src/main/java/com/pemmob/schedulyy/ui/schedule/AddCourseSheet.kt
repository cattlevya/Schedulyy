package com.pemmob.schedulyy.ui.schedule

import android.app.TimePickerDialog
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseSheet(
    viewModel: ScheduleViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val state by viewModel.courseSheetState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink
    val context = LocalContext.current
    val isEdit = state.courseId != null && state.courseId != 0L

    val pastelColors = listOf(
        "sky" to SchedulyyTheme.extendedColors.pastelSky,
        "mint" to SchedulyyTheme.extendedColors.pastelMint,
        "butter" to SchedulyyTheme.extendedColors.pastelButter,
        "bubblegum" to SchedulyyTheme.extendedColors.pastelPink,
        "lilac" to SchedulyyTheme.extendedColors.pastelLilac,
        "lime" to SchedulyyTheme.extendedColors.pastelLime
    )

    val days = listOf(
        1 to stringResource(R.string.day_mon_short),
        2 to stringResource(R.string.day_tue_short),
        3 to stringResource(R.string.day_wed_short),
        4 to stringResource(R.string.day_thu_short),
        5 to stringResource(R.string.day_fri_short),
        6 to stringResource(R.string.day_sat_short),
        7 to stringResource(R.string.day_sun_short)
    )

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    ModalBottomSheet(
        onDismissRequest = { viewModel.dismissCourseSheet() },
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
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        if (isEdit) R.string.sheet_edit_course_title else R.string.sheet_add_course_title
                    ),
                    fontFamily = InstrumentSerif,
                    fontSize = 26.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.dismissCourseSheet() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = inkColor
                    )
                }
            }

            // Error Message (jika ada)
            state.errorMessage?.let { error ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFE5E5))
                        .border(1.dp, Color(0xFFE0243B), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0243B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 1. Nama Matkul
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_course_name),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateCourseName(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_course_name_hint),
                            color = inkColor.copy(0.35f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = inkColor,
                        unfocusedBorderColor = inkColor.copy(0.25f),
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    singleLine = true
                )
            }

            // 2. Kode Matkul & Ruang Default
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sheet_course_code),
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = { viewModel.updateCourseCode(it) },
                        placeholder = { Text("IF204", color = inkColor.copy(0.35f)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = inkColor.copy(0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        singleLine = true
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sheet_course_room),
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    OutlinedTextField(
                        value = state.defaultRoom,
                        onValueChange = { viewModel.updateCourseRoom(it) },
                        placeholder = { Text("Lab 3", color = inkColor.copy(0.35f)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = inkColor,
                            unfocusedBorderColor = inkColor.copy(0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        singleLine = true
                    )
                }
            }

            // 3. Dosen
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_course_lecturer),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.lecturer,
                    onValueChange = { viewModel.updateCourseLecturer(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_course_lecturer_hint),
                            color = inkColor.copy(0.35f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = inkColor,
                        unfocusedBorderColor = inkColor.copy(0.25f),
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    singleLine = true
                )
            }

            // 4. Bobot SKS & Warna Tema
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SKS Picker
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.sheet_course_sks),
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 2, 3, 4, 6).forEach { sksVal ->
                            val isSelected = state.sks == sksVal
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) inkColor else inkColor.copy(0.25f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateCourseSks(sksVal) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$sksVal",
                                    fontFamily = SpaceMono,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = inkColor
                                )
                            }
                        }
                    }
                }

                // Warna Tema
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.sheet_course_color),
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        pastelColors.forEach { (key, color) ->
                            val isSelected = state.colorKey == key
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) inkColor else inkColor.copy(0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.updateCourseColor(key) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = inkColor.copy(alpha = 0.1f))

            // 5. Sesi Kelas (Multi-session)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sheet_course_sessions),
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.sheet_course_add_session),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchedulyyTheme.extendedColors.tintCobalt,
                        modifier = Modifier.clickable { viewModel.addSessionRow() }
                    )
                }

                state.sessions.forEachIndexed { index, session ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.5.dp, inkColor.copy(0.3f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Row Hari
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    days.forEach { (dow, dayAbbr) ->
                                        val isSelected = session.dayOfWeek == dow
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isSelected) SchedulyyTheme.extendedColors.pastelSky
                                                    else Color.White
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) inkColor else inkColor.copy(0.2f),
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .clickable { viewModel.updateSessionRow(index, dayOfWeek = dow) }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = dayAbbr,
                                                fontFamily = SpaceMono,
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = inkColor
                                            )
                                        }
                                    }
                                }

                                if (state.sessions.size > 1) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.sheet_course_session_remove),
                                        tint = Color(0xFFE0243B),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { viewModel.removeSessionRow(index) }
                                    )
                                }
                            }

                            // Row Jam Mulai - Jam Selesai & Ruang
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Jam Mulai Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            TimePickerDialog(
                                                context,
                                                { _, hour, minute ->
                                                    viewModel.updateSessionRow(
                                                        index,
                                                        startTime = LocalTime.of(hour, minute)
                                                    )
                                                },
                                                session.startTime.hour,
                                                session.startTime.minute,
                                                true
                                            ).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            tint = inkColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = session.startTime.format(timeFormatter),
                                            fontFamily = SpaceMono,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = inkColor
                                        )
                                    }
                                }

                                Text("–", color = inkColor.copy(0.5f), fontWeight = FontWeight.Bold)

                                // Jam Selesai Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            TimePickerDialog(
                                                context,
                                                { _, hour, minute ->
                                                    viewModel.updateSessionRow(
                                                        index,
                                                        endTime = LocalTime.of(hour, minute)
                                                    )
                                                },
                                                session.endTime.hour,
                                                session.endTime.minute,
                                                true
                                            ).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            tint = inkColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = session.endTime.format(timeFormatter),
                                            fontFamily = SpaceMono,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = inkColor
                                        )
                                    }
                                }

                                // Ruang khusus sesi
                                OutlinedTextField(
                                    value = session.room,
                                    onValueChange = { viewModel.updateSessionRow(index, room = it) },
                                    placeholder = { Text("Ruang", color = inkColor.copy(0.35f), fontSize = 11.sp) },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = inkColor,
                                        unfocusedBorderColor = inkColor.copy(0.25f),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons: Simpan & Hapus
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                    .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                    .clickable { viewModel.saveCourse() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.sheet_course_save),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            }

            if (isEdit && state.courseId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFE5E5))
                        .border(1.5.dp, Color(0xFFE0243B), RoundedCornerShape(14.dp))
                        .clickable { viewModel.deleteCourse(state.courseId!!) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = Color(0xFFE0243B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.sheet_course_delete),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0243B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
