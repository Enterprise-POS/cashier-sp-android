package com.pos.cashiersp.presentation.invoice_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.PaymentStatus
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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

    private val _tenantId = mutableIntStateOf(0)

    private val _isPrinting = mutableStateOf(false)
    // val isPrinting: State<Boolean> = _isPrinting

    private val _midtransPaymentDialogState = mutableStateOf(false)
    val midtransPaymentDialogState: State<Boolean> = _midtransPaymentDialogState
    private val _midtransPaymentURL = mutableStateOf("")
    val midtransPaymentURL: State<String> = _midtransPaymentURL
    private val _midtransPaymentToken = mutableStateOf("")

    private val _paymentStatusState = mutableStateOf(StateStatus())

    // Dedicated dialog state for payment-gateway related flows (checking transaction status, midtrans, etc.)
    private val _paymentGatewayState = mutableStateOf(GeneralAlertDialogStatus())
    val paymentGatewayState: State<GeneralAlertDialogStatus> = _paymentGatewayState

    private val _uiEvent = MutableSharedFlow<InvoiceDetailViewModel.UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Tracks the active payment-status polling loop so it can be cancelled
    // (dialog dismissed, a fresh check kicked off, or the ViewModel is cleared)
    private var paymentCheckJob: Job? = null

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
                is Resource.Success -> {
                    _tenantId.intValue = tenantResource.data!!.id
                    this.getInvoice(tenantResource.data.id)
                }
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
                viewModelScope.launch { _uiEvent.emit(UIEvent.BackToTransactionHistoryScreen) }
            }

            InvoiceDetailEvent.OnClickCheckTransaction -> {
                if (_paymentStatusState.value.isLoading) return
                _paymentStatusState.value = StateStatus(isLoading = true, "Checking payment status...")

                val oItem = _orderItem.value
                if (oItem == null || _receiptLineItems.value.isEmpty()) {
                    _paymentStatusState.value = StateStatus()
                    _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                        "Request Failed",
                        "Could not check transaction status"
                    )
                    return
                }
                if (oItem.paymentMethod == PaymentMethod.CASH || oItem.paymentMethod == PaymentMethod.OTHER) {
                    _paymentStatusState.value = StateStatus()
                    _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                        "Warning",
                        "Payment type Cash is already validated"
                    )
                    return
                }

                if (oItem.paymentMethod == PaymentMethod.QRIS) {
                    val transactionId = oItem.transactionId
                    val orderItemId = oItem.id
                    val tenantId = _tenantId.intValue
                    orderItemUseCase.checkPaymentStatus(orderItemId, transactionId, tenantId).onEach { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                                    "Payment could not confirmed",
                                    "Something gone wrong while checking payment status. ${resource.message}"
                                )
                            }

                            is Resource.Loading -> {
                                // ...
                            }

                            is Resource.Success -> {
                                if (resource.data == null) {
                                    println("Server crash. Application could not get payment status from this transaction")
                                    _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                                        "Failed to Check Payment Status",
                                        "Application crash. Could not get payment status from this transaction"
                                    )
                                    return@onEach
                                }
                                when (val latestPaymentStatus = resource.data.paymentStatus) {
                                    PaymentStatus.SUCCESS -> {
                                        _paymentGatewayState.value = GeneralAlertDialogStatus.success(
                                            "Payment Success",
                                            "Payment already finished and confirmed"
                                        )

                                        // Set payment status to latest condition
                                        _orderItem.value =
                                            _orderItem.value!!.copy(paymentStatus = latestPaymentStatus)
                                        _paymentStatusState.value = StateStatus()
                                    }

                                    PaymentStatus.PENDING -> {
                                        // Set payment status to latest condition
                                        _orderItem.value =
                                            _orderItem.value!!.copy(paymentStatus = latestPaymentStatus)

                                        // Open midtrans web view
                                        if (oItem.paymentToken == null || oItem.paymentURL == null) {
                                            _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                                                "Something gone wrong",
                                                "Missing data occurred. Please retry another payment method for this case"
                                            )
                                            return@onEach
                                        }
                                        _midtransPaymentToken.value = oItem.paymentToken
                                        _midtransPaymentURL.value = oItem.paymentURL
                                        _midtransPaymentDialogState.value = true
                                        _paymentStatusState.value = StateStatus()

                                        // Start polling every 2s while the user has the payment webview open
                                        checkPaymentStatusPeriodically(orderItemId, transactionId, tenantId)
                                    }

                                    PaymentStatus.REFUNDED -> TODO()
                                    PaymentStatus.FAILED -> TODO()
                                    PaymentStatus.EXPIRED, PaymentStatus.CANCELLED -> {
                                        _paymentGatewayState.value = GeneralAlertDialogStatus.success(
                                            "Payment Cancelled",
                                            "Payment already cancelled and confirmed"
                                        )

                                        // Set payment status to latest condition
                                        _orderItem.value =
                                            _orderItem.value!!.copy(paymentStatus = latestPaymentStatus)
                                        _paymentStatusState.value = StateStatus()
                                    }

                                    PaymentStatus.PARTIALY_REFUNDED -> TODO()
                                }

                            }
                        }
                    }.launchIn(viewModelScope)
                } else {
                    _paymentStatusState.value = StateStatus()
                    _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                        "Unsupported Payment Method",
                        "Current payment method not supported / under development "
                    )
                }
            }

            InvoiceDetailEvent.OnDismissPaymentDialog -> {
                // Just stop polling — do NOT cancel the transaction here.
                // This screen only observes an existing invoice's payment,
                // it doesn't own the transaction lifecycle like the cashier flow does.
                paymentCheckJob?.cancel()
                paymentCheckJob = null

                _midtransPaymentURL.value = ""
                _midtransPaymentToken.value = ""
                _midtransPaymentDialogState.value = false
            }

            InvoiceDetailEvent.OnClickDismissPaymentGatewayDialogBtn -> {
                _paymentGatewayState.value = GeneralAlertDialogStatus()
            }
        }
    }

    /**
     * Polls the payment status every 2 seconds for up to [PAYMENT_CHECK_TIMEOUT_MILLIS].
     * Cancelled whenever the payment dialog is dismissed ([InvoiceDetailEvent.OnDismissPaymentDialog]),
     * a fresh check is kicked off, or the ViewModel is cleared.
     */
    private fun checkPaymentStatusPeriodically(orderItemId: Int, transactionId: String, tenantId: Int) {
        paymentCheckJob?.cancel() // never run two polling loops at once

        paymentCheckJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val interval = 2_000L // 2s between checks

            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed >= PAYMENT_CHECK_TIMEOUT_MILLIS) {
                    _paymentGatewayState.value = GeneralAlertDialogStatus.error(
                        "Check Timed Out",
                        "Payment status check timed out. Please check manually from the transaction history."
                    )
                    _midtransPaymentDialogState.value = false
                    return@launch
                }

                delay(interval.milliseconds) // suspends only, does not block the thread

                when (val result =
                    orderItemUseCase.checkPaymentStatus(orderItemId, transactionId, tenantId).lastOrNull()) {
                    is Resource.Success -> {
                        val data = result.data ?: return@launch // transient/empty response, keep polling
                        when (val latestPaymentStatus = data.paymentStatus) {
                            PaymentStatus.SUCCESS -> {
                                _orderItem.value = _orderItem.value?.copy(paymentStatus = latestPaymentStatus)
                                _paymentGatewayState.value = GeneralAlertDialogStatus.success(
                                    "Payment Success",
                                    "Payment already finished and confirmed"
                                )
                                _midtransPaymentDialogState.value = false
                                return@launch
                            }

                            PaymentStatus.EXPIRED, PaymentStatus.CANCELLED -> {
                                _orderItem.value = _orderItem.value?.copy(paymentStatus = latestPaymentStatus)
                                _paymentGatewayState.value = GeneralAlertDialogStatus.success(
                                    "Payment Cancelled",
                                    "Payment already cancelled and confirmed"
                                )
                                _midtransPaymentDialogState.value = false
                                return@launch
                            }

                            PaymentStatus.PENDING -> {
                                // still pending — keep polling
                            }

                            else -> {
                                // REFUNDED / FAILED / PARTIALLY_REFUNDED — keep polling for now
                            }
                        }
                    }

                    is Resource.Error -> {
                        // Transient network/API failure — keep retrying rather than
                        // aborting the whole flow on a single failed check.
                        println(result.message)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        paymentCheckJob?.cancel()
    }

    sealed class UIEvent {
        object BackToTransactionHistoryScreen : UIEvent()
    }

    companion object {
        private const val PAYMENT_CHECK_TIMEOUT_MILLIS = 5 * 60 * 1000L // 5 minutes
    }
}