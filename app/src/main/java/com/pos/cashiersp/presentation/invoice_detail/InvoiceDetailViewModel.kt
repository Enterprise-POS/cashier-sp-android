package com.pos.cashiersp.presentation.invoice_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.controller.BluetoothController
import com.pos.cashiersp.controller.ReceiptLineItem
import com.pos.cashiersp.model.domain.BluetoothDevice
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.domain.PurchasedItem
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.model.dto.toReceiptLine
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bluetoothController: BluetoothController,
    private val datastoreUseCase: DataStoreUseCase,
    private val orderItemUseCase: OrderItemUseCase,
) : ViewModel() {
    // Will get the passed param at route URL
    private val orderItemId: Int = checkNotNull(savedStateHandle["orderItemId"])

    private val _generalAlertDialogState = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogState

    // Invoice detail related data
    private val _orderItem = mutableStateOf<OrderItem?>(null)
    val orderItem: State<OrderItem?> = _orderItem
    private val _purchasedItemList = mutableStateOf<List<PurchasedItem>>(listOf())
    val purchasedItemList: State<List<PurchasedItem>> = _purchasedItemList
    private val _receiptLineItems = mutableStateOf<List<ReceiptLineItem>>(listOf())

    private val _isPrinting = mutableStateOf(false)
    // val isPrinting: State<Boolean> = _isPrinting

    private val _uiEvent = MutableSharedFlow<InvoiceDetailViewModel.UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        getData()
    }

    private fun getData() {
        if (orderItemId <= 0) {
            _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                "Invalid Invoice ID",
                "Invoice ID is not valid, please go back to transaction history and try again"
            )
            return
        }
        _generalAlertDialogState.value =
            GeneralAlertDialogStatus.loading("Please wait. Requesting invoice detail\nID: $orderItemId")

        this.getUserTenant() // -> will call getInvoice()
    }

    private fun getUserTenant() {
        // This will also check if user is a valid user. Logged or not
        datastoreUseCase.getCurrentTenant().onEach { tenantResource ->
            when (tenantResource) {
                is Resource.Error -> {
                    _generalAlertDialogState.value =
                        GeneralAlertDialogStatus.error("Application Crashed", tenantResource.message!!)
                }

                is Resource.Loading -> {}
                is Resource.Success -> this.getInvoice(tenantResource.data!!.id)
            }
        }.launchIn(viewModelScope)
    }

    private fun getInvoice(tenantId: Int): Job {
        return orderItemUseCase.findTransactionsById(orderItemId, tenantId).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _generalAlertDialogState.value =
                        GeneralAlertDialogStatus.error("Something Wrong", resource.message!!)
                }

                is Resource.Loading -> {}
                is Resource.Success -> {
                    if (resource.data == null) {
                        _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                            "Application Crashed",
                            "Please contact developer for this case"
                        )
                        return@onEach
                    }

                    _orderItem.value = resource.data.orderItem.toDomain()
                    _purchasedItemList.value = resource.data.purchasedItemList.map { it.toDomain() }
                    _receiptLineItems.value = resource.data.purchasedItemList.map { it.toReceiptLine() }

                    _generalAlertDialogState.value = GeneralAlertDialogStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: InvoiceDetailEvent) {
        when (event) {
            InvoiceDetailEvent.OnClickDismissGeneralDialogStatusBtn -> _generalAlertDialogState.value =
                _generalAlertDialogState.value.copy(showDialog = false)

            InvoiceDetailEvent.OnClickPrintReceiptBtn -> {
                val connectedDevices: List<BluetoothDevice> = bluetoothController.pairedDevices.value
                if (connectedDevices.isEmpty()) {
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                        "Print Error",
                        "No printer connected. Please check your devices."
                    )
                    return
                }

                if (_orderItem.value == null || _receiptLineItems.value.isEmpty()) {
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                        "Print Error",
                        "Nothing to print."
                    )
                    return
                }

                if (_isPrinting.value) return

                _isPrinting.value = true
                _generalAlertDialogState.value = GeneralAlertDialogStatus.loading("Printing...")
                val receiptLineItems = _receiptLineItems.value

                viewModelScope.launch(Dispatchers.IO) {
                    bluetoothController.printReceipt(connectedDevices, _orderItem.value!!, receiptLineItems)

                    withContext(Dispatchers.Main) {
                        _isPrinting.value = false
                        _generalAlertDialogState.value = GeneralAlertDialogStatus()
                    }
                }
            }

            InvoiceDetailEvent.OnClickBackToTransactionHistoryBtn -> {
                viewModelScope.launch { _uiEvent.emit(InvoiceDetailViewModel.UIEvent.BackToTransactionHistoryScreen) }
            }
        }
    }

    sealed class UIEvent {
        object BackToTransactionHistoryScreen : UIEvent()
    }
}