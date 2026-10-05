package com.pemmob.schedulyy.ui.schedule

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.ClassSessionUiModel
import com.pemmob.schedulyy.domain.model.ScheduleViewMode
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun getPastelColor(key: String): Color {
    val extended = SchedulyyTheme.extendedColors
    return when (key.lowercase(Locale.ROOT)) {
        "bubblegum", "pink" -> extended.pastelPink
        "butter", "yellow"  -> extended.pastelButter
        "mint", "green"     -> extended.pastelMint
        "sky", "blue"       -> extended.pastelSky
        "lilac", "purple"   -> extended.pastelLilac
        "lime"              -> extended.pastelLime
        else                -> extended.pastelSky
    }
}

// ─── Segmented Control (Hari / Minggu) ────────────────────────────────────────

@Composable
fun ScheduleSegmentedControl(
    selectedMode: ScheduleViewMode,
    onSelectMode: (ScheduleViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val periwinkle = SchedulyyTheme.extendedColors.btnPeriwinkle

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
            val modes = listOf(
                ScheduleViewMode.DAY to stringResource(R.string.schedule_tab_day),
                ScheduleViewMode.WEEK to stringResource(R.string.schedule_tab_week)
            )

            modes.forEach { (mode, title) ->
                val isSelected = selectedMode == mode
                val bg by animateColorAsState(
                    targetValue = if (isSelected) periwinkle else Color.Transparent,
                    label = "segment_bg"
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
                        .clickable(role = Role.Tab) { onSelectMode(mode) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = inkColor
                    )
                }
            }
        }
    }
}

// ─── Date Strip (Horizontal Week Strip) ───────────────────────────────────────

@Composable
fun DateStrip(
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val today = LocalDate.now()

    // Ambil Senin dari minggu tanggal yang terpilih
    val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val dayAbbr = when (date.dayOfWeek) {
                DayOfWeek.MONDAY    -> stringResource(R.string.day_mon_short)
                DayOfWeek.TUESDAY   -> stringResource(R.string.day_tue_short)
                DayOfWeek.WEDNESDAY -> stringResource(R.string.day_wed_short)
                DayOfWeek.THURSDAY  -> stringResource(R.string.day_thu_short)
                DayOfWeek.FRIDAY    -> stringResource(R.string.day_fri_short)
                DayOfWeek.SATURDAY  -> stringResource(R.string.day_sat_short)
                DayOfWeek.SUNDAY    -> stringResource(R.string.day_sun_short)
            }

            DateStripPill(
                dayAbbr = dayAbbr,
                dayNumber = date.dayOfMonth.toString(),
                isSelected = isSelected,
                isToday = isToday,
                onClick = { onSelectDate(date) }
            )
        }
    }
}

@Composable
private fun DateStripPill(
    dayAbbr: String,
    dayNumber: String,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val activeBg = SchedulyyTheme.extendedColors.btnPeriwinkle
    val cobalt = SchedulyyTheme.extendedColors.tintCobalt

    Box(
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 1.5.dp, y = 1.5.dp)
                    .background(inkColor, RoundedCornerShape(12.dp))
            )
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) activeBg else Color.White)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) inkColor else inkColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = dayAbbr,
                fontFamily = SpaceMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) cobalt else inkColor.copy(alpha = 0.7f)
            )
            Text(
                text = dayNumber,
                fontFamily = SpaceMono,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = inkColor
            )
            // Indikator hari ini (titik biru)
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(if (isToday) cobalt else Color.Transparent, CircleShape)
            )
        }
    }
}

// ─── Session Card (Timeline & Grid) ──────────────────────────────────────────

@Composable
fun ScheduleSessionCard(
    session: ClassSessionUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val cardColor = getPastelColor(session.colorKey)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .border(1.5.dp, inkColor, RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header bar: Waktu & SKS & Badge Bentrok
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${session.startTime.format(timeFormatter)} – ${session.endTime.format(timeFormatter)}",
                fontFamily = SpaceMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = inkColor.copy(alpha = 0.85f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (session.isConflict) {
                    ConflictBadge()
                }
                if (session.sks != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .border(1.dp, inkColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.schedule_sks_suffix, session.sks),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Nama Matkul
        Text(
            text = session.courseName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = inkColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Info Ruang & Dosen & Kode
        val details = listOfNotNull(
            session.room?.takeIf { it.isNotBlank() },
            session.courseCode?.takeIf { it.isNotBlank() },
            session.lecturer?.takeIf { it.isNotBlank() }
        ).joinToString(" • ")

        if (details.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = details,
                fontFamily = SpaceMono,
                fontSize = 10.sp,
                color = inkColor.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ConflictBadge(modifier: Modifier = Modifier) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val dangerColor = Color(0xFFFFB8B8) // Pastel Red

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(dangerColor)
            .border(1.dp, inkColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = Color(0xFFC00010),
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = stringResource(R.string.schedule_conflict_badge),
            fontFamily = SpaceMono,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleComponentsPreview() {
    SchedulyyTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScheduleSegmentedControl(
                selectedMode = ScheduleViewMode.DAY,
                onSelectMode = {}
            )
            DateStrip(
                selectedDate = LocalDate.now(),
                onSelectDate = {}
            )
            ScheduleSessionCard(
                session = ClassSessionUiModel(
                    sessionId = 1,
                    courseId = 1,
                    courseName = "Pemrograman Mobile",
                    courseCode = "IF301",
                    lecturer = "Dr. Budi Santoso",
                    room = "Lab 3",
                    sks = 3,
                    colorKey = "sky",
                    dayOfWeek = 1,
                    startTime = LocalTime.of(8, 0),
                    endTime = LocalTime.of(9, 40),
                    isConflict = true
                ),
                onClick = {}
            )
        }
    }
}
