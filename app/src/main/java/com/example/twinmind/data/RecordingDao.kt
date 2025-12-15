package com.example.twinmind.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: Recording)

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<Recording>>

    @Query("SELECT * FROM recordings WHERE id = :id")
    fun getRecordingById(id: Int): Flow<Recording>

    @Query("UPDATE recordings SET transcript = :transcript WHERE id = :id")
    suspend fun updateTranscript(id: Int, transcript: String)

    @Query("UPDATE recordings SET summary = :summary WHERE id = :id")
    suspend fun updateSummary(id: Int, summary: String)

    @Query("UPDATE recordings SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Int, title: String)
}