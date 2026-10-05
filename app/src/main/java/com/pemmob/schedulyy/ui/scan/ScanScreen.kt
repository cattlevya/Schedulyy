package com.pemmob.schedulyy.ui.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.ui.components.NeoButton
import com.pemmob.schedulyy.ui.settings.components.NoApiKeyBottomSheet
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inkColor = SchedulyyTheme.extendedColors.ink
    val bgGrouped = Color(0xFFF2F2F7)

    var showNoKeySheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.onImagesSelected(uris)
        }
    }

    LaunchedEffect(uiState.saveSummaryMessage) {
        uiState.saveSummaryMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSummaryMessage()
        }
    }

    // Jika review data sudah tersedia, alihkan tampilan ke Layar Review
    if (uiState.reviewData != null) {
        ScanReviewScreen(
            reviewData = uiState.reviewData!!,
            targetSpaceName = uiState.selectedSpace?.name ?: "Kuliah",
            onToggleSchedule = { viewModel.toggleScheduleSelection(it) },
            onUpdateSchedule = { viewModel.updateScheduleCandidate(it) },
            onToggleTask = { viewModel.toggleTaskSelection(it) },
            onUpdateTask = { viewModel.updateTaskCandidate(it) },
            onSave = {
                viewModel.saveSelectedCandidates {
                    onNavigateBack()
                }
            },
            onCancel = { viewModel.cancelExtraction() }
        )
        return
    }

    if (showNoKeySheet) {
        NoApiKeyBottomSheet(
            onOpenSettings = {
                showNoKeySheet = false
                onNavigateToSettings()
            },
            onDismiss = { showNoKeySheet = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgGrouped
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = inkColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pindai Screenshot",
                        fontFamily = InstrumentSerif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }

            // Pilihan Space Tujuan
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.5.dp, inkColor, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = inkColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Simpan ke Folder",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = inkColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.activeSpaces) { space ->
                            val isSelected = uiState.selectedSpace?.id == space.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) SchedulyyTheme.extendedColors.pastelButter else Color(0xFFF2F2F7))
                                    .border(1.5.dp, inkColor, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.onSelectSpace(space) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = space.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = inkColor
                                )
                            }
                        }
                    }
                }
            }

            // Gallery / Preview Gambar
            item {
                if (uiState.processedImages.isEmpty()) {
                    // Box Kosong: Aksi Pilih Gambar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(2.dp, inkColor, RoundedCornerShape(20.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SchedulyyTheme.extendedColors.pastelSky)
                                    .border(1.5.dp, inkColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = inkColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Text(
                                text = "Pilih Screenshot Jadwal atau Tugas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = inkColor,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Mendukung SIAKAD, KRS, chat WA, LMS, Google Calendar. Maksimal 5 gambar.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = SchedulyyTheme.extendedColors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Daftar Thumbnail Gambar Terpilih
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.5.dp, inkColor, RoundedCornerShape(20.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Gambar Dipilih (${uiState.processedImages.size}/5)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = inkColor
                            )

                            if (uiState.processedImages.size < 5) {
                                Text(
                                    text = "+ Tambah",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SchedulyyTheme.extendedColors.tintCobalt
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(uiState.processedImages) { index, processed ->
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.5.dp, inkColor, RoundedCornerShape(12.dp))
                                ) {
                                    if (processed.previewBitmap != null) {
                                        Image(
                                            bitmap = processed.previewBitmap.asImageBitmap(),
                                            contentDescription = "Screenshot ${index + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFFE5E5EA)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.PhotoCamera,
                                                contentDescription = null,
                                                tint = inkColor
                                            )
                                        }
                                    }

                                    // Tombol Hapus Gambar
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(inkColor)
                                            .clickable { viewModel.onRemoveImage(index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Hapus gambar",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pesan Error jika ada
            item {
                if (uiState.errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SchedulyyTheme.extendedColors.pastelPink.copy(alpha = 0.5f))
                            .border(1.5.dp, inkColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = SchedulyyTheme.extendedColors.danger,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gagal Mengekstrak",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SchedulyyTheme.extendedColors.danger
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.errorMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = inkColor
                            )
                        }
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Tutup",
                                tint = inkColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Loading State yang Jujur (§4.5)
            item {
                if (uiState.isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.5.dp, inkColor, RoundedCornerShape(18.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            color = inkColor,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = uiState.loadingMessage,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = inkColor,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Gemini sedang menganalisis teks, tabel, jam, dan tanggal dari screenshot kamu…",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = SchedulyyTheme.extendedColors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        NeoButton(
                            text = "Batal",
                            onClick = { viewModel.cancelExtraction() },
                            modifier = Modifier.width(120.dp),
                            backgroundColor = Color.White
                        )
                    }
                }
            }

            // Tombol Eksekusi Pindai
            item {
                if (!uiState.isLoading && uiState.processedImages.isNotEmpty()) {
                    NeoButton(
                        text = "Mulai Pindai dengan Gemini AI",
                        onClick = {
                            if (!uiState.hasApiKey) {
                                showNoKeySheet = true
                            } else {
                                viewModel.startExtraction()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = SchedulyyTheme.extendedColors.btnPeriwinkle
                    )
                }
            }
        }
    }
}
