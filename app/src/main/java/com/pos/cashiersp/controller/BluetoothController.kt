package com.pos.cashiersp.controller

import com.pos.cashiersp.model.domain.BluetoothDevice
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.presentation.util.ConnectionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>

    fun closeConnection()
    fun release()

    fun printReceipt(
        device: List<BluetoothDevice> = pairedDevices.value,
        orderItem: OrderItem,
        purchasedItems: List<ReceiptLineItem>,
    )

    @Deprecated("TEST ONLY")
    fun withConnectedDevicesPrintReceipt(device: List<BluetoothDevice> = pairedDevices.value)
}