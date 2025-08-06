#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

// GPIO controlling microphone power
const int MIC_PIN = 25;
const uint16_t SERVICE_UUID = 0xFFF0;
volatile unsigned long lastSeen = 0;

class ScanCallbacks : public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) override {
    if (advertisedDevice.haveServiceData() &&
        advertisedDevice.getServiceDataUUID().getNative()->uu[0] == (SERVICE_UUID & 0xff) &&
        advertisedDevice.getServiceDataUUID().getNative()->uu[1] == (SERVICE_UUID >> 8)) {
      Serial.println("Do-Not-Listen beacon detected; muting microphone");
      digitalWrite(MIC_PIN, LOW); // mute
      lastSeen = millis();
    }
  }
};

void setup() {
  Serial.begin(115200);
  pinMode(MIC_PIN, OUTPUT);
  digitalWrite(MIC_PIN, HIGH); // microphone on
  BLEDevice::init("DNL Beacon");

  // Advertising setup
  BLEAdvertisementData advData;
  advData.setServiceData(BLEUUID(SERVICE_UUID), std::string("\x10\x06", 2));
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->setAdvertisementData(advData);
  pAdvertising->setScanResponse(false);
  pAdvertising->start();

  // Scanning setup
  BLEScan *pScan = BLEDevice::getScan();
  pScan->setAdvertisedDeviceCallbacks(new ScanCallbacks());
  pScan->setActiveScan(true);
  pScan->start(0, nullptr, false);
}

void loop() {
  // unmute after 5 seconds without beacon
  if (digitalRead(MIC_PIN) == LOW && millis() - lastSeen > 5000) {
    Serial.println("No beacon; unmuting microphone");
    digitalWrite(MIC_PIN, HIGH);
  }
  delay(1000);
}
