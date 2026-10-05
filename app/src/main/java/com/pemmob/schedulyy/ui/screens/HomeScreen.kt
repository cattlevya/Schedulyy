package com.pemmob.schedulyy.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.components.IdCard
import com.pemmob.schedulyy.ui.components.IpodScheduleWidget
import com.pemmob.schedulyy.ui.components.TodoTaskCard
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.ui.schedule.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.pemmob.schedulyy.ui.schedule.ScheduleUiState

import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import com.pemmob.schedulyy.ui.scan.components.QuickActionSheet

import com.pemmob.schedulyy.ui.settings.SettingsViewModel
import com.pemmob.schedulyy.domain.repository.UserSettings

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scheduleState by scheduleViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    HomeScreenContent(
        modifier = modifier,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToScan = onNavigateToScan,
        scheduleState = scheduleState,
        userSettings = settingsState.userSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    scheduleState: ScheduleUiState = ScheduleUiState(),
    userSettings: UserSettings = UserSettings()
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val pinkColor = SchedulyyTheme.extendedColors.pastelPink
    val mintColor = SchedulyyTheme.extendedColors.pastelMint
    val butterColor = SchedulyyTheme.extendedColors.pastelButter

    var showQuickActionSheet by remember { mutableStateOf(false) }

    if (showQuickActionSheet) {
        QuickActionSheet(
            onDismiss = { showQuickActionSheet = false },
            onScanClick = onNavigateToScan,
            onAddTaskClick = { /* handoff */ },
            onAddCourseClick = { /* handoff */ },
            onAddEventClick = { /* handoff */ }
        )
    }

    val todayDateFormatted = remember {
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("id"))
        now.format(formatter)
    }

    var task1Done by remember { mutableStateOf(false) }
    var task2Done by remember { mutableStateOf(false) }
    var task3Done by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header: Editorial Greeting
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val firstName = userSettings.studentName.split(" ").firstOrNull()?.ifBlank { "Mahasiswa" } ?: "Mahasiswa"
                    Text(
                        text = "Hello, $firstName",
                        fontFamily = InstrumentSerif,
                        fontSize = 42.sp,
                        lineHeight = 44.sp,
                        color = inkColor
                    )
                    Text(
                        text = todayDateFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = inkColor.copy(alpha = 0.75f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tombol + (Quick Action)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                            .border(1.5.dp, inkColor, CircleShape)
                            .clickable { showQuickActionSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Cepat",
                            tint = inkColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Tombol Pengaturan
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, inkColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = inkColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // 2. Y2K Student ID Card
        item {
            val cardBgColor = when (userSettings.cardColorKey) {
                "pastelPink" -> pinkColor
                "pastelMint" -> mintColor
                "pastelSky" -> SchedulyyTheme.extendedColors.pastelSky
                "pastelButter" -> butterColor
                "pastelLilac" -> SchedulyyTheme.extendedColors.pastelLilac
                else -> Color.White
            }

            IdCard(
                name = userSettings.studentName,
                semester = scheduleState.activeSemester?.name ?: userSettings.semesterName,
                major = userSettings.major,
                nim = userSettings.nim,
                university = userSettings.university,
                cardColor = cardBgColor
            )
        }

        // Banner Fitur Scan Screenshot Gemini AI (§4.5)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SchedulyyTheme.extendedColors.pastelSky.copy(alpha = 0.5f))
                    .border(1.5.dp, inkColor, RoundedCornerShape(18.dp))
                    .clickable { onNavigateToScan() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, inkColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        tint = inkColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Scan Jadwal & Tugas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = inkColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SchedulyyTheme.extendedColors.pastelLilac)
                                .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Gemini AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = inkColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Foto jadwal SIAKAD atau chat WA untuk diekstrak otomatis",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = SchedulyyTheme.extendedColors.textSecondary
                    )
                }
            }
        }

        // 3. iPod Style Schedule Widget
        item {
            val nextSession = scheduleState.nextSessionToday
            val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

            if (nextSession != null) {
                val now = LocalTime.now()
                val totalMinutes = (nextSession.endTime.toSecondOfDay() - nextSession.startTime.toSecondOfDay()).toFloat()
                val elapsedMinutes = (now.toSecondOfDay() - nextSession.startTime.toSecondOfDay()).toFloat()
                val currentProgress = if (totalMinutes > 0) {
                    (elapsedMinutes / totalMinutes).coerceIn(0f, 1f)
                } else 0f

                IpodScheduleWidget(
                    courseName = nextSession.courseName,
                    roomAndTime = "${nextSession.startTime.format(timeFmt)} · ${nextSession.room ?: "Kelas"}",
                    startTime = nextSession.startTime.format(timeFmt),
                    endTime = nextSession.endTime.format(timeFmt),
                    progress = currentProgress
                )
            } else if (scheduleState.coursesWithSessions.isNotEmpty()) {
                IpodScheduleWidget(
                    courseName = stringResource(R.string.home_no_class_title),
                    roomAndTime = stringResource(R.string.home_no_class_sub),
                    startTime = "--:--",
                    endTime = "--:--",
                    progress = 1f
                )
            } else {
                IpodScheduleWidget(
                    courseName = "Basis Data",
                    roomAndTime = "10:00 · R. 2.14 Lab",
                    startTime = "08:00",
                    endTime = "09:40",
                    progress = 0.45f
                )
            }
        }

        // 4. To-do Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // To-do Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_section_todo),
                            fontFamily = InstrumentSerif,
                            fontSize = 32.sp,
                            lineHeight = 32.sp,
                            color = inkColor
                        )

                        // Pill Dropdown Ongoing
                        Box {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .offset(x = 1.5.dp, y = 1.5.dp)
                                    .background(inkColor, shape = CircleShape)
                            )
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(width = 2.dp, color = inkColor, shape = CircleShape)
                                    .clickable(onClick = {})
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.filter_ongoing),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = inkColor
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = inkColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Plus Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(inkColor)
                            .clickable(onClick = {}),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_fab_add),
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Task Cards
                TodoTaskCard(
                    title = "Proposal Sponsor Dies Natalis",
                    subtitle = "Himpunan Mahasiswa · Besok, 16:00",
                    badgeColor = pinkColor,
                    badgeText = "HM",
                    done = task1Done,
                    onToggleDone = { task1Done = it }
                )

                TodoTaskCard(
                    title = "Laporan praktikum Basis Data",
                    subtitle = "Basis Data · Hari ini, 23:59",
                    badgeColor = mintColor,
                    badgeText = "BD",
                    done = task2Done,
                    onToggleDone = { task2Done = it }
                )

                TodoTaskCard(
                    title = "Quiz Jaringan Komputer",
                    subtitle = "Lab 3 · Rabu, 10:00",
                    badgeColor = butterColor,
                    badgeText = "JK",
                    done = task3Done,
                    onToggleDone = { task3Done = it }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SchedulyyTheme {
        HomeScreenContent()
    }
}
