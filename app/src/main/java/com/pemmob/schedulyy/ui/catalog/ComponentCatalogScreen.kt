package com.pemmob.schedulyy.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.components.FolderCard
import com.pemmob.schedulyy.ui.components.NeoBox
import com.pemmob.schedulyy.ui.components.NeoButton
import com.pemmob.schedulyy.ui.components.StatusChip
import com.pemmob.schedulyy.ui.components.TaskCheckbox
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentCatalogScreen(
    modifier: Modifier = Modifier
) {
    var selectedChipIndex by remember { mutableIntStateOf(0) }
    var task1Done by remember { mutableStateOf(false) }
    var task2Done by remember { mutableStateOf(false) }
    var task3Done by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_catalog),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.catalog_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bagian 1: Gaya Tombol
            item {
                CatalogSectionHeader(title = stringResource(R.string.catalog_section_buttons))
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeoButton(
                        text = stringResource(R.string.btn_primary),
                        onClick = {},
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    NeoButton(
                        text = stringResource(R.string.btn_secondary),
                        onClick = {},
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Bagian 2: Kartu Space Folder-Tab
            item {
                CatalogSectionHeader(title = stringResource(R.string.catalog_section_cards))
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    FolderCard(
                        title = stringResource(R.string.space_card_title_hmif),
                        subtitle = stringResource(R.string.space_card_subtitle_hmif),
                        folderColor = SchedulyyTheme.extendedColors.pastelLime
                    )
                    FolderCard(
                        title = stringResource(R.string.space_card_title_kuliah),
                        subtitle = stringResource(R.string.space_card_subtitle_kuliah),
                        folderColor = SchedulyyTheme.extendedColors.pastelSky
                    )
                    FolderCard(
                        title = stringResource(R.string.space_card_title_kepanitiaan),
                        subtitle = stringResource(R.string.space_card_subtitle_kepanitiaan),
                        folderColor = SchedulyyTheme.extendedColors.pastelLilac
                    )
                }
            }

            // Bagian 3: Filter & Status Chip
            item {
                CatalogSectionHeader(title = stringResource(R.string.catalog_section_chips))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(
                        label = stringResource(R.string.chip_all),
                        selected = selectedChipIndex == 0,
                        onClick = { selectedChipIndex = 0 }
                    )
                    StatusChip(
                        label = stringResource(R.string.chip_in_progress),
                        selected = selectedChipIndex == 1,
                        onClick = { selectedChipIndex = 1 }
                    )
                    StatusChip(
                        label = stringResource(R.string.chip_planned),
                        selected = selectedChipIndex == 2,
                        onClick = { selectedChipIndex = 2 }
                    )
                    StatusChip(
                        label = stringResource(R.string.chip_done),
                        selected = selectedChipIndex == 3,
                        onClick = { selectedChipIndex = 3 }
                    )
                }
            }

            // Bagian 4: Checkbox & Item Tugas
            item {
                CatalogSectionHeader(title = stringResource(R.string.catalog_section_tasks))
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CatalogTaskRow(
                        title = stringResource(R.string.task_sample_1),
                        subtitle = stringResource(R.string.task_sample_1_sub),
                        checked = task1Done,
                        onCheckedChange = { task1Done = it }
                    )
                    CatalogTaskRow(
                        title = stringResource(R.string.task_sample_2),
                        subtitle = stringResource(R.string.task_sample_2_sub),
                        checked = task2Done,
                        onCheckedChange = { task2Done = it }
                    )
                    CatalogTaskRow(
                        title = stringResource(R.string.task_sample_3),
                        subtitle = stringResource(R.string.task_sample_3_sub),
                        checked = task3Done,
                        onCheckedChange = { task3Done = it }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CatalogSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun CatalogTaskRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val borderColor = SchedulyyTheme.extendedColors.ink
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskCheckbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                contentDescription = title
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComponentCatalogPreview() {
    SchedulyyTheme {
        ComponentCatalogScreen()
    }
}
