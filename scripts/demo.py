#!/usr/bin/env python3
"""Proof-of-concept tools for the Do-Not-Listen Beacon."""

import argparse
import asyncio
from dataclasses import dataclass

try:
    from bleak import BleakScanner, BleakGATTCharacteristic
except Exception:  # pragma: no cover - bleak may be missing on CI
    BleakScanner = None

SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb"


@dataclass
class Frame:
    version: int = 0x10
    flags: int = 0x06  # F and R bits set

    def to_bytes(self) -> bytes:
        return bytes([self.version, self.flags])


async def listen():
    if BleakScanner is None:
        print("bleak not available; cannot listen")
        return
    print("Scanning for Do-Not-Listen frames… press Ctrl+C to stop")
    def detection(device, advertisement_data):
        svc = advertisement_data.service_data.get(SERVICE_UUID.lower())
        if svc:
            print(f"Detected beacon from {device.address}: {svc.hex()}")
    scanner = BleakScanner(detection)
    await scanner.start()
    try:
        while True:
            await asyncio.sleep(1)
    except KeyboardInterrupt:
        await scanner.stop()


def self_test():
    frame = Frame()
    assert frame.to_bytes() == b"\x10\x06"
    print("self-test OK")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--listen", action="store_true", help="Scan for beacons")
    parser.add_argument("--self-test", action="store_true", help="Run a quick self test")
    args = parser.parse_args()
    if args.self_test:
        self_test()
        return
    if args.listen:
        asyncio.run(listen())
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
