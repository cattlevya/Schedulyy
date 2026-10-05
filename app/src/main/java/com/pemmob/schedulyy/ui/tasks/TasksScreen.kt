package com.pemmob.schedulyy.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.DeadlineGroup
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

private val GROUP_ORDER = listOf(
    DeadlineGroup.OVERDUE,
    DeadlineGroup.TODAY,
    DeadlineGroup.THIS_WEEK,
    DeadlineGroup.LATER,
    DeadlineGroup.NO_DEADLINE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inkColor = SchedulyyTheme.extendedColors.ink
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetBsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val spaceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val undoLabel = stringResource(R.string.action_undo)
    val doneMessage = stringResource(R.string.task_marked_done)
    LaunchedEffect(uiState.undoTask) {
        if (uiState.undoTask != null) {
            val result = snackbarHostState.showSnackbar(
                message = doneMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoComplete()
            } else {
                viewModel.dismissUndo()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Large Title + FAB
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.nav_tasks),
                        fontFamily = InstrumentSerif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Normal,
                        color = inkColor
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                            .border(2.dp, inkColor, CircleShape)
                            .clickable { viewModel.openAddSheet() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_fab_add),
                            tint = inkColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.setQuery(it) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = inkColor.copy(0.4f), modifier = Modifier.size(20.dp))
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.tasks_search_placeholder),
                            color = inkColor.copy(0.35f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = inkColor,
                        unfocusedBorderColor = inkColor.copy(0.2f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = inkColor,
                        unfocusedTextColor = inkColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Space Filter Chips (horizontal scroll)
            if (uiState.spaces.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpaceFilterChips(
                            spaces = uiState.spaces,
                            selectedSpaceId = uiState.filterSpaceId,
                            onSelect = { viewModel.setFilterSpace(it) },
                            onAddSpace = { viewModel.openSpaceSheet() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Empty State
            if (!uiState.isLoading && uiState.groupedTasks.isEmpty()) {
                item {
                    TasksEmptyState(onAddTask = { viewModel.openAddSheet() })
                }
            }

            // Grouped task list
            GROUP_ORDER.forEach { group ->
                val tasks = uiState.groupedTasks[group] ?: return@forEach
                if (tasks.isEmpty()) return@forEach

                item(key = "header_${group.name}") {
                    DeadlineGroupHeader(group = group, count = tasks.size)
                }

                items(items = tasks, key = { "task_${it.id}" }) { task ->
                    val spaceName = uiState.spaces.find { s -> s.id == task.spaceId }?.name
                    TaskRowCard(
                        task = task,
                        spaceName = spaceName,
                        onToggleDone = { viewModel.toggleDone(task) },
                        onClick = { viewModel.openEditSheet(task) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                item(key = "spacer_${group.name}") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Add/Edit Task Bottom Sheet
    AddEditTaskSheet(viewModel = viewModel, sheetState = sheetBsState)

    // Add Space/Folder Bottom Sheet
    AddSpaceSheet(viewModel = viewModel, sheetState = spaceSheetState)
}

@Composable
private fun TasksEmptyState(onAddTask: () -> Unit) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.tasks_empty_title),
            fontFamily = InstrumentSerif,
            fontSize = 28.sp,
            color = inkColor
        )
        Text(
            text = stringResource(R.string.tasks_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = inkColor.copy(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                .clickable { onAddTask() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.tasks_add_first),
                fontWeight = FontWeight.SemiBold,
                color = inkColor
            )
        }
    }
}
