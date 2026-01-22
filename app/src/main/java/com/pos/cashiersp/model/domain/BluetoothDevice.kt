package com.pos.cashiersp.model.domain

import android.Manifest
import android.bluetooth.BluetoothClass
import androidx.annotation.RequiresPermission
import android.bluetooth.BluetoothDevice as _BluetoothDevice


// Because BluetoothDevice class name already received by Android itself
// To avoid confusion our BluetoothDevice will be BluetoothDeviceDomain
typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name: String?,
    val address: String,
    val deviceType: DeviceType = DeviceType.UNKNOWN
)

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun _BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    // This refer to android BluetoothDevice / _BluetoothDevice
    return BluetoothDeviceDomain(name = this.name, address = this.address, deviceType = this.getDeviceType())
}

enum class DeviceType {
    PRINTER,
    PHONE,
    HEADPHONES,
    SPEAKER,
    COMPUTER,
    WATCH,
    KEYBOARD,
    MOUSE,
    GAMEPAD,
    CAMERA,
    UNKNOWN
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun _BluetoothDevice.getDeviceType(): DeviceType {
    val deviceClass = this.bluetoothClass?.deviceClass
    val majorClass = this.bluetoothClass?.majorDeviceClass
    val name = this.name?.lowercase() ?: ""

    // Check by device class first
    return when {
        // Printer
        deviceClass == BluetoothClass.Device.Major.PERIPHERAL ||
                majorClass == BluetoothClass.Device.Major.IMAGING ||
                name.contains("printer") || name.contains("pos") ||
                name.contains("receipt") || name.contains("thermal") ||
                name.startsWith("rpp") || name.startsWith("mpt") -> DeviceType.PRINTER

        // Audio devices
        deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
                name.contains("headphone") || name.contains("earbuds") ||
                name.contains("airpods") -> DeviceType.HEADPHONES

        deviceClass == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER ||
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO ||
                name.contains("speaker") || name.contains("soundbar") -> DeviceType.SPEAKER

        // Phone
        deviceClass == BluetoothClass.Device.PHONE_SMART ||
                deviceClass == BluetoothClass.Device.PHONE_CELLULAR ||
                majorClass == BluetoothClass.Device.Major.PHONE -> DeviceType.PHONE

        // Computer
        deviceClass == BluetoothClass.Device.COMPUTER_LAPTOP ||
                deviceClass == BluetoothClass.Device.COMPUTER_DESKTOP ||
                majorClass == BluetoothClass.Device.Major.COMPUTER -> DeviceType.COMPUTER

        // Wearables
        deviceClass == BluetoothClass.Device.WEARABLE_WRIST_WATCH ||
                name.contains("watch") || name.contains("band") -> DeviceType.WATCH

        // Peripherals
        deviceClass == BluetoothClass.Device.PERIPHERAL_KEYBOARD ||
                name.contains("keyboard") -> DeviceType.KEYBOARD

        deviceClass == BluetoothClass.Device.PERIPHERAL_POINTING ||
                name.contains("mouse") || name.contains("trackpad") -> DeviceType.MOUSE

        // Gaming
        deviceClass == BluetoothClass.Device.TOY_GAME ||
                name.contains("controller") || name.contains("gamepad") ||
                name.contains("joy") -> DeviceType.GAMEPAD

        // Camera
        deviceClass == BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER ||
                name.contains("camera") || name.contains("gopro") -> DeviceType.CAMERA

        else -> DeviceType.UNKNOWN
    }
}


