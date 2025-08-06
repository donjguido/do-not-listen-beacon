import CoreBluetooth
import Foundation
import UserNotifications

final class DoNotListenManager: NSObject, CBCentralManagerDelegate, CBPeripheralManagerDelegate {
    private let serviceUUID = CBUUID(string: "FFF0")
    private var central: CBCentralManager!
    private var peripheral: CBPeripheralManager!

    override init() {
        super.init()
        central = CBCentralManager(delegate: self, queue: nil)
        peripheral = CBPeripheralManager(delegate: self, queue: nil)
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { _, _ in }
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if central.state == .poweredOn {
            let opts: [String: Any] = [CBCentralManagerScanOptionAllowDuplicatesKey: true]
            central.scanForPeripherals(withServices: [serviceUUID], options: opts)
        }
    }

    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        if peripheral.state == .poweredOn {
            let data: [String: Any] = [
                CBAdvertisementDataServiceDataKey: [serviceUUID: Data([0x10, 0x06])]
            ]
            peripheral.startAdvertising(data)
        }
    }

    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral,
                        advertisementData: [String: Any], rssi RSSI: NSNumber) {
        guard let svc = advertisementData[CBAdvertisementDataServiceDataKey] as? [CBUUID: Data],
              svc[serviceUUID] != nil else { return }
        notify()
    }

    private func notify() {
        let content = UNMutableNotificationContent()
        content.title = "Do-Not-Listen"
        content.body = "Beacon detected. Please pause recording."
        let req = UNNotificationRequest(identifier: UUID().uuidString,
                                        content: content, trigger: nil)
        UNUserNotificationCenter.current().add(req, withCompletionHandler: nil)
    }
}
