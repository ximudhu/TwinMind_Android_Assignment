
package com.example.twinmind.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twinmind.data.Recording
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recordingId: Int,
    viewModel: RecordingViewModel,
    onBackClick: () -> Unit,

) {
    val recordingFlow = remember(recordingId) { viewModel.getRecording(recordingId) }
    val recordingState by recordingFlow.collectAsState()
    val recording = recordingState ?: return

    LaunchedEffect(Unit) {
        if (recording.summary.isEmpty()) {
            viewModel.generateContent(recordingId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        // Bottom Bar "Ask TwinMind" (Mock)
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.LightGray),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ask TwinMind", color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            val displayTitle = if (recording.summary.isEmpty()) "Processing..." else recording.title
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDateDetail(recording.createdAt),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("Notes", "Transcript")

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (selectedTabIndex == 0) {
                    if (recording.summary.isEmpty()) {
                        LoadingPlaceholder("Generating AI Summary...")
                    } else {
                        ProfessionalSummarySection(recording.summary)
                    }
                } else {
                    if (recording.transcript.isEmpty()) {
                        LoadingPlaceholder("Transcribing Audio...")
                    } else {
                        Text(
                            text = recording.transcript,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ProfessionalSummarySection(dbSummary: String) {
    // Summary Card with "Refine" & "Share" buttons
    SectionHeader("Summary & Notes")
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dbSummary.ifEmpty { "Summary not available." },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mock Buttons: Refine & Share
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = { }, modifier = Modifier.height(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refine", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = { }, modifier = Modifier.height(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Action Items
    SectionHeader("Action Items to Review")
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ActionItemRow("Approve Instagram ad budget by Friday")
            Spacer(modifier = Modifier.height(12.dp))
            ActionItemRow("Send Q4 report to stakeholders")
            Spacer(modifier = Modifier.height(12.dp))
            ActionItemRow("Schedule follow-up meeting")
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    SectionHeader("Key Points")
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))) {
        Column(modifier = Modifier.padding(16.dp)) {
            KeyPointRow("Revenue up 15% Year-over-Year.")
            KeyPointRow("Social media channel is the top performer.")
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF37474F),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ActionItemRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp).padding(top = 2.dp),
            tint = Color(0xFF4CAF50) // Green check
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun KeyPointRow(text: String) {
    Row(modifier = Modifier.padding(bottom = 4.dp)) {
        Text("• ", fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LoadingPlaceholder(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text, color = Color.Gray)
    }
}

fun formatDateDetail(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
