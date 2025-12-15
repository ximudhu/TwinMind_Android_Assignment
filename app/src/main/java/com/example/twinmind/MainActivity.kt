package com.example.twinmind

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.twinmind.service.AndroidAudioRecorderService
import com.example.twinmind.ui.screens.DashboardScreen
import com.example.twinmind.ui.screens.DetailScreen
import com.example.twinmind.ui.screens.RecordingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwinMindAppNavigation()
        }
    }
}

@Composable
fun TwinMindAppNavigation() {
    val context = LocalContext.current
    val viewModel: RecordingViewModel = viewModel()

    // Simple state-based navigation
    var currentScreen by remember { mutableStateOf("Dashboard") }
    var selectedRecordingId by remember { mutableStateOf<Int?>(null) }

    // Handle physical back button presses
    BackHandler(enabled = currentScreen != "Dashboard") {
        currentScreen = "Dashboard"
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            startServiceAction(context, "START")
            currentScreen = "Recording"
        }
    }

    // Navigation Graph
    when (currentScreen) {
        "Dashboard" -> {
            DashboardScreen(
                viewModel = viewModel,
                onStartRecording = {
                    if (checkPermissions(context)) {
                        startServiceAction(context, "START")
                        currentScreen = "Recording"
                    } else {
                        // Request Microphone and Notification permissions
                        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
                        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        permissionLauncher.launch(perms.toTypedArray())
                    }
                },
                onRecordingClick = { id ->
                    selectedRecordingId = id
                    currentScreen = "Detail"
                }
            )
        }
        "Recording" -> {
            RecorderScreen(
                onStopRecording = {
                    startServiceAction(context, "STOP")
                    currentScreen = "Dashboard"
                    Toast.makeText(context, "Memory Saved!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        "Detail" -> {
            selectedRecordingId?.let { id ->
                DetailScreen(
                    recordingId = id,
                    viewModel = viewModel,
                    onBackClick = { currentScreen = "Dashboard" }
                )
            }
        }
    }
}
@Composable
fun RecorderScreen(onStopRecording: () -> Unit) {
    // Timer state
    var durationSeconds by remember { mutableLongStateOf(0L) }

    // Start timer when screen launches
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            durationSeconds++
        }
    }

    val captureBlue = Color(0xFF1976D2)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transcribing...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium // Use medium weight for style
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Mock Timer/Visualizer
        CircularProgressIndicator(
            color = captureBlue
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStopRecording,
            colors = ButtonDefaults.buttonColors(containerColor = captureBlue),
            modifier = Modifier.size(width = 200.dp, height = 60.dp)
        ) {
            Text(
                "Stop & Save ${formatDurationTimer(durationSeconds)}",
                fontSize = 18.sp
            )
        }
    }
}
// Helper for 00:00 format
fun formatDurationTimer(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

fun startServiceAction(context: android.content.Context, action: String) {
    val intent = Intent(context, AndroidAudioRecorderService::class.java).apply {
        this.action = action
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && action == "START") {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

fun checkPermissions(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
}
