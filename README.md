# Schedulyy

Aplikasi Android untuk manajemen jadwal perkuliahan, tugas akademik, dan kepanitiaan organisasi mahasiswa berbasis Jetpack Compose.

## Deskripsi

Schedulyy dirancang untuk membantu mahasiswa mengelola beban akademik dan aktivitas non-akademik dalam satu aplikasi terintegrasi. Aplikasi ini memisahkan konteks tugas pribadi, mata kuliah semester, dan divisi kepanitiaan menggunakan sistem folder (spaces), serta menyediakan integrasi pemindaian jadwal berbasis Google Gemini API untuk membaca jadwal dari gambar atau tangkapan layar.

## Fitur Utama

- Manajemen Jadwal Kuliah: Pencatatan mata kuliah per semester, penataan sesi jadwal mingguan, ruang kelas, dosen pengampu, serta deteksi bentrok jadwal otomatis.
- Manajemen Tugas dan Subtugas: Pengelompokan tugas berdasarkan tenggat waktu, prioritas, status (ongoing/selesai), dan subtugas bertingkat.
- Folder Organisasi dan Proker: Ruang khusus untuk organisasi atau kepanitiaan yang mendukung pembagian divisi, program kerja, dan agenda rapat.
- Pemindai Jadwal dan Tugas Cerdas: Ekstraksi teks dari tangkapan layar jadwal SIAKAD atau pesan grup menjadi entitas jadwal dan tugas secara otomatis menggunakan REST API Google Gemini.
- Sistem Notifikasi Lokal: Pengingat kelas otomatis 30 menit sebelum sesi dimulai dan pengingat deadline tugas H-1 pada pukul 19:00 WIB menggunakan Android WorkManager.
- Kustomisasi Profil dan Kartu Mahasiswa: Kartu identitas mahasiswa dinamis di halaman utama dengan pilihan tema warna pastel dan inisial kampus otomatis.
- Pencadangan dan Pemulihan Data: Ekspor dan impor seluruh data aplikasi (database Room) ke format file JSON secara lokal melalui Android Storage Access Framework.

## Arsitektur dan Teknologi

Aplikasi dibangun mengikuti panduan arsitektur modern Android (Modern Android Development / MAD):

- Bahasa: Kotlin
- UI Framework: Jetpack Compose (Material 3 + Custom Neo-Brutalist Pastel Design System)
- Pola Arsitektur: MVVM (Model-View-ViewModel) dengan Unidirectional Data Flow (UDF)
- Dependency Injection: Hilt (Dagger)
- Database Lokal: Room Database (SQLite) dengan KSP
- Asynchronous & State: Kotlin Coroutines & StateFlow
- Background Processing: AndroidX WorkManager
- Penyimpanan Preferensi & Kemanan: Jetpack DataStore Preferences dengan enkripsi Google Tink
- Jaringan HTTP: OkHttp 4 (REST client langsung ke Gemini API endpoint)
- Serialisasi Data: Kotlinx Serialization JSON

## Prasyarat Lingkungan Pengembangan

- Android Studio: Koala / Ladybug atau versi yang lebih baru
- JDK: Java 17
- Gradle: 8.9 (Gradle Wrapper disertakan)
- Android SDK Minimum: API Level 26 (Android 8.0 Oreo)
- Android SDK Target/Compile: API Level 34 (Android 14)

## Panduan Menjalankan Proyek

1. Clone repositori ini:
   ```bash
   git clone https://github.com/username/schedulyy.git
   cd schedulyy
   ```

2. Buka folder proyek di Android Studio.

3. Tunggu proses Gradle Sync selesai mengunduh semua dependensi.

4. Hubungkan perangkat Android fisik via USB Debugging atau jalankan Android Virtual Device (AVD).

5. Jalankan aplikasi melalui menu Run > Run 'app' atau melalui terminal:
   ```bash
   ./gradlew installDebug
   ```

## Penggunaan Fitur Gemini AI (Opsional)

Fitur pemindaian gambar jadwal kuliah dan tugas membutuhkan API key Google Gemini.

1. Dapatkan API key gratis melalui Google AI Studio (https://aistudio.google.com/).
2. Di dalam aplikasi Schedulyy, buka menu Pengaturan (ikon roda gigi di pojok kanan atas beranda).
3. Masukkan API key pada kolom yang tersedia dan simpan.
4. Kunci API disimpan secara terenkripsi di penyimpanan lokal perangkat menggunakan Google Tink dan dikirim langsung ke endpoint Google melalui header x-goog-api-key tanpa perantara server pihak ketiga.

## Struktur Direktori

```
app/src/main/
├── java/com/pemmob/schedulyy/
│   ├── data/
│   │   ├── backup/          # Logika ekspor dan impor JSON
│   │   ├── local/           # Room Database, DAO, dan Entitas
│   │   ├── network/         # Client REST Gemini dan DTO JSON
│   │   ├── notification/    # WorkManager, BootReceiver, dan Notification Helper
│   │   ├── repository/      # Implementasi interface repositori
│   │   └── security/        # Mesin enkripsi Google Tink
│   ├── di/                  # Hilt Dependency Injection Modules
│   ├── domain/              # Model data murni dan kontrak repositori
│   └── ui/
│       ├── components/      # Komponen antarmuka Compose yang dapat digunakan ulang
│       ├── navigation/      # NavHost, Bottom Navigation Bar, dan routing layar
│       ├── onboarding/      # Layar pengenalan 3 langkah saat instalasi baru
│       ├── scan/            # Layar pemindaian tangkapan layar jadwal dengan AI
│       ├── schedule/        # Layar timeline dan pengelolaan jadwal kuliah
│       ├── screens/         # Halaman Beranda (Home)
│       ├── settings/        # Layar Pengaturan, Profil Mahasiswa, dan API Key
│       ├── spaces/          # Layar hirarki Folder, Organisasi, dan Proker
│       ├── tasks/           # Layar daftar tugas dan subtugas
│       └── theme/           # Tipografi, palet warna pastel, dan styling aplikasi
└── res/                     # Asset ikon, font, dan strings lokalisasi Bahasa Indonesia
```
