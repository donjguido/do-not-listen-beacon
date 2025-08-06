# System Architecture

The project targets three primary environments:

1. **Mobile devices** – Android service broadcasts and listens for beacons while
   muting the microphone at the OS level.
2. **Embedded microcontrollers** – ESP32 firmware demonstrates hardware control
   of a microphone power pin.
3. **Desktop / research tools** – Python scripts provide quick experiments and
   logging.

All implementations share the same responsibilities:

- Broadcast the Do‑Not‑Listen advertisement frame.
- Scan for the same frame from nearby devices.
- When detected, mute local audio capture and show a user‑visible indicator.
