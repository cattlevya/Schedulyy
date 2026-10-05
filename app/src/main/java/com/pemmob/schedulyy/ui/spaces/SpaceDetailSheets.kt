package com.pemmob.schedulyy.ui.spaces

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.pemmob.schedulyy.domain.model.EventType
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ─── Sheet Tambah Divisi ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDivisionSheet(
    viewModel: SpaceDetailViewModel
) {
    val state by viewModel.divisionSheetState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pastelColors = listOf(
        "mint" to SchedulyyTheme.extendedColors.pastelMint,
        "sky" to SchedulyyTheme.extendedColors.pastelSky,
        "butter" to SchedulyyTheme.extendedColors.pastelButter,
        "bubblegum" to SchedulyyTheme.extendedColors.pastelPink,
        "lilac" to SchedulyyTheme.extendedColors.pastelLilac,
        "lime" to SchedulyyTheme.extendedColors.pastelLime
    )

    ModalBottomSheet(
        onDismissRequest = { viewModel.dismissAddDivision() },
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
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sheet_add_division_title),
                    fontFamily = InstrumentSerif,
                    fontSize = 24.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.dismissAddDivision() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = inkColor
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_division_name),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateDivisionName(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_division_name_hint),
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

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_division_color),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pastelColors.forEach { (key, color) ->
                        val isSelected = state.colorKey == key
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) inkColor else inkColor.copy(0.3f),
                                    shape = CircleShape
                                )
                                .clickable { viewModel.updateDivisionColor(key) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                    .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                    .clickable { viewModel.saveDivision() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.sheet_save_division),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Sheet Tambah/Edit Proker ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProkerSheet(
    viewModel: SpaceDetailViewModel
) {
    val state by viewModel.prokerSheetState.collectAsState()
    val detailState by viewModel.uiState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))

    ModalBottomSheet(
        onDismissRequest = { viewModel.dismissAddProker() },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        if (state.prokerId != null) R.string.sheet_edit_proker_title
                        else R.string.sheet_add_proker_title
                    ),
                    fontFamily = InstrumentSerif,
                    fontSize = 24.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.dismissAddProker() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = inkColor
                    )
                }
            }

            state.errorMessage?.let { error ->
                Text(text = error, color = Color(0xFFE0243B), style = MaterialTheme.typography.bodySmall)
            }

            // Nama Proker
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_proker_name),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateProkerName(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_proker_name_hint),
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

            // Divisi Selector
            if (detailState.divisions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.sheet_proker_division),
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val noneSelected = state.divisionId == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (noneSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else MaterialTheme.colorScheme.background)
                                .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateProkerDivision(null) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.sheet_proker_division_none),
                                style = MaterialTheme.typography.labelSmall,
                                color = inkColor
                            )
                        }

                        detailState.divisions.forEach { div ->
                            val isSelected = state.divisionId == div.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else MaterialTheme.colorScheme.background)
                                    .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateProkerDivision(div.id) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = div.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = inkColor
                                )
                            }
                        }
                    }
                }
            }

            // Deskripsi
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_proker_desc),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateProkerDesc(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_proker_desc_hint),
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
                    maxLines = 3
                )
            }

            // Status Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_proker_status),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf(
                        ProkerStatus.RENCANA to stringResource(R.string.status_rencana),
                        ProkerStatus.BERJALAN to stringResource(R.string.status_berjalan),
                        ProkerStatus.SELESAI to stringResource(R.string.status_selesai)
                    )
                    statuses.forEach { (st, label) ->
                        val isSelected = state.status == st
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SchedulyyTheme.extendedColors.pastelSky else MaterialTheme.colorScheme.background)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) inkColor else inkColor.copy(0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateProkerStatus(st) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = inkColor
                            )
                        }
                    }
                }
            }

            // Tanggal Mulai & Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    viewModel.updateProkerDates(LocalDate.of(y, m + 1, d), state.endDate)
                                },
                                state.startDate.year,
                                state.startDate.monthValue - 1,
                                state.startDate.dayOfMonth
                            ).show()
                        }
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sheet_proker_start),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            color = inkColor.copy(0.6f)
                        )
                        Text(
                            text = state.startDate.format(dateFmt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    viewModel.updateProkerDates(state.startDate, LocalDate.of(y, m + 1, d))
                                },
                                state.endDate.year,
                                state.endDate.monthValue - 1,
                                state.endDate.dayOfMonth
                            ).show()
                        }
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sheet_proker_end),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            color = inkColor.copy(0.6f)
                        )
                        Text(
                            text = state.endDate.format(dateFmt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                    .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                    .clickable { viewModel.saveProker() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.sheet_save_proker),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Sheet Tambah Kegiatan (Event) ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventSheet(
    viewModel: SpaceDetailViewModel
) {
    val state by viewModel.eventSheetState.collectAsState()
    val detailState by viewModel.uiState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val zone = ZoneId.systemDefault()
    val startZdt = state.start.atZone(zone)
    val endZdt = state.end.atZone(zone)
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
    val dateFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))

    val eventTypes = listOf(
        EventType.RAPAT to stringResource(R.string.event_type_rapat),
        EventType.PROKER to stringResource(R.string.event_type_proker),
        EventType.ACARA to stringResource(R.string.event_type_acara),
        EventType.DEADLINE to stringResource(R.string.event_type_deadline),
        EventType.LAINNYA to stringResource(R.string.event_type_lainnya)
    )

    ModalBottomSheet(
        onDismissRequest = { viewModel.dismissAddEvent() },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sheet_add_event_title),
                    fontFamily = InstrumentSerif,
                    fontSize = 24.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.dismissAddEvent() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = inkColor
                    )
                }
            }

            state.errorMessage?.let { error ->
                Text(text = error, color = Color(0xFFE0243B), style = MaterialTheme.typography.bodySmall)
            }

            // Judul Kegiatan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_event_title),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateEventTitle(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_event_title_hint),
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

            // Jenis Kegiatan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_event_type),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    eventTypes.forEach { (type, label) ->
                        val isSelected = state.type == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else MaterialTheme.colorScheme.background)
                                .border(1.dp, inkColor.copy(0.2f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateEventType(type) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = inkColor
                            )
                        }
                    }
                }
            }

            // Lokasi
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_event_location),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.location,
                    onValueChange = { viewModel.updateEventLocation(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_event_location_hint),
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

            // Waktu Mulai & Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tanggal & Jam Mulai
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val newStart = startZdt.withHour(h).withMinute(m).toInstant()
                                    viewModel.updateEventTimes(newStart, state.end)
                                },
                                startZdt.hour,
                                startZdt.minute,
                                true
                            ).show()
                        }
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sheet_event_start),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            color = inkColor.copy(0.6f)
                        )
                        Text(
                            text = "${startZdt.format(dateFmt)}, ${startZdt.format(timeFmt)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }

                // Jam Selesai
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val newEnd = endZdt.withHour(h).withMinute(m).toInstant()
                                    viewModel.updateEventTimes(state.start, newEnd)
                                },
                                endZdt.hour,
                                endZdt.minute,
                                true
                            ).show()
                        }
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sheet_event_end),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            color = inkColor.copy(0.6f)
                        )
                        Text(
                            text = endZdt.format(timeFmt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }
            }

            // Tautkan ke Proker
            if (detailState.prokers.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.sheet_event_proker),
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor.copy(alpha = 0.6f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val noneSelected = state.prokerId == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (noneSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else MaterialTheme.colorScheme.background)
                                .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateEventProker(null) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.sheet_event_proker_none),
                                style = MaterialTheme.typography.labelSmall,
                                color = inkColor
                            )
                        }

                        detailState.prokers.forEach { proker ->
                            val isSelected = state.prokerId == proker.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else MaterialTheme.colorScheme.background)
                                    .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateEventProker(proker.id) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = proker.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = inkColor
                                )
                            }
                        }
                    }
                }
            }

            // Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                    .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                    .clickable { viewModel.saveEvent() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.sheet_save_event),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
