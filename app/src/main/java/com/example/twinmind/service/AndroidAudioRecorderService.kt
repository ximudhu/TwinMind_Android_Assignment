package com.example.twinmind.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.twinmind.data.Recording
import com.example.twinmind.data.RecordingDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.widget.Toast
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class AndroidAudioRecorderService : Service() {

    @Inject
    lateinit var recordingDao: RecordingDao

    private val CHANNEL_ID = "TwinMindRecorderChannel"
    private val NOTIFICATION_ID = 1

    private var mediaRecorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var startTime: Long = 0

    // Scope for database operations
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startRecording()
            "STOP" -> stopRecording()
        }
        return START_STICKY
    }

    private fun startRecording() {
        val minSpace = 10 * 1024 * 1024L // 10MB minimum required space
        val freeSpace = applicationContext.filesDir.freeSpace

        if (freeSpace < minSpace) {
            // Show error Toast message
            Toast.makeText(
                this,
                "Recording stopped - Low storage",
                Toast.LENGTH_LONG
            ).show()

            stopSelf()
            return
        }
        createNotificationChannel()

        val fileName = "audio_${System.currentTimeMillis()}.m4a"
        val file = File(filesDir, fileName)
        currentFile = file

        // Create notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TwinMind is listening")
            .setContentText("Recording in progress...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        // Start Foreground Service (Compatibility for Android 14+)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
            return
        }

        // Initialize and start MediaRecorder
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            startTime = System.currentTimeMillis()
        } catch (e: IOException) {
            e.printStackTrace()
            stopRecording()
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            // Check if recorder was actually running
        }
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {}
        mediaRecorder = null

        // Save metadata to Room Database asynchronously
        val durationSeconds = (System.currentTimeMillis() - startTime) / 1000
        currentFile?.let { file ->
            if (durationSeconds >= 0) {
                serviceScope.launch {
                    try {
                        val newRecording = Recording(
                            title = "Recording ${System.currentTimeMillis()}",
                            filePath = file.absolutePath,
                            durationSeconds = durationSeconds,
                            isRecording = false
                        )
                        recordingDao.insertRecording(newRecording)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "TwinMind Recording Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}