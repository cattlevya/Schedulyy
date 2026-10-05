package com.pemmob.schedulyy.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.ScheduleViewMode
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

import androidx.compose.material.icons.outlined.CameraAlt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    onNavigateToScan: () -> Unit = {},
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val courseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val semesterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ScheduleScreenContent(
        uiState = uiState,
        onSetViewMode = { viewModel.setViewMode(it) },
        onSetSelectedDate = { viewModel.setSelectedDate(it) },
        onOpenAddCourse = { viewModel.openAddCourse() },
        onOpenSemesterSheet = { viewModel.openSemesterSheet() },
        onOpenEditCourse = { viewModel.openEditCourse(it) },
        onNavigateToScan = onNavigateToScan,
        modifier = modifier
    )

    // Mount Modal Sheets
    AddCourseSheet(
        viewModel = viewModel,
        sheetState = courseSheetState
    )

    SemesterSheet(
        viewModel = viewModel,
        sheetState = semesterSheetState
    )
}

@Composable
fun ScheduleScreenContent(
    uiState: ScheduleUiState,
    onSetViewMode: (ScheduleViewMode) -> Unit,
    onSetSelectedDate: (java.time.LocalDate) -> Unit,
    onOpenAddCourse: () -> Unit,
    onOpenSemesterSheet: () -> Unit,
    onOpenEditCourse: (Long) -> Unit,
    onNavigateToScan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
            // ─── Header: Large Title "Jadwal" & Action "+" / Scan ───────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.schedule_title),
                        fontFamily = InstrumentSerif,
                        fontSize = 38.sp,
                        lineHeight = 40.sp,
                        color = inkColor
                    )
                    // Subtitle / Semester Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SchedulyyTheme.extendedColors.pastelButter.copy(alpha = 0.5f))
                            .border(1.dp, inkColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .clickable { onOpenSemesterSheet() }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = uiState.activeSemester?.name
                                ?: stringResource(R.string.schedule_no_semester),
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }

                // Action Buttons: Scan Screenshot + Tambah Manual
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Scan Jadwal
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SchedulyyTheme.extendedColors.pastelButter)
                            .border(2.dp, inkColor, CircleShape)
                            .clickable { onNavigateToScan() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = stringResource(R.string.scan_fab_tooltip),
                            tint = inkColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Tombol "+" Bulat iOS Neo-style
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                            .border(2.dp, inkColor, CircleShape)
                            .clickable { onOpenAddCourse() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.sheet_add_course_title),
                            tint = inkColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ─── Segmented Control: Hari / Minggu ───────────────────────────────
            ScheduleSegmentedControl(
                selectedMode = uiState.viewMode,
                onSelectMode = onSetViewMode
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ─── Content: Day vs Week ───────────────────────────────────────────
            when (uiState.viewMode) {
                ScheduleViewMode.DAY -> {
                    // Date Strip Horizontal
                    DateStrip(
                        selectedDate = uiState.selectedDate,
                        onSelectDate = onSetSelectedDate,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Timeline Vertikal
                    DayTimelineView(
                        sessions = uiState.daySessions,
                        selectedDate = uiState.selectedDate,
                        onSessionClick = { onOpenEditCourse(it.courseId) },
                        onAddClick = onOpenAddCourse,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ScheduleViewMode.WEEK -> {
                    WeekGridView(
                        weekSessions = uiState.weekSessions,
                        onSessionClick = { onOpenEditCourse(it.courseId) },
                        onAddClick = onOpenAddCourse,
                        onDayHeaderClick = { targetDow ->
                            val currentMonday = uiState.selectedDate.with(
                                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                            )
                            val targetDate = currentMonday.plusDays((targetDow - 1).toLong())
                            onSetSelectedDate(targetDate)
                            onSetViewMode(ScheduleViewMode.DAY)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ScheduleScreenPreview() {
    SchedulyyTheme {
        ScheduleScreenContent(
            uiState = ScheduleUiState(),
            onSetViewMode = {},
            onSetSelectedDate = {},
            onOpenAddCourse = {},
            onOpenSemesterSheet = {},
            onOpenEditCourse = {}
        )
    }
}
