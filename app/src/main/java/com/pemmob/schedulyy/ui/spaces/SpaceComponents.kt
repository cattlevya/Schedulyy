package com.pemmob.schedulyy.ui.spaces

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.data.local.entity.DivisionEntity
import com.pemmob.schedulyy.data.local.entity.EventEntity
import com.pemmob.schedulyy.data.local.entity.ProkerEntity
import com.pemmob.schedulyy.domain.model.EventType
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.ui.schedule.getPastelColor
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ─── Tab Bar Detail Space (Ringkasan • Proker • Tugas • Jadwal) ───────────────

@Composable
fun SpaceTabBar(
    selectedTab: SpaceTab,
    onSelectTab: (SpaceTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val periwinkle = SchedulyyTheme.extendedColors.btnPeriwinkle

    val tabs = listOf(
        SpaceTab.SUMMARY to stringResource(R.string.tab_summary),
        SpaceTab.PROKER to stringResource(R.string.tab_proker),
        SpaceTab.TASKS to stringResource(R.string.tab_tasks),
        SpaceTab.SCHEDULE to stringResource(R.string.tab_schedule)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(2.dp, inkColor, RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { (tab, title) ->
                val isSelected = selectedTab == tab
                val bg by animateColorAsState(
                    targetValue = if (isSelected) periwinkle else Color.Transparent,
                    label = "tab_bg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .then(
                            if (isSelected) {
                                Modifier.border(1.5.dp, inkColor, RoundedCornerShape(10.dp))
                            } else Modifier
                        )
                        .clickable(role = Role.Tab) { onSelectTab(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = inkColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ─── Divisi Chip ─────────────────────────────────────────────────────────────

@Composable
fun DivisionChip(
    division: DivisionEntity,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val color = division.colorKey?.let { getPastelColor(it) } ?: SchedulyyTheme.extendedColors.pastelSky

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) color else color.copy(alpha = 0.35f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) inkColor else inkColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = division.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = inkColor
        )
    }
}

// ─── Proker Card ─────────────────────────────────────────────────────────────

@Composable
fun ProkerCard(
    proker: ProkerEntity,
    divisionName: String?,
    onStatusClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))

    val (statusLabel, statusBg) = when (proker.status) {
        ProkerStatus.RENCANA  -> stringResource(R.string.status_rencana) to SchedulyyTheme.extendedColors.pastelButter
        ProkerStatus.BERJALAN -> stringResource(R.string.status_berjalan) to SchedulyyTheme.extendedColors.pastelSky
        ProkerStatus.SELESAI  -> stringResource(R.string.status_selesai) to SchedulyyTheme.extendedColors.pastelLime
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        // Neo shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(inkColor, RoundedCornerShape(14.dp))
        )
        // Kartu Utama
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Divisi Tag
                Text(
                    text = divisionName ?: stringResource(R.string.sheet_proker_division_none),
                    fontFamily = SpaceMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )

                // Status Badge (dapat diklik untuk toggle status)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .border(1.5.dp, inkColor, RoundedCornerShape(6.dp))
                        .clickable { onStatusClick() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }

            // Nama Proker
            Text(
                text = proker.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = inkColor
            )

            // Deskripsi (opsional)
            proker.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = inkColor.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tanggal Rentang
            if (proker.startDate != null) {
                val dateStr = if (proker.endDate != null) {
                    "${proker.startDate.format(dateFormatter)} – ${proker.endDate.format(dateFormatter)}"
                } else {
                    proker.startDate.format(dateFormatter)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = inkColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = dateStr,
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        color = inkColor.copy(alpha = 0.65f)
                    )
                }
            }
        }
    }
}

// ─── Event (Kegiatan) Card ───────────────────────────────────────────────────

@Composable
fun EventRowCard(
    event: EventEntity,
    prokerName: String?,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val zone = ZoneId.systemDefault()
    val startZdt = event.start.atZone(zone)
    val endZdt = event.end?.atZone(zone)
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
    val dateFmt = DateTimeFormatter.ofPattern("d MMM", Locale("id"))

    val typeLabel = when (event.type) {
        EventType.RAPAT    -> stringResource(R.string.event_type_rapat)
        EventType.PROKER   -> stringResource(R.string.event_type_proker)
        EventType.ACARA    -> stringResource(R.string.event_type_acara)
        EventType.DEADLINE -> stringResource(R.string.event_type_deadline)
        EventType.LAINNYA  -> stringResource(R.string.event_type_lainnya)
    }

    val typeColor = when (event.type) {
        EventType.RAPAT    -> SchedulyyTheme.extendedColors.pastelSky
        EventType.PROKER   -> SchedulyyTheme.extendedColors.pastelMint
        EventType.ACARA    -> SchedulyyTheme.extendedColors.pastelPink
        EventType.DEADLINE -> SchedulyyTheme.extendedColors.pastelButter
        EventType.LAINNYA  -> SchedulyyTheme.extendedColors.pastelLilac
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(inkColor, RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.5.dp, inkColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tipe Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeColor)
                        .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = typeLabel,
                        fontFamily = SpaceMono,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }

                // Waktu Jam
                val timeStr = if (endZdt != null) {
                    "${startZdt.format(dateFmt)}, ${startZdt.format(timeFmt)} – ${endZdt.format(timeFmt)}"
                } else {
                    "${startZdt.format(dateFmt)}, ${startZdt.format(timeFmt)}"
                }
                Text(
                    text = timeStr,
                    fontFamily = SpaceMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = inkColor.copy(alpha = 0.7f)
                )
            }

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = inkColor
            )

            // Info Lokasi & Proker
            val metaList = listOfNotNull(
                event.location?.takeIf { it.isNotBlank() },
                prokerName?.takeIf { it.isNotBlank() }
            )
            if (metaList.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = inkColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = metaList.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = inkColor.copy(alpha = 0.65f)
                    )
                }
            }
        }
    }
}

// ─── Sheet Buat Space / Folder Baru dengan Template ──────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSpaceSheet(
    viewModel: SpacesViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val state by viewModel.createSheetState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink

    val templates = listOf(
        "HIMPUNAN" to stringResource(R.string.template_himpunan),
        "KEPANITIAAN" to stringResource(R.string.template_kepanitiaan),
        "UKM" to stringResource(R.string.template_ukm),
        "KOSONG" to stringResource(R.string.template_kosong)
    )

    val types = listOf(
        SpaceType.ORGANISASI to stringResource(R.string.space_type_organisasi),
        SpaceType.KEPANITIAAN to stringResource(R.string.space_type_kepanitiaan),
        SpaceType.KULIAH to stringResource(R.string.space_type_kuliah),
        SpaceType.CUSTOM to stringResource(R.string.space_type_custom)
    )

    val pastelColors = listOf(
        "mint" to SchedulyyTheme.extendedColors.pastelMint,
        "sky" to SchedulyyTheme.extendedColors.pastelSky,
        "butter" to SchedulyyTheme.extendedColors.pastelButter,
        "bubblegum" to SchedulyyTheme.extendedColors.pastelPink,
        "lilac" to SchedulyyTheme.extendedColors.pastelLilac,
        "lime" to SchedulyyTheme.extendedColors.pastelLime
    )

    ModalBottomSheet(
        onDismissRequest = { viewModel.dismissCreateSheet() },
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sheet_add_space),
                    fontFamily = InstrumentSerif,
                    fontSize = 26.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.dismissCreateSheet() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = inkColor
                    )
                }
            }

            // Error
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

            // Nama Folder
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_space_name),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sheet_space_name_placeholder),
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

            // Jenis Space
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_space_type),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { (type, label) ->
                        val isSelected = state.type == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle
                                    else MaterialTheme.colorScheme.background
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) inkColor else inkColor.copy(0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateType(type) }
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

            // Template Organisasi (§4.1)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.template_label),
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    templates.forEach { (key, label) ->
                        val isSelected = state.template == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) SchedulyyTheme.extendedColors.pastelSky
                                    else MaterialTheme.colorScheme.background
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) inkColor else inkColor.copy(0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateTemplate(key) }
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

            // Pilihan Warna
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.sheet_space_color),
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
                                .clickable { viewModel.updateColorKey(key) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                    .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                    .clickable { viewModel.saveSpace() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.sheet_space_save),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
