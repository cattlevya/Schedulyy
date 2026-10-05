package com.pemmob.schedulyy.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono

@Composable
fun IpodScheduleWidget(
    courseName: String,
    roomAndTime: String,
    startTime: String,
    endTime: String,
    progress: Float = 0.45f,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onCenterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val skyColor = SchedulyyTheme.extendedColors.pastelSky
    val limeColor = SchedulyyTheme.extendedColors.pastelLime

    // Wadah Bevel Retro iPod
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE8E8EC), Color(0xFFD2D3D8))
                )
            )
            .border(width = 2.dp, color = inkColor, shape = RoundedCornerShape(24.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sisi Kiri: Layar Mini LCD iPod
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(124.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(width = 2.dp, color = inkColor, shape = RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Status LCD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.ipod_label_next),
                            fontFamily = SpaceMono,
                            fontSize = 8.sp,
                            color = inkColor.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(10.dp)
                            )
                            // Indikator Baterai
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(8.dp)
                                    .border(width = 1.dp, color = inkColor, shape = RoundedCornerShape(2.dp))
                                    .padding(1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(4.dp)
                                        .background(limeColor, shape = RoundedCornerShape(1.dp))
                                )
                            }
                        }
                    }

                    // Body Layar LCD (Matkul)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(skyColor)
                                .border(width = 1.5.dp, color = inkColor, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = courseName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = inkColor,
                                maxLines = 1
                            )
                            Text(
                                text = roomAndTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = inkColor.copy(alpha = 0.65f),
                                maxLines = 1
                            )
                        }
                    }

                    // Progress Scrubber Bar
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape)
                                .border(width = 1.dp, color = inkColor.copy(alpha = 0.3f), shape = CircleShape),
                            color = inkColor,
                            trackColor = Color(0xFFE5E5EA),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = startTime,
                                fontFamily = SpaceMono,
                                fontSize = 8.sp,
                                color = inkColor.copy(alpha = 0.6f)
                            )
                            Text(
                                text = endTime,
                                fontFamily = SpaceMono,
                                fontSize = 8.sp,
                                color = inkColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Sisi Kanan: Click-Wheel Controller
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(width = 2.dp, color = inkColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Label JADWAL di atas roda
                Text(
                    text = stringResource(R.string.nav_schedule).uppercase(),
                    fontFamily = SpaceMono,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp)
                )

                // Tombol Rewind di kiri
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                        .clickable(onClick = onPrevious)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = stringResource(R.string.cd_prev_schedule),
                        tint = inkColor.copy(alpha = 0.75f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Tombol Forward di kanan
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                        .clickable(onClick = onNext)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = stringResource(R.string.cd_next_schedule),
                        tint = inkColor.copy(alpha = 0.75f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Tombol Play/Pause di bawah roda
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = inkColor.copy(alpha = 0.75f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Tombol Tengah Click-Wheel
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F7))
                        .border(width = 1.dp, color = Color(0xFFD1D1D6), shape = CircleShape)
                        .clickable(onClick = onCenterClick),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1D1D6))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IpodScheduleWidgetPreview() {
    SchedulyyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            IpodScheduleWidget(
                courseName = "Basis Data",
                roomAndTime = "10:00 · R. 2.14 Lab",
                startTime = "08:00",
                endTime = "09:40"
            )
        }
    }
}
