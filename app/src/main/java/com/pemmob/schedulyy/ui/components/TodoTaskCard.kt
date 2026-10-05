package com.pemmob.schedulyy.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@Composable
fun TodoTaskCard(
    title: String,
    subtitle: String,
    badgeColor: Color,
    badgeIcon: ImageVector? = null,
    badgeText: String? = null,
    done: Boolean,
    onToggleDone: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val cardShape = RoundedCornerShape(18.dp)

    val scaleCheck by animateFloatAsState(
        targetValue = if (done) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "task_check_scale"
    )

    val checkboxBg by animateColorAsState(
        targetValue = if (done) inkColor else Color.White,
        label = "task_check_bg"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 2.dp, color = inkColor, shape = cardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sisi Kiri: Badge Ikon Pastel + Judul & Subtitel
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge Pastel dengan border 2dp
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor)
                        .border(width = 2.dp, color = inkColor, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (badgeIcon != null) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (badgeText != null) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = inkColor
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else inkColor,
                        textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Sisi Kanan: Chunky Checkbox (w-7 h-7, border 2dp) dengan target sentuh 48dp
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        role = Role.Checkbox,
                        onClick = { onToggleDone(!done) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(checkboxBg)
                        .border(width = 2.dp, color = inkColor, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (scaleCheck > 0f) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .size(18.dp)
                                .scale(scaleCheck)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoTaskCardPreview() {
    SchedulyyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TodoTaskCard(
                title = "Proposal Sponsor Dies Natalis",
                subtitle = "Himpunan Mahasiswa · Besok, 16:00",
                badgeColor = SchedulyyTheme.extendedColors.pastelPink,
                badgeText = "🎸",
                done = false,
                onToggleDone = {}
            )
        }
    }
}
