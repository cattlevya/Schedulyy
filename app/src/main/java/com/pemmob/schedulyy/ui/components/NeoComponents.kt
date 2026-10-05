package com.pemmob.schedulyy.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pemmob.schedulyy.ui.theme.FolderTabShape
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@Composable
fun NeoBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = SchedulyyTheme.extendedColors.ink,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 2.5.dp,
    shadowColor: Color = SchedulyyTheme.extendedColors.ink,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Shadow Keras
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = shadowColor, shape = shape)
        )
        // Lapisan Utama
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = shape)
                .border(width = borderWidth, color = borderColor, shape = shape)
        ) {
            content()
        }
    }
}

@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SchedulyyTheme.extendedColors.btnPeriwinkle,
    backgroundColor: Color = containerColor,
    contentColor: Color = SchedulyyTheme.extendedColors.ink,
    enabled: Boolean = true
) {
    val finalBg = if (backgroundColor != containerColor) backgroundColor else containerColor
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        NeoBox(
            shape = RoundedCornerShape(14.dp),
            backgroundColor = if (enabled) finalBg else finalBg.copy(alpha = 0.5f),
            shadowOffset = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FolderCard(
    title: String,
    subtitle: String,
    folderColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = remember { FolderTabShape(tabWidth = 110.dp, tabHeight = 22.dp, cornerRadius = 14.dp) }
    val borderColor = SchedulyyTheme.extendedColors.ink

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else Modifier
            )
    ) {
        // Shadow keras
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.5.dp, y = 2.5.dp)
                .background(color = borderColor, shape = shape)
        )
        // Kartu Utama Folder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = folderColor, shape = shape)
                .border(width = 2.dp, color = borderColor, shape = shape)
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 18.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = borderColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = borderColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = SchedulyyTheme.extendedColors.pastelLime
) {
    val borderColor = SchedulyyTheme.extendedColors.ink
    val bgColor by animateColorAsState(
        targetValue = if (selected) activeColor else MaterialTheme.colorScheme.surface,
        label = "chip_bg"
    )

    Surface(
        modifier = modifier
            .heightIn(min = 36.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Checkbox, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = borderColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TaskCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String
) {
    val borderColor = SchedulyyTheme.extendedColors.ink
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "tick_scale"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (checked) borderColor else MaterialTheme.colorScheme.surface
                )
                .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (scale > 0f) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(scale)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoComponentsPreview() {
    SchedulyyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NeoButton(text = "Tombol Periwinkle", onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            FolderCard(
                title = "HMIF 2026",
                subtitle = "Organisasi • 5 Proker",
                folderColor = SchedulyyTheme.extendedColors.pastelLime
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(label = "Berjalan", selected = true, onClick = {})
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(label = "Selesai", selected = false, onClick = {})
                Spacer(modifier = Modifier.width(16.dp))
                TaskCheckbox(checked = true, onCheckedChange = {}, contentDescription = "Selesai")
            }
        }
    }
}
