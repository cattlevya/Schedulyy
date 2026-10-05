package com.pemmob.schedulyy.ui.tasks

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

// Palet pastel yang tersedia untuk Space baru
private val SPACE_COLORS = listOf(
    "pastelPink"   to Color(0xFFFFB8D9),
    "pastelButter" to Color(0xFFFFE680),
    "pastelMint"   to Color(0xFFA8F0CF),
    "pastelSky"    to Color(0xFFA5D8FF),
    "pastelLilac"  to Color(0xFFD4B8FF),
    "pastelLime"   to Color(0xFFD8F77A)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpaceSheet(
    viewModel: TaskViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val state by viewModel.addSpaceState.collectAsState()
    if (!state.isVisible) return

    val inkColor = SchedulyyTheme.extendedColors.ink

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeSpaceSheet() },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(inkColor.copy(0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sheet_add_space),
                    fontFamily = InstrumentSerif,
                    fontSize = 26.sp,
                    color = inkColor
                )
                IconButton(onClick = { viewModel.closeSpaceSheet() }) {
                    Icon(Icons.Default.Close, null, tint = inkColor)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = inkColor.copy(0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Jenis Space (Segmented)
            SpaceSheetLabel(stringResource(R.string.sheet_space_type))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val row1 = listOf(SpaceType.PRIBADI, SpaceType.KULIAH)
                val row2 = listOf(SpaceType.ORGANISASI, SpaceType.KEPANITIAAN, SpaceType.CUSTOM)
                listOf(row1, row2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { type ->
                            val label = when (type) {
                                SpaceType.PRIBADI     -> stringResource(R.string.space_type_pribadi)
                                SpaceType.KULIAH      -> stringResource(R.string.space_type_kuliah)
                                SpaceType.ORGANISASI  -> stringResource(R.string.space_type_organisasi)
                                SpaceType.KEPANITIAAN -> stringResource(R.string.space_type_kepanitiaan)
                                SpaceType.CUSTOM      -> stringResource(R.string.space_type_custom)
                            }
                            val selected = state.type == type
                            val bg = if (selected) SchedulyyTheme.extendedColors.btnPeriwinkle else Color(0xFFF5F5F5)
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(bg)
                                    .border(if (selected) 2.dp else 1.dp, inkColor.copy(if (selected) 1f else 0.2f), CircleShape)
                                    .clickable { viewModel.updateSpaceType(type) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = inkColor
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Nama Space
            SpaceSheetLabel(stringResource(R.string.sheet_space_name))
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateSpaceName(it) },
                placeholder = {
                    Text(
                        stringResource(R.string.sheet_space_name_placeholder),
                        color = inkColor.copy(0.35f)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = inkColor,
                    unfocusedBorderColor = inkColor.copy(0.25f),
                    focusedTextColor = inkColor,
                    unfocusedTextColor = inkColor
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Pilih Warna
            SpaceSheetLabel(stringResource(R.string.sheet_space_color))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SPACE_COLORS.forEach { (key, color) ->
                    val selected = state.colorKey == key
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) inkColor else inkColor.copy(0.2f),
                                shape = CircleShape
                            )
                            .clickable { viewModel.updateSpaceColor(key) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            // Tombol Simpan
            val canSave = state.name.isNotBlank()
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(start = 3.dp, top = 3.dp)
                        .background(inkColor, RoundedCornerShape(14.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (canSave) SchedulyyTheme.extendedColors.btnPeriwinkle else Color(0xFFE5E5EA),
                            RoundedCornerShape(14.dp)
                        )
                        .border(2.dp, inkColor, RoundedCornerShape(14.dp))
                        .clickable(enabled = canSave) { viewModel.saveSpace() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.sheet_space_save),
                        fontWeight = FontWeight.SemiBold,
                        color = inkColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SpaceSheetLabel(text: String) {
    Text(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = SchedulyyTheme.extendedColors.ink.copy(alpha = 0.55f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
