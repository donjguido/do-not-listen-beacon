package org.example.dnl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "dnl"
private const val SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb"

class DoNotListenService : Service() {
    private lateinit var advertiser: BluetoothLeAdvertiser
    private lateinit var audio: AudioManager

    override fun onCreate() {
        super.onCreate()
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        advertiser = btManager.adapter.bluetoothLeAdvertiser
        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createChannel()
        startForeground(1, notification("Broadcasting Do-Not-Listen"))
        startAdvertising()
        startScanning()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(CHANNEL_ID, "Do Not Listen", NotificationManager.IMPORTANCE_LOW)
        nm.createNotificationChannel(ch)
    }

    private fun notification(text: String): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Do-Not-Listen")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .build()

    private fun startAdvertising() {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(false)
            .build()
        val data = AdvertiseData.Builder()
            .addServiceData(android.os.ParcelUuid.fromString(SERVICE_UUID), byteArrayOf(0x10.toByte(), 0x06))
            .build()
        advertiser.startAdvertising(settings, data, object : android.bluetooth.le.AdvertiseCallback() {})
    }

    private fun startScanning() {
        val scanner = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter.bluetoothLeScanner
        val filter = ScanFilter.Builder().setServiceUuid(android.os.ParcelUuid.fromString(SERVICE_UUID)).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        scanner.startScan(listOf(filter), settings, callback)
    }

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val data = result?.scanRecord?.serviceData?.get(android.os.ParcelUuid.fromString(SERVICE_UUID))
            if (data != null) {
                audio.isMicrophoneMute = true
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(2, notification("Microphone muted"))
            }
        }
    }

    override fun onDestroy() {
        audio.isMicrophoneMute = false
        super.onDestroy()
    }
}
