# Integration Guide

This guide outlines how to integrate the Do‑Not‑Listen Beacon protocol into
existing products.

## Android

1. Include the reference module under `android/` or copy the relevant source
   files into your project.
2. Request the `BLUETOOTH_ADVERTISE` and `BLUETOOTH_SCAN` permissions.
3. Start the `DoNotListenService` to advertise and scan.

## ESP32 / nRF52

1. Flash the firmware in `microcontroller/esp32` or adapt it to your board.
2. Connect a microphone power pin to GPIO 25 (configurable) so it can be muted.
3. The firmware will advertise and scan automatically on boot.

## Python / Desktop

Run the demo script to broadcast or listen on a laptop or desktop with BLE:

```bash
python scripts/demo.py --listen
```

## Honor the Flag

When a frame with the Do‑Not‑Listen flag is received your application should:

1. Mute microphones or pause recording.
2. Display a visual indicator to the user.
3. Unmute when the beacon is no longer detected for several seconds.

