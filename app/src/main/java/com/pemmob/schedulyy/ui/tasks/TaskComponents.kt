package com.pemmob.schedulyy.ui.tasks

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
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.DeadlineGroup
import com.pemmob.schedulyy.domain.model.TaskPriority
import com.pemmob.schedulyy.domain.model.TaskStatus
import com.pemmob.schedulyy.domain.model.TaskUiModel
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ─── Grouped Section Header ──────────────────────────────────────────────────

@Composable
fun DeadlineGroupHeader(
    group: DeadlineGroup,
    count: Int,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val label = when (group) {
        DeadlineGroup.OVERDUE    -> stringResource(R.string.group_overdue)
        DeadlineGroup.TODAY      -> stringResource(R.string.group_today)
        DeadlineGroup.THIS_WEEK  -> stringResource(R.string.group_this_week)
        DeadlineGroup.LATER      -> stringResource(R.string.group_later)
        DeadlineGroup.NO_DEADLINE -> stringResource(R.string.group_no_deadline)
    }
    val accentColor = when (group) {
        DeadlineGroup.OVERDUE -> Color(0xFFE0243B)
        DeadlineGroup.TODAY   -> SchedulyyTheme.extendedColors.tintCobalt
        else -> inkColor.copy(alpha = 0.5f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontFamily = InstrumentSerif,
            fontSize = 22.sp,
            color = accentColor,
            fontWeight = FontWeight.Normal
        )
        Box(
            modifier = Modifier
                .background(accentColor.copy(alpha = 0.12f), CircleShape)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                fontFamily = SpaceMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

// ─── Task Row Card ───────────────────────────────────────────────────────────

@Composable
fun TaskRowCard(
    task: TaskUiModel,
    spaceName: String?,
    onToggleDone: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val cardBg by animateColorAsState(
        targetValue = if (task.isDone) Color(0xFFF7F7F7) else Color.White,
        animationSpec = spring(), label = "card_bg"
    )
    val checkBg by animateColorAsState(
        targetValue = if (task.isDone) SchedulyyTheme.extendedColors.pastelMint else Color.White,
        animationSpec = spring(), label = "check_bg"
    )
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH   -> Color(0xFFE0243B)
        TaskPriority.MEDIUM -> SchedulyyTheme.extendedColors.pastelButter
        TaskPriority.LOW    -> SchedulyyTheme.extendedColors.pastelMint
    }
    val deadlineText = task.deadline?.let { formatDeadline(it, task.hasTime) }

    Box(modifier = modifier.fillMaxWidth()) {
        // Hard shadow 2dp
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(inkColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg, RoundedCornerShape(14.dp))
                .border(1.5.dp, inkColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Priority strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )

            // Checkbox bulat 2dp border
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(checkBg)
                    .border(2.dp, inkColor, CircleShape)
                    .clickable { onToggleDone() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.cd_task_done),
                        tint = inkColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isDone) inkColor.copy(0.4f) else inkColor,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (deadlineText != null || spaceName != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = listOfNotNull(spaceName, deadlineText).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = inkColor.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Subtugas progress bar
                if (task.subTasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    SubTaskProgressBar(
                        progress = task.subTaskProgress,
                        doneCount = task.subTasks.count { it.done },
                        totalCount = task.subTasks.size
                    )
                }
            }
        }
    }
}

@Composable
private fun SubTaskProgressBar(
    progress: Float,
    doneCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val barWidth by animateFloatAsState(progress, label = "subtask_progress")
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subtask_progress_label),
                fontFamily = SpaceMono,
                fontSize = 8.sp,
                color = inkColor.copy(0.4f)
            )
            Text(
                text = "$doneCount/$totalCount",
                fontFamily = SpaceMono,
                fontSize = 8.sp,
                color = inkColor.copy(0.4f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(inkColor.copy(0.1f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidth.coerceIn(0f, 1f))
                    .height(3.dp)
                    .background(SchedulyyTheme.extendedColors.pastelMint, CircleShape)
            )
        }
    }
}

// ─── Space Filter Chips ───────────────────────────────────────────────────────

@Composable
fun SpaceFilterChips(
    spaces: List<com.pemmob.schedulyy.data.local.entity.SpaceEntity>,
    selectedSpaceId: Long?,
    onSelect: (Long?) -> Unit,
    onAddSpace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipItem(
            label = stringResource(R.string.filter_all),
            selected = selectedSpaceId == null,
            onClick = { onSelect(null) }
        )
        spaces.forEach { space ->
            FilterChipItem(
                label = space.name,
                selected = selectedSpaceId == space.id,
                onClick = { onSelect(space.id) }
            )
        }
        // Chip "+" untuk tambah Space baru
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, inkColor.copy(0.3f), CircleShape)
                .clickable { onAddSpace() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+ Folder",
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = inkColor.copy(0.5f)
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val bg by animateColorAsState(
        targetValue = if (selected) SchedulyyTheme.extendedColors.btnPeriwinkle else Color.White,
        label = "chip_bg"
    )
    Box {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 1.5.dp, y = 1.5.dp)
                    .background(inkColor, CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(bg)
                .border(1.5.dp, inkColor, CircleShape)
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = inkColor
            )
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("id"))
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
private fun formatDeadline(instant: Instant, hasTime: Boolean): String {
    val zone = ZoneId.systemDefault()
    val zdt = instant.atZone(zone)
    return if (hasTime) {
        "${zdt.format(dateFormatter)}, ${zdt.format(timeFormatter)}"
    } else {
        zdt.format(dateFormatter)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun TaskRowPreview() {
    SchedulyyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeadlineGroupHeader(group = DeadlineGroup.TODAY, count = 2)
            TaskRowCard(
                task = TaskUiModel(
                    id = 1,
                    spaceId = 1,
                    courseId = null,
                    title = "Laporan Praktikum Basis Data",
                    notes = null,
                    deadline = Instant.now(),
                    hasTime = true,
                    priority = TaskPriority.HIGH,
                    status = TaskStatus.TODO,
                    source = com.pemmob.schedulyy.domain.model.TaskSource.MANUAL,
                    assigneeLabel = null,
                    subTasks = emptyList(),
                    deadlineGroup = DeadlineGroup.TODAY,
                    createdAt = Instant.now(),
                    completedAt = null
                ),
                spaceName = "Kuliah",
                onToggleDone = {},
                onClick = {}
            )
        }
    }
}
