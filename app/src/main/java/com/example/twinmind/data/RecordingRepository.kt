package com.example.twinmind.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingRepository @Inject constructor(
    private val recordingDao: RecordingDao
) {

    fun getAllRecordings(): Flow<List<Recording>> {
        return recordingDao.getAllRecordings()
    }

    fun getRecordingById(id: Int): Flow<Recording> {
        return recordingDao.getRecordingById(id)
    }

    // Simulate transcript generation (Mock API)
    suspend fun generateFakeTranscript(recordingId: Int) {
        delay(2000) // Simulate network latency
        val mockTranscript = "Speaker 1: Thanks for joining everyone. Let's look at the Q4 numbers.\n\n" +
                "Speaker 2: Looks like we are up 15% year over year. The new campaign is working.\n\n" +
                "Speaker 1: Great. What about the budget for next month?\n\n" +
                "Speaker 2: We need to approve the extra spend for Instagram ads by Friday."
        recordingDao.updateTranscript(recordingId, mockTranscript)
    }

    // Simulate summary generation AND Smart Title (Mock API)
    suspend fun generateFakeSummary(recordingId: Int) {
        delay(2500) // Simulate processing time

        // Generate a mock summary text (persisted to DB)
        val mockSummary = "The team reviewed Q4 performance metrics. Revenue is up 15% YoY driven by the new social media campaign."
        recordingDao.updateSummary(recordingId, mockSummary)

        // Generate a "Smart Title" to replace the timestamp
        val smartTitles = listOf(
            "Weekly Sync",
            "Product Design Sync",
            "Client Meeting",
            "Marketing Strategy",
            "Q4 Budget Review"
        )
        val newTitle = smartTitles.random()
        recordingDao.updateTitle(recordingId, newTitle)
    }
}