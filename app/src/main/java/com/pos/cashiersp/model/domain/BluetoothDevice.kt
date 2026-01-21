package com.pos.cashiersp.model.domain

import android.Manifest
import androidx.annotation.RequiresPermission
import android.bluetooth.BluetoothDevice as _BluetoothDevice


// Because BluetoothDevice class name already received by Android itself
// To avoid confusion our BluetoothDevice will be BluetoothDeviceDomain
typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name: String?,
    val address: String
)

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun _BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    // This refer to android BluetoothDevice / _BluetoothDevice
    return BluetoothDeviceDomain(name = this.name, address = this.address)
}

