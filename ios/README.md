# iOS Reference Implementation

This directory contains a minimal Swift class that simultaneously advertises
and scans for Do-Not-Listen beacons using CoreBluetooth. When a beacon is
received a local notification is shown to the user. Integrate
`DoNotListenManager` into an existing Xcode project and ensure the app has
Bluetooth and notification permissions.
