package com.pemmob.schedulyy.ui.scan.components

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
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
    onScanClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onAddCourseClick: () -> Unit,
    onAddEventClick: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tambah Cepat",
                fontFamily = InstrumentSerif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = inkColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // 1. Pindai Screenshot dengan Gemini AI (Fitur Utama)
            QuickActionItem(
                icon = Icons.Outlined.CameraAlt,
                iconBg = SchedulyyTheme.extendedColors.btnPeriwinkle,
                title = "Pindai Jadwal & Tugas",
                subtitle = "Ekstrak otomatis dari screenshot SIAKAD, WA, atau LMS",
                badge = "Gemini AI",
                onClick = {
                    onDismiss()
                    onScanClick()
                }
            )

            // 2. Tugas Baru
            QuickActionItem(
                icon = Icons.Outlined.CheckCircleOutline,
                iconBg = SchedulyyTheme.extendedColors.pastelMint,
                title = "Tugas Baru",
                subtitle = "Catat tugas kuliah atau kepanitiaan baru",
                onClick = {
                    onDismiss()
                    onAddTaskClick()
                }
            )

            // 3. Sesi Kelas Baru
            QuickActionItem(
                icon = Icons.Outlined.CalendarMonth,
                iconBg = SchedulyyTheme.extendedColors.pastelButter,
                title = "Jadwal Kelas / Matkul",
                subtitle = "Input jadwal kuliah mingguan manual",
                onClick = {
                    onDismiss()
                    onAddCourseClick()
                }
            )

            // 4. Kegiatan Organisasi
            QuickActionItem(
                icon = Icons.Outlined.Groups,
                iconBg = SchedulyyTheme.extendedColors.pastelSky,
                title = "Kegiatan & Rapat",
                subtitle = "Jadwalkan rapat atau proker kepanitiaan",
                onClick = {
                    onDismiss()
                    onAddEventClick()
                }
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9FB))
            .border(1.5.dp, inkColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconBg)
                .border(1.5.dp, inkColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = inkColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = inkColor
                )

                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SchedulyyTheme.extendedColors.pastelLilac)
                            .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = inkColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = SchedulyyTheme.extendedColors.textSecondary
            )
        }
    }
}
