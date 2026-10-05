package com.pemmob.schedulyy.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.ClassSessionUiModel
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.LocalDate
import java.time.LocalTime

private const val START_HOUR = 7
private const val END_HOUR = 22
private val HOUR_HEIGHT = 68.dp

@Composable
fun DayTimelineView(
    sessions: List<ClassSessionUiModel>,
    selectedDate: LocalDate,
    onSessionClick: (ClassSessionUiModel) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val cobalt = SchedulyyTheme.extendedColors.tintCobalt
    val isToday = selectedDate == LocalDate.now()
    val now = LocalTime.now()

    if (sessions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SchedulyyTheme.extendedColors.pastelSky)
                        .border(2.dp, inkColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = inkColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.schedule_today_empty_title),
                    fontFamily = InstrumentSerif,
                    fontSize = 24.sp,
                    color = inkColor,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.schedule_today_empty_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = inkColor.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                // Tombol tambah jadwal
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                        .border(2.dp, inkColor, RoundedCornerShape(12.dp))
                        .clickable { onAddClick() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.schedule_empty_action_add),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }
            }
        }
        return
    }

    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 12.dp)
    ) {
        val totalHeight = (END_HOUR - START_HOUR + 1) * HOUR_HEIGHT.value
        val timeLabelWidth = 52.dp
        val availableCardWidth = maxWidth - timeLabelWidth - 16.dp

        Box(modifier = Modifier.fillMaxWidth().height(totalHeight.dp)) {
            // Garis jam & label waktu di sisi kiri
            Column(modifier = Modifier.fillMaxSize()) {
                for (hour in START_HOUR..END_HOUR) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HOUR_HEIGHT),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = String.format("%02d:00", hour),
                            fontFamily = SpaceMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = inkColor.copy(alpha = 0.45f),
                            modifier = Modifier.width(timeLabelWidth)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            thickness = 1.dp,
                            color = inkColor.copy(alpha = 0.08f)
                        )
                    }
                }
            }

            // Indikator Garis Waktu Sekarang (jika hari ini dan berada dalam rentang jam)
            if (isToday && now.hour in START_HOUR until END_HOUR) {
                val currentMinutes = (now.hour - START_HOUR) * 60 + now.minute
                val lineOffsetY = (currentMinutes / 60f) * HOUR_HEIGHT.value + 6f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = lineOffsetY.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = (timeLabelWidth.value - 6).dp)
                            .background(cobalt, CircleShape)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (timeLabelWidth.value).dp),
                        thickness = 2.dp,
                        color = cobalt
                    )
                }
            }

            // Render Blok Sesi Kelas
            sessions.forEach { session ->
                val startMinutes = (session.startTime.hour - START_HOUR) * 60 + session.startTime.minute
                val endMinutes = (session.endTime.hour - START_HOUR) * 60 + session.endTime.minute
                val durationMinutes = (endMinutes - startMinutes).coerceAtLeast(30)

                val blockOffsetY = (startMinutes / 60f) * HOUR_HEIGHT.value
                val blockHeight = (durationMinutes / 60f) * HOUR_HEIGHT.value

                val totalCols = session.totalConflicts
                val colIdx = session.conflictIndex
                val colGap = 4.dp
                val colWidth = if (totalCols > 1) {
                    (availableCardWidth - (colGap * (totalCols - 1))) / totalCols
                } else {
                    availableCardWidth
                }
                val blockOffsetX = timeLabelWidth + 8.dp + ((colWidth + colGap) * colIdx)

                Box(
                    modifier = Modifier
                        .width(colWidth)
                        .height(blockHeight.dp)
                        .offset(x = blockOffsetX, y = blockOffsetY.dp)
                        .padding(bottom = 4.dp)
                ) {
                    ScheduleSessionCard(
                        session = session,
                        onClick = { onSessionClick(session) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
