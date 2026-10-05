package com.pemmob.schedulyy.ui.spaces

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.ProkerStatus
import com.pemmob.schedulyy.ui.components.FolderCard
import com.pemmob.schedulyy.ui.schedule.getPastelColor
import com.pemmob.schedulyy.ui.tasks.TaskRowCard
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono

@Composable
fun SpaceDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChildSpace: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpaceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val space = uiState.space ?: return
    val inkColor = SchedulyyTheme.extendedColors.ink

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = inkColor
                        )
                    }
                    Column {
                        Text(
                            text = space.name,
                            fontFamily = InstrumentSerif,
                            fontSize = 32.sp,
                            lineHeight = 34.sp,
                            color = inkColor
                        )
                        Text(
                            text = space.type.name,
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            color = inkColor.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Tab Bar: Ringkasan • Proker • Tugas • Jadwal
            SpaceTabBar(
                selectedTab = uiState.selectedTab,
                onSelectTab = { viewModel.selectTab(it) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Contents
            when (uiState.selectedTab) {
                SpaceTab.SUMMARY -> {
                    SummaryTabContent(
                        uiState = uiState,
                        onAddDivision = { viewModel.openAddDivision() },
                        onAddChildSpace = { /* bisa buka create sheet child */ },
                        onChildSpaceClick = onNavigateToChildSpace
                    )
                }
                SpaceTab.PROKER -> {
                    ProkerTabContent(
                        uiState = uiState,
                        onStatusFilter = { viewModel.filterProkerStatus(it) },
                        onCycleStatus = { viewModel.cycleProkerStatus(it) },
                        onEditProker = { viewModel.openEditProker(it) },
                        onAddProker = { viewModel.openAddProker() }
                    )
                }
                SpaceTab.TASKS -> {
                    TasksTabContent(
                        uiState = uiState,
                        onToggleTaskDone = { viewModel.toggleTaskDone(it) },
                        onAddTask = { viewModel.openAddTask() }
                    )
                }
                SpaceTab.SCHEDULE -> {
                    ScheduleTabContent(
                        uiState = uiState,
                        onAddEvent = { viewModel.openAddEvent() }
                    )
                }
            }
        }

        // Mount Sheets
        AddDivisionSheet(viewModel = viewModel)
        AddProkerSheet(viewModel = viewModel)
        AddEventSheet(viewModel = viewModel)
    }
}

// ─── 1. Ringkasan Tab ─────────────────────────────────────────────────────────

@Composable
private fun SummaryTabContent(
    uiState: SpaceDetailUiState,
    onAddDivision: () -> Unit,
    onAddChildSpace: () -> Unit,
    onChildSpaceClick: (Long) -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section Divisi
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.division_title),
                        fontFamily = InstrumentSerif,
                        fontSize = 22.sp,
                        color = inkColor
                    )
                    Text(
                        text = stringResource(R.string.add_division),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchedulyyTheme.extendedColors.tintCobalt,
                        modifier = Modifier.clickable { onAddDivision() }
                    )
                }

                if (uiState.divisions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.divisions.forEach { div ->
                            DivisionChip(division = div)
                        }
                    }
                } else {
                    Text(
                        text = "Belum ada divisi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = inkColor.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Section Sub-Folder / Kepanitiaan Anak
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.subfolder_title),
                        fontFamily = InstrumentSerif,
                        fontSize = 22.sp,
                        color = inkColor
                    )
                }

                if (uiState.childSpaces.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.childSpaces.forEach { child ->
                            FolderCard(
                                title = child.name,
                                subtitle = child.type.name,
                                folderColor = getPastelColor(child.colorKey),
                                onClick = { onChildSpaceClick(child.id) }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Belum ada kepanitiaan atau sub-folder.",
                        style = MaterialTheme.typography.bodySmall,
                        color = inkColor.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Statistik Ringkas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Proker",
                    value = "${uiState.prokers.size}",
                    bgColor = SchedulyyTheme.extendedColors.pastelSky,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tugas",
                    value = "${uiState.tasks.count { it.status != com.pemmob.schedulyy.domain.model.TaskStatus.DONE }}",
                    bgColor = SchedulyyTheme.extendedColors.pastelButter,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Kegiatan",
                    value = "${uiState.events.size}",
                    bgColor = SchedulyyTheme.extendedColors.pastelMint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    Box(modifier = modifier) {
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
                .background(bgColor)
                .border(1.5.dp, inkColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = SpaceMono,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = inkColor
            )
            Text(
                text = title,
                fontFamily = SpaceMono,
                fontSize = 10.sp,
                color = inkColor.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── 2. Proker Tab ───────────────────────────────────────────────────────────

@Composable
private fun ProkerTabContent(
    uiState: SpaceDetailUiState,
    onStatusFilter: (ProkerStatus?) -> Unit,
    onCycleStatus: (com.pemmob.schedulyy.data.local.entity.ProkerEntity) -> Unit,
    onEditProker: (com.pemmob.schedulyy.data.local.entity.ProkerEntity) -> Unit,
    onAddProker: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    val filteredProkers = if (uiState.selectedProkerStatusFilter != null) {
        uiState.prokers.filter { it.status == uiState.selectedProkerStatusFilter }
    } else uiState.prokers

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Filter Bar & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val isAll = uiState.selectedProkerStatusFilter == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAll) SchedulyyTheme.extendedColors.btnPeriwinkle else Color.White)
                            .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                            .clickable { onStatusFilter(null) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.filter_all),
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                            color = inkColor
                        )
                    }

                    listOf(
                        ProkerStatus.RENCANA to stringResource(R.string.status_rencana),
                        ProkerStatus.BERJALAN to stringResource(R.string.status_berjalan),
                        ProkerStatus.SELESAI to stringResource(R.string.status_selesai)
                    ).forEach { (st, label) ->
                        val isSelected = uiState.selectedProkerStatusFilter == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SchedulyyTheme.extendedColors.btnPeriwinkle else Color.White)
                                .border(1.dp, inkColor.copy(0.3f), RoundedCornerShape(8.dp))
                                .clickable { onStatusFilter(st) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = inkColor
                            )
                        }
                    }
                }

                // Add Proker Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                        .border(1.5.dp, inkColor, RoundedCornerShape(8.dp))
                        .clickable { onAddProker() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "+ Proker",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }
        }

        // List of Prokers
        if (filteredProkers.isNotEmpty()) {
            items(filteredProkers, key = { it.id }) { proker ->
                val divName = uiState.divisions.firstOrNull { it.id == proker.divisionId }?.name
                ProkerCard(
                    proker = proker,
                    divisionName = divName,
                    onStatusClick = { onCycleStatus(proker) },
                    onClick = { onEditProker(proker) }
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.proker_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = inkColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// ─── 3. Tugas Tab ────────────────────────────────────────────────────────────

@Composable
private fun TasksTabContent(
    uiState: SpaceDetailUiState,
    onToggleTaskDone: (com.pemmob.schedulyy.data.local.entity.TaskEntity) -> Unit,
    onAddTask: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.tasks.size} Tugas",
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                        .border(1.5.dp, inkColor, RoundedCornerShape(8.dp))
                        .clickable { onAddTask() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "+ Tugas",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }
        }

        if (uiState.tasks.isNotEmpty()) {
            items(uiState.tasks, key = { it.id }) { task ->
                val proker = uiState.prokers.firstOrNull { it.id == task.prokerId }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.5.dp, inkColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = inkColor
                            )
                            if (proker != null) {
                                Text(
                                    text = proker.name,
                                    fontFamily = SpaceMono,
                                    fontSize = 10.sp,
                                    color = SchedulyyTheme.extendedColors.tintCobalt
                                )
                            }
                        }

                        // Checkbox
                        com.pemmob.schedulyy.ui.components.TaskCheckbox(
                            checked = task.status == com.pemmob.schedulyy.domain.model.TaskStatus.DONE,
                            onCheckedChange = { onToggleTaskDone(task) },
                            contentDescription = task.title
                        )
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada tugas di folder ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = inkColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// ─── 4. Jadwal (Kegiatan) Tab ────────────────────────────────────────────────

@Composable
private fun ScheduleTabContent(
    uiState: SpaceDetailUiState,
    onAddEvent: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.events.size} Agenda Kegiatan",
                    fontFamily = SpaceMono,
                    fontSize = 11.sp,
                    color = inkColor.copy(alpha = 0.6f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                        .border(1.5.dp, inkColor, RoundedCornerShape(8.dp))
                        .clickable { onAddEvent() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "+ Kegiatan",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }
        }

        if (uiState.events.isNotEmpty()) {
            items(uiState.events, key = { it.id }) { event ->
                val proker = uiState.prokers.firstOrNull { it.id == event.prokerId }
                EventRowCard(
                    event = event,
                    prokerName = proker?.name
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.event_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = inkColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
