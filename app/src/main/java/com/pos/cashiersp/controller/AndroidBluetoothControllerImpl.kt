package com.pos.cashiersp.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.pos.cashiersp.model.domain.BluetoothDevice
import com.pos.cashiersp.model.domain.BluetoothDeviceDomain
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.domain.PurchasedItem as domainPurchasedItem
import com.pos.cashiersp.model.domain.toBluetoothDeviceDomain
import com.pos.cashiersp.model.dto.FindTransactionsByIdDto
import com.pos.cashiersp.model.dto.PurchasedItem as dtoPurchasedItem
import com.pos.cashiersp.presentation.util.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothControllerImpl(
    private val context: Context,
) : BluetoothController {
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    // get own address, bluetooth name, get list of scan devices, get list of pair devices
    // to initiate scan, to initiate connection, to start server
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device: android.bluetooth.BluetoothDevice ->
        _scannedDevices.update { devices: List<BluetoothDeviceDomain> ->
            // Must check both for _scannedDevices and _pairedDevices otherwise double device might appear
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices || newDevice in _pairedDevices.value) devices else devices + newDevice
        }
    }

    private val _isPrinting = MutableStateFlow(false)
    val isPrinting: StateFlow<Boolean> get() = _isPrinting.asStateFlow()

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        /**
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {return
        }
         */

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()
        println(bluetoothAdapter)

        bluetoothAdapter?.startDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        /*
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
        return
        }
         */
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    override fun stopDiscovery() {
        /*
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        * */

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        TODO("Not yet implemented")
    }

    override fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult> {
        return flow {
            /*
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            * */

            emit(ConnectionResult.Connecting)
            val remoteDevice =
                bluetoothAdapter
                    ?: throw IllegalStateException("Bluetooth adapter not available")

            bluetoothAdapter?.cancelDiscovery()

            val btDevice = remoteDevice.getRemoteDevice(device.address)

            // Wait until bonded
            if (btDevice.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
                btDevice.createBond()
                waitForBond(btDevice)
            }

            // Is it need some socket ?

            // Update the scanned and paired. otherwise the UI crash because the key at UI must unique
            _scannedDevices.update { it - device }
            _pairedDevices.update { it + device }
            emit(ConnectionResult.ConnectionEstablished)
        }.catch {
            emit(ConnectionResult.Error(it.message ?: "Unknown error occurred"))
        }.flowOn(Dispatchers.IO)
    }

    /*
        We don't pass the value to function because interface already now which printer by default should print
        bluetoothController.withConnectedDevicesPrintReceipt(_state.value.pairedDevices)
    * */
    override fun withConnectedDevicesPrintReceipt(devices: List<BluetoothDevice>) {
        _isPrinting.update { true }
        devices.forEach { device ->
            try {
                val androidDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                val connection = BluetoothConnection(androidDevice)

                val printer = EscPosPrinter(connection, 183, 76.5f, 48)
                val formattedText = createFormattedText(printer)

                // Give time for print job to complete
                // delay(2000) // 2 seconds delay between printers

                printer.printFormattedTextAndCut(formattedText)

                // connection.disconnect()

            } catch (e: Exception) {
                println("Printer Failed to print to ${device.name}: ${e.message}")
                // Continue to next printer even if one fails
            }
        }
        _isPrinting.update { false }
    }

    override fun printReceipt(
        devices: List<BluetoothDevice>,
        orderItem: OrderItem,
        receiptItems: List<ReceiptLineItem>
    ) {
        val orderData = OrderData(
            id = orderItem.id.toString(),
            storeName = orderItem.storeName,
            items = receiptItems,
            discountAmount = orderItem.discountAmount.toDouble(),
            cash = orderItem.purchasedPrice.toDouble(),
            taxRate = 0.0,
        )

        _isPrinting.update { true }
        try {
            devices.forEach { device ->
                println("Printing at: ${device.name}")
                try {
                    val androidDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                        ?: throw IllegalStateException("Bluetooth adapter is null")
                    val connection = BluetoothConnection(androidDevice)
                    val printer = EscPosPrinter(connection, 183, 76.5f, 48)
                    printer.printFormattedTextAndCut(buildReceiptText(orderData))
                } catch (e: Exception) {
                    println("Printer failed for ${device.name}: ${e.message}")
                }
            }
        } finally {
            _isPrinting.update { false }
        }
    }

    override fun withConnectedDevicesPrintReceipt2(
        devices: List<BluetoothDevice>,
        findTransactionsByIdDto: FindTransactionsByIdDto,
    ) {
        val orderItem = findTransactionsByIdDto.orderItem
        val purchasedItemList = findTransactionsByIdDto.purchasedItemList

        val receiptItems = purchasedItemList.map { item ->
            ReceiptLineItem(
                name = item.itemNameSnapshot,
                qty = item.quantity,
                unitPrice = item.storePriceSnapshot.toDouble(),
            )
        }

        val orderData = OrderData(
            id = orderItem.id.toString(),
            storeName = orderItem.storeName,
            items = receiptItems,
            discountAmount = orderItem.discountAmount.toDouble(),
            cash = orderItem.purchasedPrice.toDouble(),
            taxRate = 0.0,
        )

        _isPrinting.update { true }
        try {
            devices.forEach { device ->
                println("Printing at: ${device.name}")
                try {
                    val androidDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                        ?: throw IllegalStateException("Bluetooth adapter is null")
                    val connection = BluetoothConnection(androidDevice)
                    val printer = EscPosPrinter(connection, 183, 76.5f, 48)
                    printer.printFormattedTextAndCut(buildReceiptText(orderData))
                } catch (e: Exception) {
                    println("Printer failed for ${device.name}: ${e.message}")
                }
            }
        } finally {
            _isPrinting.update { false }
        }
    }

    suspend fun waitForBond(device: android.bluetooth.BluetoothDevice) {
        while (device.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
            delay(300)
        }
    }

    override fun closeConnection() {
        TODO("Not yet implemented")
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private fun createFormattedText(printer: EscPosPrinter): String {
        return "[C]<font size='big'>TASTY BITES</font>\n" +
                "[C]Order #123\n" +
                "[C]================================\n" +
                "[L]<b>Burger Deluxe</b>          12.99\n" +
                "[L]  - No onions\n" +
                "[L]  - Extra cheese\n" +
                "[L]\n" +
                "[L]<b>Fries (Large)</b>           4.99\n" +
                "[L]<b>Coke</b>                    2.50\n" +
                "[L]  x2\n" +
                "[C]--------------------------------\n" +
                "[R]SUBTOTAL:                  20.48\n" +
                "[C]================================\n" +
                "[R]<b>TOTAL:                    24.17</b>\n"
        /*
        return "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
            printer,
            context.resources.getDrawableForDensity(
                R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM
            )
        ) + "</img>\n" +
                "[L]\n" +
                "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                "[L]\n" +
                "[C]================================\n" +
                "[L]\n" +
                "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                "[L]  + Size : S\n" +
                "[L]\n" +
                "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                "[L]  + Size : 57/58\n" +
                "[L]\n" +
                "[C]--------------------------------\n" +
                "[R]TOTAL PRICE :[R]34.98e\n" +
                "[R]TAX :[R]4.23e\n" +
                "[L]\n" +
                "[C]================================\n" +
                "[L]\n" +
                "[L]<font size='tall'>Customer :</font>\n" +
                "[L]Raymond DUPONT\n" +
                "[L]5 rue des girafes\n" +
                "[L]31547 PERPETES\n" +
                "[L]Tel : +33801201456\n" +
                "[L]\n" +
                "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                "[L]\n"
    }
         */
        return "test"
    }
}

// This is the broadcast class where our android device will received the data that we need
class FoundDeviceReceiver(
    private val onDeviceFound: (android.bluetooth.BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        android.bluetooth.BluetoothDevice.EXTRA_DEVICE,
                        android.bluetooth.BluetoothDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                }

                // Will execute the onDeviceFound callback that we give at AndroidBluetoothControllerImpl
                device?.let(onDeviceFound)
            }
        }
    }
}

// This is a special data class that, the printer only required information when printing the receipt
// Could be use for converting PurchasedItem, Item
// this data required because sometime we don't know yet the Item id yet
data class ReceiptLineItem(
    val name: String,
    val qty: Int,
    val unitPrice: Double,
) {
    val lineTotal: Double get() = qty * unitPrice
}

data class OrderData(
    val id: String,
    val storeName: String,
    val items: List<ReceiptLineItem> = emptyList(),
    val discountAmount: Double = 0.0,
    val cash: Double = 0.0,
    val taxRate: Double = 0.0,
)

// ── Receipt text builder ──────────────────────────────────────────────────────
fun buildReceiptText(order: OrderData): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    val fmt = { v: Double -> "Rp %.0f".format(v) }

    val subtotal = order.items.sumOf { it.lineTotal }
    val afterDiscount = subtotal - order.discountAmount
    val taxAmount = afterDiscount * order.taxRate
    val totalAmount = afterDiscount + taxAmount
    val change = order.cash - totalAmount

    // 48 chars fills full width at 183dpi / 76.5mm
    val divider = "[C]================================================\n"
    val thinLine = "[C]------------------------------------------------\n"

    return buildString {
        // ── Header ────────────────────────────────────────────────────────
        append("[C]<font size='big'>${order.storeName}</font>\n")
        append(divider)
        append("[L]Order : #${order.id}\n")
        append("[L]Date  : $dateStr\n")
        append(divider)

        // ── Column header ─────────────────────────────────────────────────
        append("[L]<b>ITEM</b>[R]<b>AMOUNT</b>\n")
        append(thinLine)

        // ── Items ─────────────────────────────────────────────────────────
        order.items.forEach { item ->
            append("[L]<b>${item.name}</b>[R]${fmt(item.lineTotal)}\n")
            append("[L]  ${fmt(item.unitPrice)} x ${item.qty}\n")
        }

        // ── Subtotals ─────────────────────────────────────────────────────
        append(thinLine)
        append("[L]Subtotal[R]${fmt(subtotal)}\n")

        if (order.discountAmount > 0) {
            append("[L]Discount[R]-${fmt(order.discountAmount)}\n")
        }

        if (order.taxRate > 0) {
            val pct = (order.taxRate * 100).toInt()
            append("[L]Tax ($pct%)[R]${fmt(taxAmount)}\n")
        }

        // ── Grand total ───────────────────────────────────────────────────
        append(divider)
        append("[L]<font size='big'><b>TOTAL</b></font>[R]<font size='big'><b>${fmt(totalAmount)}</b></font>\n")
        append(divider)

        // ── Payment ───────────────────────────────────────────────────────
        append("[L]Cash[R]${fmt(order.cash)}\n")
        append("[L]<b>Change</b>[R]<b>${fmt(change)}</b>\n")
        append(thinLine)

        // ── Footer ────────────────────────────────────────────────────────
        append("[C]\n")
        append("[C]<font size='tall'>Thank you for your visit!</font>\n")
        append("[C]Please come again\n")
        append("[C]\n")
        append("[L]\n")
    }
}