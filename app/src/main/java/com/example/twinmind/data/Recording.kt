package com.example.twinmind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,

    // Local path to the .m4a audio file
    val filePath: String = "",

    val transcript: String = "",
    val summary: String = "",
    val isRecording: Boolean = false
)