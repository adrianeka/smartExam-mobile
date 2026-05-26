package com.p79smartexam.smartexam.database

import com.p79smartexam.smartexam.model.Soal

object DummyData {

    val emptyDummyData = emptyList<Soal>()

    val normalDummyData = listOf(
        // --- 10 SOAL PILIHAN GANDA (tipeSoal = 1) ---
        Soal(
            id = 1L,
            soal = "Apa fungsi dari 'Room' pada Android?",
            tipeSoal = 1,
            pilihan = "Database ~ Networking ~ UI Design ~ Testing"
        ),
        Soal(
            id = 2L,
            soal = "Bahasa resmi untuk pengembangan Android saat ini adalah...",
            tipeSoal = 1,
            pilihan = "Java ~ Kotlin ~ C++ ~ Python"
        ),
        Soal(
            id = 3L,
            soal = "Komponen yang digunakan untuk background process di Android?",
            tipeSoal = 1,
            pilihan = "Activity ~ Broadcast Receiver ~ WorkManager ~ Fragment"
        ),
        Soal(
            id = 4L,
            soal = "Format data yang umum digunakan saat sinkronisasi API?",
            tipeSoal = 1,
            pilihan = "XML ~ JSON ~ HTML ~ TXT"
        ),
        Soal(
            id = 5L,
            soal = "Annotation Room untuk mendefinisikan tabel adalah...",
            tipeSoal = 1,
            pilihan = "@Table ~ @Entity ~ @Database ~ @Column"
        ),
        Soal(
            id = 6L,
            soal = "State awal dalam siklus hidup Activity adalah...",
            tipeSoal = 1,
            pilihan = "onStart ~ onResume ~ onCreate ~ onPause"
        ),
        Soal(
            id = 7L,
            soal = "Jetpack Compose digunakan untuk...",
            tipeSoal = 1,
            pilihan = "UI Deklaratif ~ Local Database ~ Dependency Injection ~ Unit Test"
        ),
        Soal(
            id = 8L,
            soal = "Library untuk memproses gambar di Android?",
            tipeSoal = 1,
            pilihan = "Retrofit ~ Glide ~ Room ~ Dagger"
        ),
        Soal(
            id = 9L,
            soal = "Metode HTTP untuk mengirim data baru ke server?",
            tipeSoal = 1,
            pilihan = "GET ~ DELETE ~ POST ~ UPDATE"
        ),
        Soal(
            id = 10L,
            soal = "Fungsi dari Primary Key di database?",
            tipeSoal = 1,
            pilihan = "Data ganda ~ Identitas unik ~ Format teks ~ Keamanan"
        ),

        // --- 5 SOAL ESSAY (tipeSoal = 2) ---
        Soal(
            id = 11L,
            soal = "Jelaskan perbedaan antara SQLite dan Room!",
            tipeSoal = 2,
        ),
        Soal(
            id = 12L,
            soal = "Apa yang dimaksud dengan Offline-first App?",
            tipeSoal = 2,
        ),
        Soal(
            id = 13L,
            soal = "Bagaimana cara kerja WorkManager dalam sinkronisasi data?",
            tipeSoal = 2,
        ),
        Soal(
            id = 14L,
            soal = "Mengapa kita membutuhkan TypeConverter di Room?",
            tipeSoal = 2,
        ),
        Soal(
            id = 15L,
            soal = "Sebutkan manfaat menggunakan arsitektur MVVM!",
            tipeSoal = 2,
        )
    )

    val answeredDummyData = listOf(
        // --- 10 SOAL PILIHAN GANDA (tipeSoal = 1) ---
        Soal(
            id = 1L,
            soal = "Apa fungsi dari 'Room' pada Android?",
            tipeSoal = 1,
            pilihan = "Database ~ Networking ~ UI Design ~ Testing",
            jawaban = "Database"
        ),
        Soal(
            id = 2L,
            soal = "Bahasa resmi untuk pengembangan Android saat ini adalah...",
            tipeSoal = 1,
            pilihan = "Java ~ Kotlin ~ C++ ~ Python",
            jawaban = "Kotlin"
        ),
        Soal(
            id = 3L,
            soal = "Komponen yang digunakan untuk background process di Android?",
            tipeSoal = 1,
            pilihan = "Activity ~ Broadcast Receiver ~ WorkManager ~ Fragment",
            jawaban = "WorkManager"
        ),
        Soal(
            id = 4L,
            soal = "Format data yang umum digunakan saat sinkronisasi API?",
            tipeSoal = 1,
            pilihan = "XML ~ JSON ~ HTML ~ TXT",
            jawaban = "JSON"
        ),
        Soal(
            id = 5L,
            soal = "Annotation Room untuk mendefinisikan tabel adalah...",
            tipeSoal = 1,
            pilihan = "@Table ~ @Entity ~ @Database ~ @Column",
            jawaban = "@Entity"
        ),
        Soal(
            id = 6L,
            soal = "State awal dalam siklus hidup Activity adalah...",
            tipeSoal = 1,
            pilihan = "onStart ~ onResume ~ onCreate ~ onPause",
            jawaban = "onCreate"
        ),
        Soal(
            id = 7L,
            soal = "Jetpack Compose digunakan untuk...",
            tipeSoal = 1,
            pilihan = "UI Deklaratif ~ Local Database ~ Dependency Injection ~ Unit Test",
            jawaban = "UI Deklaratif"
        ),
        Soal(
            id = 8L,
            soal = "Library untuk memproses gambar di Android?",
            tipeSoal = 1,
            pilihan = "Retrofit ~ Glide ~ Room ~ Dagger",
            jawaban = "Glide"
        ),
        Soal(
            id = 9L,
            soal = "Metode HTTP untuk mengirim data baru ke server?",
            tipeSoal = 1,
            pilihan = "GET ~ DELETE ~ POST ~ UPDATE",
            jawaban = "POST"
        ),
        Soal(
            id = 10L,
            soal = "Fungsi dari Primary Key di database?",
            tipeSoal = 1,
            pilihan = "Data ganda ~ Identitas unik ~ Format teks ~ Keamanan",
            jawaban = "Identitas unik"
        ),

        // --- 5 SOAL ESSAY (tipeSoal = 2) ---
        Soal(
            id = 11L,
            soal = "Jelaskan perbedaan antara SQLite dan Room!",
            tipeSoal = 2,
            jawaban = "Room adalah abstraction layer di atas SQLite yang memudahkan pengelolaan database."
        ),
        Soal(
            id = 12L,
            soal = "Apa yang dimaksud dengan Offline-first App?",
            tipeSoal = 2,
            jawaban = "Aplikasi yang tetap berfungsi penuh tanpa koneksi internet menggunakan storage lokal."
        ),
        Soal(
            id = 13L,
            soal = "Bagaimana cara kerja WorkManager dalam sinkronisasi data?",
            tipeSoal = 2,
            jawaban = "Menjadwalkan tugas di latar belakang yang tetap berjalan meski aplikasi ditutup."
        ),
        Soal(
            id = 14L,
            soal = "Mengapa kita membutuhkan TypeConverter di Room?",
            tipeSoal = 2,
            jawaban = "Untuk menyimpan tipe data kompleks seperti List atau Date yang tidak didukung SQLite."
        ),
        Soal(
            id = 15L,
            soal = "Sebutkan manfaat menggunakan arsitektur MVVM!",
            tipeSoal = 2,
            jawaban = "Memisahkan logika bisnis (ViewModel) dengan UI (View) sehingga lebih mudah ditest."
        )
    )

    val defaultDummyData = normalDummyData
}