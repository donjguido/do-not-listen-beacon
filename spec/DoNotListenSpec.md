# Do‑Not‑Listen Beacon Protocol Specification

Version: 0.1.0

This document describes a minimal interoperable protocol for broadcasting a
"Do‑Not‑Listen" request over Bluetooth Low Energy (BLE) advertising.

## Overview

Devices that support the protocol broadcast a short advertisement frame while
they also continuously scan for the same frame from others. On reception, a
participating device should mute local microphones and provide a user
notification for as long as the beacon is observed.

## Packet Format

The protocol uses **Service Data** for a 16‑bit Service UUID. Until an official
UUID is allocated the value `0xFFF0` is used for experimentation.

```
0               1               2               3
+---------------+---------------+---------------+
| Version |F|R|S|   Reserved    |  Payload ...
+---------------+---------------+---------------+
```

* **Version** – 4 bits major, 4 bits minor (e.g. `0x10` for v1.0).
* **F** – Do‑Not‑Listen flag. When set, the sender requests nearby devices to
  pause audio recording and hotword detection.
* **R** – Reciprocity flag. Must be set to `1` to indicate the device is also
  scanning and honoring requests from others.
* **S** – Signature present. When `1`, a public‑key signature follows the
  payload.
* **Reserved** – Future use; must be `0`.
* **Payload** – Optional opaque data. When `S=1` the payload is `N` bytes of
  application‑defined content followed by a 64‑byte Ed25519 signature of the
  preceding bytes.

The entire Service Data field including header and payload must fit within
normal BLE advertising limits (31 bytes for legacy, 255 bytes for extended).

### Example Minimal Frame

An implementation that only needs the basic flag would send the following
Service Data bytes:

```
UUID = 0xFFF0
Data = 0x10 0x06
```

`0x10` represents version 1.0 and `0x06` sets bits F and R.

## Security

To prevent spoofing a device may sign the payload with an Ed25519 private key
and advertise the public key through another channel. Receivers can maintain
trust lists of public keys for automated honoring of requests.

## State Machine

1. **Broadcast** – Transmit the advertisement at a 1 Hz to 10 Hz interval.
2. **Scan** – Continuously scan for the Service UUID `0xFFF0`.
3. **Honor** – When a valid frame with `F=1` is detected, mute microphones and
   notify the user. Unmute when frames are no longer observed for a configurable
   timeout (default 5 s).

## Registration and Standardization

1. **Bluetooth SIG** – Apply for a 16‑bit Service UUID through the [Bluetooth
   SIG](https://www.bluetooth.com/specifications/assigned-numbers/16-bit-uuids-for-members/) once the protocol is stable.
2. **IETF** – Prepare an Internet‑Draft based on this document and submit it to
   the IETF Bluetooth‑related working group.

## License

This specification is released under the MIT License.
