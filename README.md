# TwinMind Android Assignment


## 🚨 Demo Video: [**Click here to watch the App Walkthrough**](https://drive.google.com/file/d/1XS7plpV-JLK4H0FZX7zaEjeKz1ARApAQ/)

This is a submission for the TwinMind Android Developer assignment, implementing a voice recording and summarization app using Jetpack Compose and Modern Android Architecture.

## Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **DI:** Hilt
* **Database:** Room
* **Concurrency:** Coroutines & Flow
* **Services:** Foreground Service for recording

## Features Implemented

### 1. Audio Recording
* **Background Recording:** Uses a Foreground Service to keep recording active when the app is minimized.
* **UI Feedback:** Displays recording status and a timer (00:00) directly on the stop button.
* **Storage Check:** Added a check to prevent recording if device storage is below 10MB.
* **Data Simulation:** Simulates the "Transcribing..." process and generates a mock summary for demonstration purposes.

### 2. Summary UI
* **Structured Layout:** Implemented the requested 4-section view: Title, Summary, Action Items, and Key Points.
* **Detail View:** Users can view saved recordings and their details from the dashboard.

### 3. Architecture
* **MVVM:** Separated logic into ViewModels and Repositories.
* **Room Database:** Used as the single source of truth for the recording list.

## Trade-offs & Notes

Due to the 48-hour time limit, I made the following decisions:

* **Single File Recording:** I implemented continuous recording into a single file instead of 30-second chunks to ensure playback stability for this MVP.
* **Edge Cases:** I focused on the main recording flow and the "Low Storage" check. Complex handling for phone call interruptions and process death was omitted to prioritize code stability.
* **WebM Format:** The demo video is recorded using the Android Emulator (WebM format).
