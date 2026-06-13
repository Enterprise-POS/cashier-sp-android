package com.pos.cashiersp.presentation.cashier

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.controller.BluetoothController
import com.pos.cashiersp.controller.ReceiptLineItem
import com.pos.cashiersp.model.domain.BluetoothDevice
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.model.domain.Category
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.domain.StoreStock
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.model.dto.toOrderItemDomain
import com.pos.cashiersp.model.dto.toReceiptLine
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnAddQuantity
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnAddToCart
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnDecreaseQuantity
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnRemoveFromCart
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnSelectCategory
import com.pos.cashiersp.presentation.cashier.CashierEvent.OnSelectPaymentMethod
import com.pos.cashiersp.presentation.cashier.CashierEvent.PlaceOrder
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.util.InpTextFieldState
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.presentation.util.parseDateString
import com.pos.cashiersp.presentation.util.toRupiah
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import com.pos.cashiersp.use_case.StoreStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import com.pos.cashiersp.model.domain.Item as domainItem
import com.pos.cashiersp.model.dto.Item as dtoItem

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val storeStockUseCase: StoreStockUseCase,
    private val orderItemUseCase: OrderItemUseCase,
    private val bluetoothController: BluetoothController,
    private val jwtStore: JwtStore,
) : ViewModel() {
    // UI State

    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state

    private val _transactionState = mutableStateOf(StateStatus())
    val transactionState: State<StateStatus> = _transactionState

    private val _loadAllProductsDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val loadAllProductsDialogStatus: State<GeneralAlertDialogStatus> = _loadAllProductsDialogStatus

    private val _generalAlertDialogState = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogState

    // Store / Tenant

    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)
    private val _storeName = mutableStateOf("")

    // Products

    private val _cashierItems = mutableStateOf<List<CashierItem>>(emptyList())
    val cashierItems: State<List<CashierItem>> = _cashierItems

    private val _categories = mutableStateOf<Map<Int, Category>>(emptyMap())
    val categories: State<Map<Int, Category>> = _categories

    /**
     * -1 = "All", 0 = "Uncategorized", else = user-defined category
     */
    private val _selectedCategory = mutableIntStateOf(-1)
    val selectedCategory: State<Int> = _selectedCategory

    private val _searchProductString = mutableStateOf("")
    val searchProductString: State<String> = _searchProductString

    // Cart

    /**
     * Key = Item.itemId
     */
    private val _cart = mutableStateOf<Map<Int, CartItem>>(emptyMap())
    val cart: State<Map<Int, CartItem>> = _cart

    // Payment

    /**
     * Currently only Cash is supported.
     * Credit Card and QR Code are not yet implemented.
     */
    private val _selectedPaymentMethod = mutableStateOf(PaymentMethod.CASH)
    val selectedPaymentMethod: State<PaymentMethod> = _selectedPaymentMethod

    private val _inpCashPaymentMethod = mutableStateOf(InpTextFieldState())
    val inpCashPaymentMethod: State<InpTextFieldState> = _inpCashPaymentMethod

    // Transaction

    private val _transactionCompleteDialogState = mutableStateOf(false)
    val transactionCompleteDialogState: State<Boolean> = _transactionCompleteDialogState

    private val _completeTransactionReference = mutableStateOf<TransactionResponse?>(null)
    val completeTransactionReference: State<TransactionResponse?> = _completeTransactionReference

    // Saved locally to avoid re-fetching when printing
    private val _completeOrderItemReference = mutableStateOf<OrderItem?>(null)
    private val _completeTransactionCartReference = mutableStateOf<List<ReceiptLineItem>>(emptyList())

    // Printing

    private val _isPrinting = mutableStateOf(false)
    val isPrinting: State<Boolean> = _isPrinting

    // Staff

    private val _staffName = mutableStateOf("")
    val staffName: State<String> = _staffName

    private val _staffId = mutableIntStateOf(0)
    val staffId: State<Int> = _staffId

    // Internal

    // AtomicBoolean prevents race conditions from rapid tapping
    private val isProcessingTransaction = AtomicBoolean(false)
    private var transactionJob: Job? = null

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Init

    init {
        loadStoreAndTenantData()
        viewModelScope.launch { loadProfile() }
    }

    // Event Handler

    fun onEvent(event: CashierEvent) {
        when (event) {
            is OnSelectCategory -> onSelectCategory(event)
            is OnAddToCart -> onAddToCart(event)
            is OnAddQuantity -> onAddQuantity(event)
            is OnDecreaseQuantity -> onDecreaseQuantity(event)
            is OnRemoveFromCart -> onRemoveFromCart(event)
            is OnSelectPaymentMethod -> onSelectPaymentMethod(event)
            is PlaceOrder -> onPlaceOrder()
            is CashierEvent.EnteredCashBalance -> onEnteredCashBalance(event)
            is CashierEvent.OnConfirmTransactionBtnDialog -> onConfirmTransactionDialog()
            is CashierEvent.OnConfirmGeneralAlertDialog -> onConfirmGeneralAlertDialog()
            is CashierEvent.OnSearchProduct -> onSearchProduct(event)
            is CashierEvent.OnClearSearchProduct -> onClearSearchProduct()
            is CashierEvent.TryAgainRequestAllProducts -> onTryAgainRequestAllProducts()
            is CashierEvent.OnDismissTryAgainRequestAllProducts -> onDismissTryAgainDialog()
            is CashierEvent.OnPressPrintReceipt -> onPressPrintReceipt()
        }
    }

    // Implementations

    private fun onSelectCategory(event: OnSelectCategory) {
        _selectedCategory.intValue = event.categoryId
    }

    private fun onAddToCart(event: OnAddToCart) {
        if (isTransactionBlocked()) return
        val item = event.cashierItem
        val now = Date()

        val cartItem = CartItem(
            category = Category(
                id = item.categoryId,
                categoryName = item.categoryName,
                tenantId = _tenantId.intValue,
                createdAt = now
            ),
            item = domainItem(
                itemId = item.itemId,
                tenantId = _tenantId.intValue,
                itemName = item.itemName,
                stocks = item.stocks,
                isActive = item.isActive,
                stockType = item.stockType,
                basePrice = item.basePrice
            ),
            storeStock = StoreStock(
                id = item.storeStockId,
                itemId = item.itemId,
                itemName = item.itemName,
                price = item.storeStockPrice,
                stocks = item.storeStockStocks,
                createdAt = now.toString(),
                lastUpdate = now.toString(),
                storeId = _storeId.intValue,
                stockType = item.stockType,
            ),
            quantity = 1
        )

        _cart.value = _cart.value + (item.itemId.toInt() to cartItem)
    }

    private fun onAddQuantity(event: OnAddQuantity) {
        if (isTransactionBlocked()) return

        val existingItem = _cart.value[event.cashierItem.itemId]
            ?: return showFatalError("Increasing quantity on item not in cart")

        val newQuantity = existingItem.quantity + event.quantity
        if (newQuantity > MAX_ITEM_QUANTITY) return

        _cart.value = _cart.value + (event.cashierItem.itemId.toInt() to existingItem.copy(quantity = newQuantity))
    }

    private fun onDecreaseQuantity(event: OnDecreaseQuantity) {
        if (isTransactionBlocked()) return

        val existingItem = _cart.value[event.cashierItem.itemId]
            ?: return showFatalError("Decreasing quantity on item not in cart")

        val newQuantity = existingItem.quantity - event.quantity
        if (newQuantity <= 0) {
            // Auto-remove when quantity reaches 0
            onEvent(OnRemoveFromCart(event.cashierItem))
        } else {
            _cart.value = _cart.value + (event.cashierItem.itemId.toInt() to existingItem.copy(quantity = newQuantity))
        }
    }

    private fun onRemoveFromCart(event: OnRemoveFromCart) {
        if (isTransactionBlocked()) return

        val existingItem = _cart.value[event.cashierItem.itemId]
            ?: return showFatalError("Removing item not in cart")

        _cart.value = _cart.value.toMutableMap().also { it.remove(existingItem.id) }
    }

    private fun onSelectPaymentMethod(event: OnSelectPaymentMethod) {
        val selectedPaymentMethod = event.paymentMethod

        when (selectedPaymentMethod) {
            PaymentMethod.CASH -> _selectedPaymentMethod.value = PaymentMethod.CASH
            PaymentMethod.CARD, PaymentMethod.EWALLET, PaymentMethod.QRIS -> _selectedPaymentMethod.value =
                PaymentMethod.CASH

            PaymentMethod.OTHER -> _selectedPaymentMethod.value = PaymentMethod.OTHER
        }
    }

    private fun onPlaceOrder() {
        // Prevent double-tap / race conditions
        if (transactionJob?.isActive == true) return
        if (!isProcessingTransaction.compareAndSet(false, true)) return

        val currentCart = _cart.value

        // Common validation
        if (currentCart.isEmpty()) {
            isProcessingTransaction.set(false)
            return showWarning("Select at least 1 item / product before make transaction")
        }

        val paymentMethod = _selectedPaymentMethod.value

        // Build order once
        var subTotal = 0L
        var totalQuantity = 0
        val discountAmount = 0L // TODO

        val items = mutableListOf<dtoItem>()

        for ((_, cartItem) in currentCart) {

            // Optional stock validation
            if (cartItem.quantity > cartItem.storeStock.stocks) {
                isProcessingTransaction.set(false)
                return showWarning(
                    "Insufficient stock for ${cartItem.item.itemName}"
                )
            }

            val lineTotal =
                cartItem.storeStock.price.toLong() * cartItem.quantity

            subTotal += lineTotal
            totalQuantity += cartItem.quantity

            items.add(
                dtoItem(
                    itemId = cartItem.item.itemId,
                    quantity = cartItem.quantity,
                    storePriceSnapshot = cartItem.storeStock.price,
                    discountAmount = 0,
                    totalAmount = lineTotal.toInt(),
                    itemNameSnapshot = cartItem.item.itemName,
                    basePriceSnapshot = cartItem.item.basePrice,
                )
            )
        }

        val totalAmount = subTotal - discountAmount

        when (paymentMethod) {

            PaymentMethod.CASH -> {
                // Get the user inputted value
                val cashInput = _inpCashPaymentMethod.value.text

                if (cashInput.isEmpty()) {
                    isProcessingTransaction.set(false)
                    return showWarning(
                        "Please fill the (Amount Received) first before transaction"
                    )
                }

                val purchasedPrice = cashInput.toLongOrNull()

                if (purchasedPrice == null) {
                    isProcessingTransaction.set(false)
                    return showWarning("Invalid payment amount entered")
                }

                if (purchasedPrice < totalAmount) {
                    isProcessingTransaction.set(false)

                    _transactionState.value =
                        StateStatus(error = "Insufficient Payment")

                    _generalAlertDialogState.value =
                        GeneralAlertDialogStatus.error(
                            "Insufficient Payment",
                            "Amount received (${purchasedPrice.toRupiah()}) is less than total (${totalAmount.toRupiah()})"
                        )
                    return
                }

                val params = CreateTransactionParams(
                    items = items,
                    purchasedPrice = purchasedPrice.toInt(),
                    totalQuantity = totalQuantity,
                    totalAmount = totalAmount.toInt(),
                    discountAmount = discountAmount.toInt(),
                    subTotal = subTotal.toInt(),
                    tenantId = _tenantId.intValue,
                    storeId = _storeId.intValue,
                    userId = _staffId.intValue,
                    paymentMethod = paymentMethod
                )

                _transactionState.value =
                    StateStatus(isLoading = true)

                executeTransaction(params, items)
            }

            PaymentMethod.OTHER -> {

                val params = CreateTransactionParams(
                    items = items,
                    purchasedPrice = totalAmount.toInt(),
                    totalQuantity = totalQuantity,
                    totalAmount = totalAmount.toInt(),
                    discountAmount = discountAmount.toInt(),
                    subTotal = subTotal.toInt(),
                    tenantId = _tenantId.intValue,
                    storeId = _storeId.intValue,
                    userId = _staffId.intValue,
                    paymentMethod = paymentMethod
                )

                _transactionState.value =
                    StateStatus(isLoading = true)

                executeTransaction(params, items)
            }

            else -> {
                isProcessingTransaction.set(false)
                showWarning("Payment method not supported")
            }
        }
    }

    private fun executeTransaction(params: CreateTransactionParams, items: List<dtoItem>) {
        transactionJob = orderItemUseCase.transaction(params).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Loading state already set before calling this function
                }

                is Resource.Error -> {
                    isProcessingTransaction.set(false)
                    _transactionState.value = StateStatus(error = resource.message)
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                        "Transaction Failed",
                        resource.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Success -> {
                    isProcessingTransaction.set(false)
                    _transactionState.value = StateStatus()

                    val data = resource.data ?: run {
                        showFatalError("Transaction succeeded but returned no data")
                        return@onEach
                    }

                    // Parse date and build order item reference for printing
                    val storeName = _storeName.value.ifEmpty { "-E" }
                    val calendar = parseDateString(data.createdAt)
                    _completeOrderItemReference.value = if (calendar != null) {
                        params.toOrderItemDomain(data.createdOrderItemId, calendar, storeName = storeName)
                    } else {
                        null // Printing will show an error if this is null
                    }

                    _completeTransactionReference.value = data
                    _completeTransactionCartReference.value = items.map { it.toReceiptLine() }
                    _transactionCompleteDialogState.value = true

                    // Reset cart and payment input
                    _cart.value = emptyMap()
                    _inpCashPaymentMethod.value = InpTextFieldState()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onEnteredCashBalance(event: CashierEvent.EnteredCashBalance) {
        // Filter to digits only — safe to call .toInt() later
        _inpCashPaymentMethod.value = _inpCashPaymentMethod.value.copy(
            text = event.value.filter { it.isDigit() }
        )
    }

    private fun onConfirmTransactionDialog() {
        _transactionCompleteDialogState.value = false
    }

    private fun onConfirmGeneralAlertDialog() {
        _generalAlertDialogState.value = GeneralAlertDialogStatus()
    }

    private fun onSearchProduct(event: CashierEvent.OnSearchProduct) {
        _searchProductString.value = event.text
    }

    private fun onClearSearchProduct() {
        _searchProductString.value = ""
    }

    private fun onTryAgainRequestAllProducts() {
        _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus()
        loadAllStoreStock(_tenantId.intValue, _storeId.intValue)
    }

    private fun onDismissTryAgainDialog() {
        _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus()
    }

    private fun onPressPrintReceipt() {
        val connectedDevices: List<BluetoothDevice> = bluetoothController.pairedDevices.value
        if (connectedDevices.isEmpty()) {
            _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                "Print Error",
                "No printer connected. Please check your devices.\nYou can access transaction data from the transaction screen."
            )
            return
        }

        val transactionResponse = _completeTransactionReference.value
        val purchasedItems = _completeTransactionCartReference.value
        val orderItem = _completeOrderItemReference.value

        if (transactionResponse == null || orderItem == null || purchasedItems.isEmpty()) {
            _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                "Print Error",
                "No data available to print."
            )
            return
        }

        if (_isPrinting.value) return

        _isPrinting.value = true
        _transactionCompleteDialogState.value = false
        _generalAlertDialogState.value = GeneralAlertDialogStatus.loading("Printing...")

        viewModelScope.launch(Dispatchers.IO) {
            bluetoothController.printReceipt(connectedDevices, orderItem, purchasedItems)

            withContext(Dispatchers.Main) {
                _isPrinting.value = false
                _generalAlertDialogState.value = GeneralAlertDialogStatus()
                _transactionCompleteDialogState.value = true
            }
        }
    }

    // Data Loading

    private fun loadStoreAndTenantData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenant, store -> Pair(tenant, store) }
            .onEach { (tenantResource, storeResource) ->
                when {
                    tenantResource is Resource.Success && storeResource is Resource.Success -> {
                        val tenantId = tenantResource.data!!.id
                        val storeId = storeResource.data!!.id
                        _tenantId.intValue = tenantId
                        _storeId.intValue = storeId
                        _storeName.value = storeResource.data.name
                        loadAllStoreStock(tenantId, storeId)
                    }

                    tenantResource is Resource.Error || storeResource is Resource.Error -> {
                        _uiEvent.emit(
                            UIEvent.ErrorAndMustNavigateToSelectTenantScreen(
                                "Fatal Error while getting cashier data."
                            )
                        )
                    }

                    else -> { /* Loading — do nothing */
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadAllStoreStock(tenantId: Int, storeId: Int): Job {
        return storeStockUseCase.loadCashierData(tenantId, storeId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = StateStatus(
                        isLoading = true,
                        loadingMessage = "Please wait…\nRequesting all products and caching data"
                    )
                }

                is Resource.Error -> {
                    _state.value = StateStatus(error = resource.message)
                    _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus.error(
                        "Couldn't get store products",
                        resource.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Success -> {
                    val data = resource.data
                    if (data == null) {
                        _state.value = StateStatus(error = "Server returned no data")
                        _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus.error(
                            "Data Error",
                            "Server returned no data unexpectedly"
                        )
                        return@onEach
                    }

                    _cashierItems.value = data
                    _categories.value = buildCategoryMap(data, tenantId)
                    _state.value = StateStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun loadProfile() {
        val userPayload = jwtStore.getPayload().first() ?: return
        _staffName.value = userPayload.name
        _staffId.intValue = userPayload.sub
    }

    // Helpers

    /**
     * Returns true and emits a snackbar if a transaction is currently being processed.
     */
    private fun isTransactionBlocked(): Boolean {
        if (isProcessingTransaction.get()) {
            viewModelScope.launch {
                _uiEvent.emit(UIEvent.ShowErrorSnackbar("Transaction in progress. Please wait."))
            }
            return true
        }
        return false
    }

    private fun showFatalError(message: String) {
        _generalAlertDialogState.value = GeneralAlertDialogStatus.error("Fatal Error", message)
    }

    private fun showWarning(message: String) {
        _generalAlertDialogState.value = GeneralAlertDialogStatus.error("Warning", message)
    }

    private fun buildCategoryMap(items: List<CashierItem>, tenantId: Int): Map<Int, Category> {
        val now = Date()
        val map = hashMapOf(
            -1 to Category(-1, "All", tenantId, now, items.size),
            0 to Category(0, "Uncategorized", tenantId, now)
        )
        for (item in items) {
            val id = item.categoryId
            if (map.containsKey(id)) {
                map[id]!!.count += 1
            } else {
                map[id] = Category(id, item.categoryName, tenantId, now, 1)
            }
        }
        return map.toMap()
    }

    // UI Events

    sealed class UIEvent {
        data class ErrorAndMustNavigateToSelectTenantScreen(val message: String) : UIEvent()
        object CloseCashierPartialSheet : UIEvent()
        data class ShowErrorSnackbar(val message: String) : UIEvent()
    }

    // Constants

    companion object {
        private const val MAX_ITEM_QUANTITY = 999
    }
}