# Do Not Listen Beacon

The **Do‑Not‑Listen Beacon** project defines an open protocol and reference
implementations that allow devices to broadcast and honor a nearby user's
request **not to be recorded**. The system relies on Bluetooth Low Energy (BLE)
advertising packets and simple reciprocity rules: devices that broadcast a
*Do‑Not‑Listen* flag must also scan for and respect the flag from others.

## Repository Layout

- `spec/` – Protocol specification and packet format
- `android/` – Reference sender/receiver app for Android
- `microcontroller/` – Example firmware for ESP32 / nRF52 class boards
- `scripts/` – Python proof‑of‑concept tools using [bleak](https://github.com/hbldh/bleak)
- `docs/` – Integration guides and project roadmap

## Quick Start

1. Read the [protocol specification](spec/DoNotListenSpec.md).
2. Try the [Python demo](scripts/demo.py) on a BLE‑capable computer.
3. Explore the Android and ESP32 examples for full mobile/embedded integrations.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for
code style guidelines and the preferred workflow.

## License

Released under the [MIT License](LICENSE).
