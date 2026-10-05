package com.pemmob.schedulyy.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.components.IdCard
import com.pemmob.schedulyy.ui.components.NeoButton
import com.pemmob.schedulyy.ui.theme.InstrumentSerif
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

@Composable
fun OnboardingScreen(
    onFinishOnboarding: (name: String, nim: String, major: String, university: String, semester: String, colorKey: String) -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val bgGrouped = Color(0xFFF2F2F7)

    var currentStep by remember { mutableIntStateOf(0) }

    // State Input Profil untuk Step 2
    var inputName by remember { mutableStateOf("Dimas Pratama") }
    var inputNim by remember { mutableStateOf("2310101049") }
    var inputMajor by remember { mutableStateOf("Informatika") }
    var inputUniversity by remember { mutableStateOf("Universitas Nusantara") }
    var inputSemester by remember { mutableStateOf("5") }
    var selectedCardColorKey by remember { mutableStateOf("pastelButter") }

    Scaffold(
        containerColor = bgGrouped
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Logo/Nama & Tombol Lewati
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Schedulyy",
                    fontFamily = InstrumentSerif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )

                if (currentStep < 2) {
                    TextButton(onClick = { currentStep = 2 }) {
                        Text(
                            text = "Lewati",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = SchedulyyTheme.extendedColors.textSecondary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Step Content dengan animasi transisi
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    label = "onboarding_step"
                ) { step ->
                    when (step) {
                        0 -> OnboardingStepWelcome()
                        1 -> OnboardingStepFeatures()
                        2 -> OnboardingStepProfile(
                            name = inputName,
                            onNameChange = { inputName = it },
                            nim = inputNim,
                            onNimChange = { inputNim = it },
                            major = inputMajor,
                            onMajorChange = { inputMajor = it },
                            university = inputUniversity,
                            onUniversityChange = { inputUniversity = it },
                            semester = inputSemester,
                            onSemesterChange = { inputSemester = it },
                            selectedColorKey = selectedCardColorKey,
                            onColorChange = { selectedCardColorKey = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indikator Titik (Progress Dots)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(3) { index ->
                    val isActive = index == currentStep
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 24.dp else 10.dp, 10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (isActive) inkColor
                                else Color(0xFFD1D1D6)
                            )
                    )
                }
            }

            // Tombol Navigasi Bawah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    TextButton(onClick = { currentStep-- }) {
                        Text(
                            text = "Kembali",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = inkColor
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }

                NeoButton(
                    text = if (currentStep == 2) "Mulai Sekarang" else "Lanjut",
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            onFinishOnboarding(
                                inputName.trim().ifEmpty { "Mahasiswa" },
                                inputNim.trim().ifEmpty { "0000000000" },
                                inputMajor.trim().ifEmpty { "Program Studi" },
                                inputUniversity.trim().ifEmpty { "Universitas" },
                                inputSemester.trim().ifEmpty { "1" },
                                selectedCardColorKey
                            )
                        }
                    },
                    backgroundColor = if (currentStep == 2) SchedulyyTheme.extendedColors.pastelMint else SchedulyyTheme.extendedColors.btnPeriwinkle
                )
            }
        }
    }
}

@Composable
private fun OnboardingStepWelcome() {
    val inkColor = SchedulyyTheme.extendedColors.ink

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Visual Card Ilustrasi
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(SchedulyyTheme.extendedColors.pastelButter)
                .border(2.dp, inkColor, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = inkColor,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = "SCHEDULYY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = inkColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Kelola Kuliah &\nOrganisasi Tanpa Ribet",
            fontFamily = InstrumentSerif,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Selamat datang di Schedulyy! Dirancang khusus dengan estetika neo-pastel yang rapi untuk membantumu mencatat jadwal, menyelesaikan tugas, hingga mengurus proker organisasi.",
            style = MaterialTheme.typography.bodyMedium,
            color = SchedulyyTheme.extendedColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun OnboardingStepFeatures() {
    val inkColor = SchedulyyTheme.extendedColors.ink

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Fitur Andalan Kamu",
            fontFamily = InstrumentSerif,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FeatureItem(
                icon = Icons.Outlined.CalendarMonth,
                title = "Timeline Jadwal Cerdas",
                description = "Lihat kelas hari ini dengan format visual iPod, deteksi bentrok, dan pengingat kelas otomatis 30 menit sebelum sesi.",
                bgColor = SchedulyyTheme.extendedColors.pastelSky
            )

            FeatureItem(
                icon = Icons.Outlined.CheckCircleOutline,
                title = "Manajemen Tugas & H-1",
                description = "Lacak deadline tugas dengan subtugas bertingkat dan pengingat pintar H-1 pukul 19:00 sebelum deadline.",
                bgColor = SchedulyyTheme.extendedColors.pastelMint
            )

            FeatureItem(
                icon = Icons.Outlined.FolderSpecial,
                title = "Folder Organisasi & Proker",
                description = "Bagi hirarki ruang untuk Himpunan, BEM, UKM, atau Panitia dengan divisi, proker, dan jadwal rapat terpadu.",
                bgColor = SchedulyyTheme.extendedColors.pastelButter
            )

            FeatureItem(
                icon = Icons.Outlined.AutoAwesome,
                title = "Scan AI Screenshot",
                description = "Foto jadwal dari SIAKAD atau pesan WA tugas, diekstrak instan menjadi agenda teratur memakai Gemini AI.",
                bgColor = SchedulyyTheme.extendedColors.pastelPink
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    bgColor: Color
) {
    val inkColor = SchedulyyTheme.extendedColors.ink

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.5.dp, inkColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(1.dp, inkColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = inkColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = inkColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SchedulyyTheme.extendedColors.textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun OnboardingStepProfile(
    name: String,
    onNameChange: (String) -> Unit,
    nim: String,
    onNimChange: (String) -> Unit,
    major: String,
    onMajorChange: (String) -> Unit,
    university: String,
    onUniversityChange: (String) -> Unit,
    semester: String,
    onSemesterChange: (String) -> Unit,
    selectedColorKey: String,
    onColorChange: (String) -> Unit
) {
    val inkColor = SchedulyyTheme.extendedColors.ink
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kartu Mahasiswa Kamu",
            fontFamily = InstrumentSerif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Sesuaikan kartu identitas yang akan tampil di Beranda",
            style = MaterialTheme.typography.bodySmall,
            color = SchedulyyTheme.extendedColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pratinjau IdCard Realtime
        val previewBg = when (selectedColorKey) {
            "pastelPink" -> SchedulyyTheme.extendedColors.pastelPink
            "pastelMint" -> SchedulyyTheme.extendedColors.pastelMint
            "pastelSky" -> SchedulyyTheme.extendedColors.pastelSky
            "pastelButter" -> SchedulyyTheme.extendedColors.pastelButter
            "pastelLilac" -> SchedulyyTheme.extendedColors.pastelLilac
            else -> Color.White
        }

        IdCard(
            name = name.ifBlank { "Nama Mahasiswa" },
            semester = semester.ifBlank { "1" },
            major = major.ifBlank { "Program Studi" },
            nim = nim.ifBlank { "NIM" },
            university = university.ifBlank { "Universitas" },
            cardColor = previewBg,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pilihan Warna Kartu
        val colorOptions = listOf(
            "pastelButter" to SchedulyyTheme.extendedColors.pastelButter,
            "pastelSky" to SchedulyyTheme.extendedColors.pastelSky,
            "pastelMint" to SchedulyyTheme.extendedColors.pastelMint
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colorOptions.forEach { (key, color) ->
                val isSelected = selectedColorKey == key
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
                        .clickable { onColorChange(key) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Fields
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nama Lengkap") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inkColor,
                unfocusedBorderColor = Color(0xFFD1D1D6),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = nim,
                onValueChange = onNimChange,
                label = { Text("NIM") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = inkColor,
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            OutlinedTextField(
                value = semester,
                onValueChange = onSemesterChange,
                label = { Text("Semester") },
                singleLine = true,
                modifier = Modifier.weight(0.7f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = inkColor,
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = major,
            onValueChange = onMajorChange,
            label = { Text("Jurusan / Program Studi") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inkColor,
                unfocusedBorderColor = Color(0xFFD1D1D6),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = university,
            onValueChange = onUniversityChange,
            label = { Text("Perguruan Tinggi / Universitas") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inkColor,
                unfocusedBorderColor = Color(0xFFD1D1D6),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}
