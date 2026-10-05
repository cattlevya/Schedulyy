package com.pemmob.schedulyy.ui.spaces

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.ui.components.FolderCard
import com.pemmob.schedulyy.ui.schedule.getPastelColor
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import com.pemmob.schedulyy.ui.theme.SpaceMono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreen(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpacesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    SpacesScreenContent(
        uiState = uiState,
        onNavigateUp = { viewModel.navigateUp() },
        onOpenCreate = { viewModel.openCreateSheet() },
        onSpaceClick = { space ->
            onNavigateToDetail(space.id)
        },
        modifier = modifier
    )

    CreateSpaceSheet(
        viewModel = viewModel,
        sheetState = createSheetState
    )
}

@Composable
fun SpacesScreenContent(
    uiState: SpacesUiState,
    onNavigateUp: () -> Unit,
    onOpenCreate: () -> Unit,
    onSpaceClick: (SpaceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.currentParent != null) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = inkColor
                            )
                        }
                    }
                    Column {
                        Text(
                            text = uiState.currentParent?.name ?: stringResource(R.string.spaces_title),
                            fontFamily = InstrumentSerif,
                            fontSize = 38.sp,
                            lineHeight = 40.sp,
                            color = inkColor
                        )
                        if (uiState.currentParent != null) {
                            Text(
                                text = "Sub-folder",
                                fontFamily = SpaceMono,
                                fontSize = 11.sp,
                                color = inkColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Plus Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SchedulyyTheme.extendedColors.btnPeriwinkle)
                        .border(2.dp, inkColor, CircleShape)
                        .clickable { onOpenCreate() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.sheet_add_space),
                        tint = inkColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (uiState.spaces.isEmpty()) {
                Box(
                    modifier = Modifier
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
                                .background(SchedulyyTheme.extendedColors.pastelButter)
                                .border(2.dp, inkColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.spaces_empty_title),
                            fontFamily = InstrumentSerif,
                            fontSize = 24.sp,
                            color = inkColor,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.spaces_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = inkColor.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.spaces, key = { it.space.id }) { item ->
                        val space = item.space
                        val folderColor = getPastelColor(space.colorKey)

                        val subtitle = buildString {
                            append(
                                when (space.type) {
                                    SpaceType.KULIAH -> stringResource(R.string.space_type_kuliah)
                                    SpaceType.ORGANISASI -> stringResource(R.string.space_type_organisasi)
                                    SpaceType.KEPANITIAAN -> stringResource(R.string.space_type_kepanitiaan)
                                    SpaceType.PRIBADI -> stringResource(R.string.space_type_pribadi)
                                    SpaceType.CUSTOM -> stringResource(R.string.space_type_custom)
                                }
                            )
                            if (item.subFolderCount > 0) {
                                append(" • ${item.subFolderCount} Sub-Folder")
                            }
                            if (item.prokerCount > 0) {
                                append(" • ${item.prokerCount} Proker")
                            }
                        }

                        FolderCard(
                            title = space.name,
                            subtitle = subtitle,
                            folderColor = folderColor,
                            onClick = { onSpaceClick(space) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpacesScreenPreview() {
    SchedulyyTheme {
        SpacesScreenContent(
            uiState = SpacesUiState(),
            onNavigateUp = {},
            onOpenCreate = {},
            onSpaceClick = {}
        )
    }
}
