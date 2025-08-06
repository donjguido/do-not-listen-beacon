# Android Reference Implementation

This module provides a minimal Android service that simultaneously advertises
and scans for Do‑Not‑Listen beacons. When a beacon is received the device's
microphone is muted using `AudioManager.setMicrophoneMute(true)` and a
notification is displayed.

The code is provided as illustrative Kotlin source files. Integrate it into an
existing Android Studio project or build the module with Gradle as needed.
