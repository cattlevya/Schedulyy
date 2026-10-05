package com.pemmob.schedulyy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun IdCard(
    name: String,
    semester: String,
    major: String,
    nim: String,
    university: String = "Universitas Nusantara",
    academicYear: String = "2026/27",
    statusText: String = "MAHASISWA",
    cardColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val limeColor = SchedulyyTheme.extendedColors.pastelLime
    val cardShape = RoundedCornerShape(20.dp)

    // Inisial kampus
    val uniInitials = university.split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(3)
        .joinToString("")
        .ifEmpty { "UN" }

    Box(modifier = modifier.fillMaxWidth()) {
        // Hard shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(color = inkColor, shape = cardShape)
        )
        // Kartu utama
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = cardColor, shape = cardShape)
                .border(width = 2.dp, color = inkColor, shape = cardShape)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: logo kiri + tahun kanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo/Icon kampus
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5EA))
                            .border(1.5.dp, inkColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uniInitials,
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                    Column {
                        Text(
                            text = "UNIVERSITAS",
                            fontFamily = SpaceMono,
                            fontSize = 8.sp,
                            color = inkColor.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = university.uppercase(),
                            fontFamily = SpaceMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkColor,
                            maxLines = 1
                        )
                    }
                }

                // Tahun akademik badge
                Box(
                    modifier = Modifier
                        .background(limeColor, RoundedCornerShape(6.dp))
                        .border(1.dp, inkColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = academicYear,
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = inkColor.copy(alpha = 0.15f)
            )

            // Foto + Info identitas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Foto profil (portrait 3:4)
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE5E5EA))
                        .border(1.5.dp, inkColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.cd_profile_photo),
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Grid info 2x2
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoCell(
                            label = stringResource(R.string.id_label_name),
                            value = name,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCell(
                            label = stringResource(R.string.id_label_semester),
                            value = semester,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoCell(
                            label = stringResource(R.string.id_label_major),
                            value = major,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCell(
                            label = stringResource(R.string.id_label_nim),
                            value = nim,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = inkColor.copy(alpha = 0.15f)
            )

            // Footer: barcode + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Barcode
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(16.dp)
                ) {
                    listOf(3, 1, 4, 1, 5, 2, 3, 4, 2, 5, 1, 3, 2, 4, 1).forEachIndexed { i, w ->
                        Box(
                            modifier = Modifier
                                .width(w.dp)
                                .height(if (i % 3 == 0) 16.dp else 12.dp)
                                .background(inkColor)
                        )
                    }
                }

                // Status chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(limeColor)
                            .border(1.dp, inkColor, CircleShape)
                    )
                    Text(
                        text = statusText,
                        fontFamily = SpaceMono,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = SpaceMono,
            fontSize = 7.sp,
            color = inkColor.copy(alpha = 0.45f),
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = SpaceMono,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun IdCardPreview() {
    SchedulyyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            IdCard(
                name = "Dimas Pratama",
                semester = "5",
                major = "Informatika",
                nim = "2310101049"
            )
        }
    }
}
