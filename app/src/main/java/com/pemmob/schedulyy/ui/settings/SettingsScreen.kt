package com.pemmob.schedulyy.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.components.NeoButton
import com.pemmob.schedulyy.ui.settings.components.PrivacyNoticeCard
import com.pemmob.schedulyy.ui.settings.components.SettingsDivider
import com.pemmob.schedulyy.ui.settings.components.SettingsInsetGroup
import com.pemmob.schedulyy.ui.settings.components.SettingsRow
import com.pemmob.schedulyy.ui.settings.components.SettingsSectionHeader
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationsEnabled(isGranted)
    }

    val inkColor = SchedulyyTheme.extendedColors.ink
    val bgGrouped = Color(0xFFF2F2F7)

    var isEditingKey by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editNim by remember { mutableStateOf("") }
    var editMajor by remember { mutableStateOf("") }
    var editUniversity by remember { mutableStateOf("") }
    var editSemester by remember { mutableStateOf("") }
    var editCardColor by remember { mutableStateOf("pastelButter") }

    LaunchedEffect(showEditProfileDialog) {
        if (showEditProfileDialog) {
            editName = uiState.userSettings.studentName
            editNim = uiState.userSettings.nim
            editMajor = uiState.userSettings.major
            editUniversity = uiState.userSettings.university
            editSemester = uiState.userSettings.semesterName
            editCardColor = uiState.userSettings.cardColorKey
        }
    }

    // Export Document Launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val json = viewModel.exportData()
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray(Charsets.UTF_8))
                    }
                    snackbarHostState.showSnackbar("Data cadangan berhasil diekspor!")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Gagal mengekspor: ${e.message}")
                }
            }
        }
    }

    // Import Document Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader(Charsets.UTF_8).readText()
                    }
                    if (!json.isNullOrBlank()) {
                        viewModel.importData(json) { success, msg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Gagal memulihkan: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearInfoMessage()
        }
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Edit Kartu Mahasiswa",
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Mahasiswa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editNim,
                        onValueChange = { editNim = it },
                        label = { Text("NIM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editMajor,
                        onValueChange = { editMajor = it },
                        label = { Text("Jurusan / Prodi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editUniversity,
                        onValueChange = { editUniversity = it },
                        label = { Text("Universitas / Kampus") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editSemester,
                        onValueChange = { editSemester = it },
                        label = { Text("Semester") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Warna Kartu",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = inkColor
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "pastelButter" to SchedulyyTheme.extendedColors.pastelButter,
                            "pastelSky" to SchedulyyTheme.extendedColors.pastelSky,
                            "pastelMint" to SchedulyyTheme.extendedColors.pastelMint,
                            "pastelPink" to SchedulyyTheme.extendedColors.pastelPink
                        ).forEach { (key, color) ->
                            val isSelected = editCardColor == key
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = inkColor,
                                        shape = CircleShape
                                    )
                                    .clickable { editCardColor = key }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateProfile(
                            name = editName.trim().ifEmpty { "Mahasiswa" },
                            nim = editNim.trim().ifEmpty { "-" },
                            major = editMajor.trim().ifEmpty { "-" },
                            university = editUniversity.trim().ifEmpty { "-" },
                            semester = editSemester.trim().ifEmpty { "1" },
                            colorKey = editCardColor
                        )
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold, color = inkColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal", color = inkColor)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (uiState.showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowClearDataDialog(false) },
            title = {
                Text(
                    text = stringResource(R.string.settings_clear_data_confirm_title),
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_clear_data_confirm_desc),
                    color = SchedulyyTheme.extendedColors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearAllData() }
                ) {
                    Text(
                        text = stringResource(R.string.settings_clear_data_confirm_action),
                        color = SchedulyyTheme.extendedColors.danger,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowClearDataDialog(false) }) {
                    Text(text = "Batal", color = inkColor)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Top Bar dengan gaya iOS Large Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
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
                        text = stringResource(R.string.settings_title),
                        fontFamily = InstrumentSerif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
            }

            // Seksi 0: KARTU MAHASISWA
            item {
                SettingsSectionHeader(title = "KARTU MAHASISWA")

                val cardBg = when (uiState.userSettings.cardColorKey) {
                    "pastelPink" -> SchedulyyTheme.extendedColors.pastelPink
                    "pastelMint" -> SchedulyyTheme.extendedColors.pastelMint
                    "pastelSky" -> SchedulyyTheme.extendedColors.pastelSky
                    else -> SchedulyyTheme.extendedColors.pastelButter
                }

                SettingsInsetGroup {
                    SettingsRow(
                        title = uiState.userSettings.studentName,
                        subtitle = "${uiState.userSettings.nim} • ${uiState.userSettings.major}",
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(cardBg)
                                    .border(1.dp, inkColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Badge,
                                    contentDescription = null,
                                    tint = inkColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit Kartu",
                                tint = inkColor,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = { showEditProfileDialog = true }
                    )

                    SettingsDivider()

                    SettingsRow(
                        title = uiState.userSettings.university,
                        subtitle = "Semester ${uiState.userSettings.semesterName}",
                        onClick = { showEditProfileDialog = true }
                    )
                }
            }

            // Seksi 1: GEMINI AI
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_gemini))

                SettingsInsetGroup {
                    // API Key Row / Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Key,
                                    contentDescription = null,
                                    tint = inkColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.settings_api_key_label),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = inkColor
                                )
                            }

                            if (uiState.userSettings.hasApiKey && !isEditingKey) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SchedulyyTheme.extendedColors.pastelMint)
                                        .border(1.dp, inkColor, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_api_key_saved),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = inkColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.userSettings.hasApiKey && !isEditingKey) {
                            // Tampilan key tersimpan (Masked: ••••••••abcd)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF9F9FB))
                                    .border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = uiState.userSettings.maskedApiKey,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = inkColor
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Ganti",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SchedulyyTheme.extendedColors.tintCobalt
                                        ),
                                        modifier = Modifier
                                            .clickable { isEditingKey = true }
                                            .padding(4.dp)
                                    )
                                    Text(
                                        text = "Hapus",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SchedulyyTheme.extendedColors.danger
                                        ),
                                        modifier = Modifier
                                            .clickable { viewModel.deleteApiKey() }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        } else {
                            // Input field API Key
                            OutlinedTextField(
                                value = uiState.inputApiKey,
                                onValueChange = { viewModel.onApiKeyInputChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.settings_api_key_hint),
                                        fontSize = 13.sp,
                                        color = SchedulyyTheme.extendedColors.textSecondary
                                    )
                                },
                                visualTransformation = if (uiState.isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { viewModel.toggleApiKeyVisibility() }) {
                                        Icon(
                                            imageVector = if (uiState.isApiKeyVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = "Toggle visibility",
                                            tint = inkColor
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = inkColor,
                                    unfocusedBorderColor = Color(0xFFD1D1D6),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFF9F9FB)
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.saveApiKey()
                                    isEditingKey = false
                                })
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NeoButton(
                                    text = stringResource(R.string.settings_api_key_save),
                                    onClick = {
                                        viewModel.saveApiKey()
                                        isEditingKey = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    backgroundColor = SchedulyyTheme.extendedColors.pastelButter
                                )
                                if (isEditingKey) {
                                    NeoButton(
                                        text = "Batal",
                                        onClick = { isEditingKey = false },
                                        modifier = Modifier.weight(1f),
                                        backgroundColor = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tombol Tes Koneksi & Statusnya
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NeoButton(
                                text = stringResource(R.string.settings_test_connection),
                                onClick = { viewModel.testConnection() },
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = SchedulyyTheme.extendedColors.btnPeriwinkle
                            )
                        }

                        // Hasil Tes Koneksi
                        when (val state = uiState.testConnectionState) {
                            is TestConnectionUiState.Loading -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = inkColor
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_testing_connection),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = inkColor
                                    )
                                }
                            }
                            is TestConnectionUiState.Success -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SchedulyyTheme.extendedColors.pastelMint.copy(alpha = 0.5f))
                                        .border(1.dp, inkColor, RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = inkColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = inkColor
                                    )
                                }
                            }
                            is TestConnectionUiState.Error -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SchedulyyTheme.extendedColors.pastelPink.copy(alpha = 0.5f))
                                        .border(1.dp, inkColor, RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = SchedulyyTheme.extendedColors.danger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = SchedulyyTheme.extendedColors.danger
                                    )
                                }
                            }
                            is TestConnectionUiState.Idle -> {}
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Link Bantuan Google AI Studio
                        Row(
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://aistudio.google.com/apikey")
                                    )
                                    context.startActivity(intent)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_api_key_guide),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SchedulyyTheme.extendedColors.tintCobalt
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = null,
                                tint = SchedulyyTheme.extendedColors.tintCobalt,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    SettingsDivider()

                    // Pilihan Model Gemini
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.settings_model_label),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = inkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.availableModels) { model ->
                                val isSelected = !uiState.isCustomModelMode && uiState.userSettings.selectedModel == model
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) SchedulyyTheme.extendedColors.pastelButter else Color(0xFFF2F2F7))
                                        .border(1.5.dp, inkColor, RoundedCornerShape(20.dp))
                                        .clickable { viewModel.selectModel(model) }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = model,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        ),
                                        color = inkColor
                                    )
                                }
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (uiState.isCustomModelMode) SchedulyyTheme.extendedColors.pastelMint else Color(0xFFF2F2F7))
                                        .border(1.5.dp, inkColor, RoundedCornerShape(20.dp))
                                        .clickable { viewModel.enableCustomModelMode() }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_model_manual_option),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (uiState.isCustomModelMode) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        ),
                                        color = inkColor
                                    )
                                }
                            }
                        }

                        if (uiState.isCustomModelMode) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.customModelInput,
                                    onValueChange = { viewModel.onCustomModelInputChanged(it) },
                                    modifier = Modifier.weight(1f),
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.settings_model_manual_hint),
                                            fontSize = 13.sp,
                                            color = SchedulyyTheme.extendedColors.textSecondary
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = inkColor,
                                        unfocusedBorderColor = Color(0xFFD1D1D6),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color(0xFFF9F9FB)
                                    )
                                )
                                NeoButton(
                                    text = "Set",
                                    onClick = { viewModel.saveCustomModel() },
                                    backgroundColor = SchedulyyTheme.extendedColors.pastelMint
                                )
                            }
                        }
                    }

                    SettingsDivider()

                    // Catatan Privasi & Keamanan Data (§8)
                    PrivacyNoticeCard(
                        text = stringResource(R.string.settings_privacy_note),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Seksi 2: PENGINGAT & NOTIFIKASI
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_reminders))

                SettingsInsetGroup {
                    SettingsRow(
                        title = stringResource(R.string.settings_notif_enabled),
                        subtitle = "Kirim notifikasi untuk deadline dan kelas",
                        trailing = {
                            Switch(
                                checked = uiState.userSettings.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (!hasPermission) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            viewModel.setNotificationsEnabled(true)
                                        }
                                    } else {
                                        viewModel.setNotificationsEnabled(enabled)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SchedulyyTheme.extendedColors.tintCobalt,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE5E5EA)
                                )
                            )
                        }
                    )

                    SettingsDivider()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_reminder_default),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = inkColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val reminderOptions = listOf(
                            30 to stringResource(R.string.settings_reminder_30m),
                            60 to stringResource(R.string.settings_reminder_1h),
                            120 to stringResource(R.string.settings_reminder_2h),
                            1440 to stringResource(R.string.settings_reminder_1d)
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(reminderOptions) { (minutes, label) ->
                                val isSelected = uiState.userSettings.defaultReminderMinutes == minutes
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) SchedulyyTheme.extendedColors.pastelSky else Color(0xFFF2F2F7))
                                        .border(1.5.dp, inkColor, RoundedCornerShape(20.dp))
                                        .clickable { viewModel.setDefaultReminderMinutes(minutes) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        ),
                                        color = inkColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Seksi 3: DATA & PENYIMPANAN
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_data))

                SettingsInsetGroup {
                    SettingsRow(
                        title = stringResource(R.string.settings_export_json),
                        subtitle = "Cadangkan semua data ke file lokal .json",
                        trailing = {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            val filename = "schedulyy_backup_${LocalDate.now()}.json"
                            exportLauncher.launch(filename)
                        }
                    )

                    SettingsDivider()

                    SettingsRow(
                        title = stringResource(R.string.settings_import_json),
                        subtitle = "Pulihkan data dari cadangan JSON",
                        trailing = {
                            Icon(
                                imageVector = Icons.Outlined.FileUpload,
                                contentDescription = null,
                                tint = inkColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            importLauncher.launch(arrayOf("application/json"))
                        }
                    )

                    SettingsDivider()

                    SettingsRow(
                        title = stringResource(R.string.settings_clear_data),
                        subtitle = "Bersihkan database dan pengaturan lokal",
                        trailing = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = SchedulyyTheme.extendedColors.danger,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = { viewModel.setShowClearDataDialog(true) }
                    )
                }
            }

            // Seksi 4: TENTANG APLIKASI
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_about))

                SettingsInsetGroup {
                    SettingsRow(
                        title = stringResource(R.string.settings_app_name),
                        subtitle = stringResource(R.string.settings_app_version)
                    )
                    SettingsDivider()
                    SettingsRow(
                        title = "Dibuat untuk Mahasiswa Indonesia",
                        subtitle = "Jadwal kuliah, organisasi, proker, dan scan Gemini AI"
                    )
                    SettingsDivider()
                    SettingsRow(
                        title = "Panduan & Onboarding",
                        subtitle = "Buka kembali 3 kartu perkenalan & kustomisasi kartu",
                        trailing = {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = SchedulyyTheme.extendedColors.tintCobalt,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            viewModel.setOnboardingCompleted(false)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
