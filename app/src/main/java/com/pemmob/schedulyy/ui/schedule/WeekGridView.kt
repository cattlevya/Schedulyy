package com.pemmob.schedulyy.ui.schedule

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.ClassSessionUiModel
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.LocalDate

@Composable
fun WeekGridView(
    weekSessions: Map<Int, List<ClassSessionUiModel>>,
    onSessionClick: (ClassSessionUiModel) -> Unit,
    onAddClick: () -> Unit,
    onDayHeaderClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val todayDow = LocalDate.now().dayOfWeek.value

    val days = listOf(
        1 to stringResource(R.string.day_mon_full),
        2 to stringResource(R.string.day_tue_full),
        3 to stringResource(R.string.day_wed_full),
        4 to stringResource(R.string.day_thu_full),
        5 to stringResource(R.string.day_fri_full),
        6 to stringResource(R.string.day_sat_full),
        7 to stringResource(R.string.day_sun_full)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        days.forEach { (dow, dayName) ->
            val daySessions = weekSessions[dow].orEmpty()
            val isToday = dow == todayDow

            item(key = "day_$dow") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Hari
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) { onDayHeaderClick(dow) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = dayName,
                                fontFamily = InstrumentSerif,
                                fontSize = 22.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) SchedulyyTheme.extendedColors.tintCobalt else inkColor
                            )
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SchedulyyTheme.extendedColors.tintCobalt.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.group_today),
                                        fontFamily = SpaceMono,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchedulyyTheme.extendedColors.tintCobalt
                                    )
                                }
                            }
                        }

                        // Jumlah sesi hari ini
                        Box(
                            modifier = Modifier
                                .background(inkColor.copy(alpha = 0.08f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${daySessions.size} kelas",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                color = inkColor.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Daftar Sesi Hari Ini atau Empty Box
                    if (daySessions.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            daySessions.forEach { session ->
                                ScheduleSessionCard(
                                    session = session,
                                    onClick = { onSessionClick(session) }
                                )
                            }
                        }
                    } else {
                        // Empty indicator untuk hari ini
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .border(1.dp, inkColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Tidak ada kelas",
                                style = MaterialTheme.typography.bodySmall,
                                color = inkColor.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}
