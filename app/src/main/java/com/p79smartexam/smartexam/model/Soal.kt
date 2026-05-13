package com.p79smartexam.smartexam.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("soal")
data class Soal(
    @PrimaryKey
    val id: Long = 0,
    val soal: String,
    val tipeSoal: Int,
    val pilihan: String = "",
    val jawaban: String = "",
    val isSynced: Boolean = false
)