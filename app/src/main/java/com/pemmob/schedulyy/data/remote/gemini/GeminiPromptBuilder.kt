package com.pemmob.schedulyy.data.remote.gemini

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object GeminiPromptBuilder {

    fun buildSystemPrompt(
        spaceName: String,
        existingCourses: List<String>
    ): String {
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val weekdayStr = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id"))
        val zone = ZoneId.systemDefault().id

        val coursesContext = if (existingCourses.isNotEmpty()) {
            "Matkul terdaftar: " + existingCourses.joinToString(", ")
        } else {
            "Belum ada matkul terdaftar."
        }

        val spaceContext = "Space tujuan: '$spaceName'. $coursesContext"

        return """
Kamu adalah asisten cerdas yang mengekstrak data jadwal kuliah dan tugas akademik dari screenshot mahasiswa Indonesia (SIAKAD, Google Calendar, chat WhatsApp/Telegram, LMS Moodle/Google Classroom).

Hari ini: $weekdayStr, $todayStr.
Zona waktu perangkat: $zone.
Konteks: $spaceContext.

Aturan Ekstraksi:
1. Ekstrak HANYA informasi yang benar-benar ada di gambar. Jangan mengarang data.
2. Jika ada informasi yang buram atau diragukan, beri confidence rendah (< 0.7) dan cantumkan catatan di array "warnings".
3. Hari kuliah (dayOfWeek): 1=Senin, 2=Selasa, 3=Rabu, 4=Kamis, 5=Jumat, 6=Sabtu, 7=Minggu.
4. Jam: Gunakan format 24 jam HH:mm (contoh: "07.30", "1 siang", "13:00 WIB" diubah menjadi "07:30" dan "13:00").
5. Tanggal relatif ("besok", "lusa", "Jumat depan", "H-3"):
   - Jika terlihat timestamp chat, hitung relatif terhadap waktu pesan tersebut.
   - Jika tidak terlihat, hitung relatif terhadap hari ini ($todayStr).
6. Tanggal tanpa tahun: Gunakan tahun terdekat di masa mendatang dari $todayStr.
7. Jam deadline: "23.59", "tengah malam", "sebelum ganti hari" diubah menjadi "23:59". Jika tidak ada jam yang disebut, isi deadlineTime dengan null (JANGAN isi 00:00).
8. Pisahkan tugas menjadi item terpisah jika ada beberapa tugas dalam satu gambar/pesan.
9. Abaikan obrolan basa-basi atau info di luar jadwal/tugas.
10. Cocokkan nama matkul dengan daftar di konteks jika nama serupa; jika matkul baru, tulis apa adanya secara rapi.
11. Isi "sourceSnippet" pada tugas dengan kutipan teks asli dari gambar sebagai bukti.

Format keluaran WAJIB berupa JSON murni dengan skema:
{
  "schedules": [
    {
      "courseName": "Nama Matkul",
      "courseCode": "Kode Matkul (opsional/null)",
      "dayOfWeek": 1,
      "startTime": "HH:mm",
      "endTime": "HH:mm (opsional/null)",
      "room": "Ruang kelas (opsional/null)",
      "lecturer": "Nama dosen (opsional/null)",
      "confidence": 0.95
    }
  ],
  "tasks": [
    {
      "title": "Judul tugas",
      "courseName": "Matkul terkait (opsional/null)",
      "description": "Deskripsi/instruksi (opsional/null)",
      "deadlineDate": "YYYY-MM-DD (opsional/null)",
      "deadlineTime": "HH:mm (opsional/null)",
      "priorityHint": "LOW / MEDIUM / HIGH",
      "sourceSnippet": "Kutipan teks asli",
      "confidence": 0.95
    }
  ],
  "warnings": [
    "Catatan atau keraguan ekstraksi (jika ada)"
  ]
}
        """.trimIndent()
    }
}
