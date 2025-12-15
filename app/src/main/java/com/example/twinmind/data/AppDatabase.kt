package com.example.twinmind.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recording::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}