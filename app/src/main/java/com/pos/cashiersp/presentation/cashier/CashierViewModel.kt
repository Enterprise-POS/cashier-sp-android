package com.pos.cashiersp.presentation.cashier

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.model.domain.Category
import com.pos.cashiersp.model.domain.StoreStock
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.dto.CreateTransactionParams
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
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import com.pos.cashiersp.use_case.StoreStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import com.pos.cashiersp.model.domain.Item as domainItem
import com.pos.cashiersp.model.dto.Item as dtoItem

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CashierViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val storeStockUseCase: StoreStockUseCase,
    private val orderItemUseCase: OrderItemUseCase,
    private val jwtStore: JwtStore,
) : ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state
    private val _loadAllProductsDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val loadAllProductsDialogStatus: State<GeneralAlertDialogStatus> = _loadAllProductsDialogStatus

    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)

    private val _cashierItems = mutableStateOf<List<CashierItem>>(listOf())
    val cashierItems: State<List<CashierItem>> = _cashierItems

    /*
    * -1 = "All"
    * 0 = "Uncategorized"
    * else = --From user--
    * */
    private val _categories = mutableStateOf<Map<Int, Category>>(mapOf())
    val categories: State<Map<Int, Category>> = _categories
    private val _selectedCategory = mutableIntStateOf(-1)
    val selectedCategory: State<Int> = _selectedCategory
    private val _inpCashPaymentMethod = mutableStateOf(InpTextFieldState())
    val inpCashPaymentMethod: State<InpTextFieldState> = _inpCashPaymentMethod

    // Sometimes state still take time to disable UI so AtomicBoolean is needed
    private val isProcessingTransaction = AtomicBoolean(false)
    private val _transactionState = mutableStateOf(StateStatus())
    val transactionState: State<StateStatus> = _transactionState

    private val _searchProductString = mutableStateOf("")
    val searchProductString: State<String> = _searchProductString

    // For any error or notification alert.
    private val _generalAlertDialogState = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogState

    /*
    * Key = Item.itemId
    * */
    private val _cart = mutableStateOf<Map<Int, CartItem>>(mapOf())
    val cart: State<Map<Int, CartItem>> = _cart

    private val _staffName = mutableStateOf("")
    val staffName: State<String> = _staffName
    private val _staffId = mutableIntStateOf(0)
    val staffId: State<Int> = _staffId

    /*
    * Payment method
    * 1. Cash / Immediate payment (ok)
    * 2. Credit Card (x)
    * 3. QR code (x)
    * */
    private val _selectedPaymentMethod = mutableStateOf(PaymentMethod.CASH)
    val selectedPaymentMethod: State<PaymentMethod> = _selectedPaymentMethod

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var transactionJob: Job? = null

    init {
        loadData()
        viewModelScope.launch { loadProfile() }
    }

    private fun now() = Date.from(Instant.now())

    fun onEvent(event: CashierEvent) {
        when (event) {
            is OnSelectCategory -> {
                // Null = no category selected = "All"
                _selectedCategory.intValue = event.categoryId
            }

            is OnAddToCart -> {
                if (isProcessingTransaction.get()) {
                    viewModelScope.launch {
                        _uiEvent.emit(UIEvent.ShowErrorSnackbar("Transactions under processing. Try again later"))
                    }
                    return
                }
                val item = event.cashierItem
                val currentCart = _cart.value

                _cart.value = currentCart + (item.itemId.toInt() to CartItem(
                    category = Category(item.categoryId, item.categoryName, _tenantId.intValue, now()),
                    item = domainItem(
                        item.itemId,
                        _tenantId.intValue,
                        item.itemName,
                        item.stocks,
                        item.isActive,
                        item.stockType
                    ),
                    storeStock = StoreStock(
                        item.storeStockId,
                        item.itemId,
                        item.itemName,
                        item.storeStockPrice,
                        item.storeStockStocks,
                        now().toString(),
                        now().toString(),
                        _storeId.intValue
                    ),
                    quantity = 1
                ))
            }

            is OnAddQuantity -> {
                if (isProcessingTransaction.get()) {
                    viewModelScope.launch {
                        _uiEvent.emit(UIEvent.ShowErrorSnackbar("Transactions under processing. Try again later"))
                    }
                    return
                }
                val quantity = event.quantity
                val item = event.cashierItem
                val existingItem = _cart.value[event.cashierItem.itemId]
                val currentCart = _cart.value


                if (existingItem != null) {
                    // Item exists, increment quantity
                    if (existingItem.quantity + 1 <= 999) {
                        _cart.value = currentCart + (item.itemId.toInt() to existingItem.copy(
                            quantity = existingItem.quantity + quantity
                        ))
                    }
                } else {
                    val title = "FATAL ERROR"
                    val message = "Increasing on not exist item at cart"
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(title, message)
                }
            }

            is OnDecreaseQuantity -> {
                if (isProcessingTransaction.get()) {
                    viewModelScope.launch {
                        _uiEvent.emit(UIEvent.ShowErrorSnackbar("Transactions under processing. Try again later"))
                    }
                    return
                }
                val quantity = event.quantity
                val item: CashierItem = event.cashierItem
                val existingCartItem: CartItem? = _cart.value[item.itemId]
                val currentCart: MutableMap<Int, CartItem> = _cart.value.toMutableMap()

                if (existingCartItem != null) {
                    if (existingCartItem.quantity - quantity > 0) {
                        // Item exists, increment quantity
                        _cart.value = currentCart + (item.itemId.toInt() to existingCartItem.copy(
                            quantity = existingCartItem.quantity - quantity
                        ))
                    } else {
                        // If user click decrease on item that already 1 quantity
                        // then also remove the item from cart. This will recursive onEvent for RemovingItemFromCart
                        this.onEvent(OnRemoveFromCart(item))
                    }
                } else {
                    val title = "FATAL ERROR"
                    val message = "Decreasing on not exist item at cart"
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(title, message)
                }
            }

            is OnRemoveFromCart -> {
                if (isProcessingTransaction.get()) {
                    viewModelScope.launch {
                        _uiEvent.emit(UIEvent.ShowErrorSnackbar("Transactions under processing. Try again later"))
                    }
                    return
                }

                val item: CashierItem = event.cashierItem
                val existingCartItem: CartItem? = _cart.value[item.itemId]
                val currentCart: MutableMap<Int, CartItem> = _cart.value.toMutableMap()
                if (existingCartItem != null) {
                    currentCart.remove(existingCartItem.id)
                    _cart.value = currentCart
                } else {
                    val title = "FATAL ERROR"
                    val message = "Removing item from cart that not exist item at cart"
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(title, message)
                }
            }

            is OnSelectPaymentMethod -> {
                // Currently only support Cash
                _selectedPaymentMethod.value = PaymentMethod.CASH
            }

            is PlaceOrder -> {
                // Prevent mash clicking - check if transaction is already running
                if (transactionJob?.isActive == true) {
                    return
                }
                // Atomic check-and-set - prevents race conditions
                if (!isProcessingTransaction.compareAndSet(false, true)) {
                    // Already processing, ignore this click
                    return
                }
                // Validate input
                if (_inpCashPaymentMethod.value.text.isEmpty()) {
                    isProcessingTransaction.set(false) // Reset flag
                    val title = "Warning"
                    val message = "Please fill the (Amount Received) first before transaction"
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(title, message)
                    return
                }
                val currentCart = _cart.value
                if (currentCart.isEmpty()) {
                    val title = "Warning"
                    val message = "Select at least 1 item / product before make transaction"
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(title, message)
                    isProcessingTransaction.set(false)
                    return
                }
                _transactionState.value = StateStatus(isLoading = true)

                val purchasedPrice = _inpCashPaymentMethod.value.text.toInt()
                var subTotal = 0
                var totalQuantity = 0
                val discountAmount = 0
                val items: MutableList<dtoItem> = mutableListOf()
                for ((_, cartItem) in currentCart) {
                    subTotal += cartItem.storeStock.price * cartItem.quantity
                    totalQuantity += cartItem.quantity
                    // discountAmount += ...
                    items.add(
                        dtoItem(
                            itemId = cartItem.item.itemId,
                            quantity = cartItem.quantity,
                            purchasedPrice = cartItem.storeStock.price,
                            discountAmount = 0,
                            totalAmount = cartItem.storeStock.price * cartItem.quantity,
                            itemNameSnapshot = cartItem.item.itemName
                        )
                    )
                }
                val totalAmount = subTotal - discountAmount
                if (purchasedPrice < totalAmount) {
                    _transactionState.value = StateStatus(error = "Insufficient Payment")
                    _generalAlertDialogState.value = GeneralAlertDialogStatus.error(
                        "Insufficient Payment",
                        "Amount received (¥$purchasedPrice) is less than total (¥$totalAmount)"
                    )
                    isProcessingTransaction.set(false)
                    return
                }

                val params = CreateTransactionParams(
                    items = items,
                    purchasedPrice = purchasedPrice,
                    totalQuantity = totalQuantity,
                    totalAmount = totalAmount,
                    discountAmount = discountAmount,
                    subTotal = subTotal,

                    tenantId = _tenantId.intValue,
                    storeId = _storeId.intValue,
                    userId = _staffId.intValue
                )
                transactionJob = orderItemUseCase.transaction(params).onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            // This onEvent reset flag
                            isProcessingTransaction.set(false)

                            val title = "Transaction failed"
                            val message = resource.message!!
                            _transactionState.value = StateStatus(error = message)
                            _generalAlertDialogState.value =
                                GeneralAlertDialogStatus.error(title, message)
                        }

                        is Resource.Loading -> {
                            /*
                                We don't change loading state while start requesting. Otherwise it will be late because
                                asynchronous execute late
                            * */
                        }

                        is Resource.Success -> {
                            // This onEvent reset flag
                            isProcessingTransaction.set(false)
                            val title = "Transaction Complete"
                            val message = "Transaction Successful\ntransaction id: ${resource.data!!.transactionId}"
                            _transactionState.value = StateStatus(successMessage = message)

                            _generalAlertDialogState.value =
                                GeneralAlertDialogStatus.success(title, message)

                            // Reset the cart and input
                            _cart.value = emptyMap()
                            _inpCashPaymentMethod.value = InpTextFieldState()
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is CashierEvent.EnteredCashBalance -> {
                // This will guaranteed that every input that user input is only Int. So it's safe to use .toInt()
                _inpCashPaymentMethod.value =
                    _inpCashPaymentMethod.value.copy(text = event.value.filter { it.isDigit() })
            }

            is CashierEvent.OnConfirmTransactionBtnDialog -> {
            }

            is CashierEvent.OnConfirmGeneralAlertDialog -> {
                // Close the dialog
                _generalAlertDialogState.value = GeneralAlertDialogStatus()
            }

            is CashierEvent.OnSearchProduct -> {
                // Will find the item base what user search
                _searchProductString.value = event.text
            }

            is CashierEvent.OnClearSearchProduct -> {
                // Reset search bar input when x icon clicked
                _searchProductString.value = ""
            }

            is CashierEvent.TryAgainRequestAllProducts -> {
                // Close the dialog first
                _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus()

                // Reload the data with current tenant and store
                loadAllStoreStock(_tenantId.intValue, _storeId.intValue)
            }

            is CashierEvent.OnDismissTryAgainRequestAllProducts -> {
                // Close try again request all products dialog
                _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus()
            }
        }
    }

    private fun loadData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenantResource, storeResource ->
            Pair(tenantResource, storeResource)
        }.onEach { (tenantResource, storeResource) ->
            when {
                tenantResource is Resource.Success && storeResource is Resource.Success -> {
                    // Both data arrived successfully
                    /*
                    println("Tenant: ${tenantResource.data}")
                    println("Store: ${storeResource.data}")
                    */
                    // 01 Try to fetch the data with the available data

                    // 02 If the data return as UNAUTHORIZED action then

                    // 03a Delete tenant and store data store

                    // 03b Proceed to fetch all store stock
                    val tenantId = tenantResource.data!!.id
                    val storeId = storeResource.data!!.id

                    _tenantId.intValue = tenantId
                    _storeId.intValue = storeId

                    this.loadAllStoreStock(tenantId, storeId)
                }

                tenantResource is Resource.Error || storeResource is Resource.Error -> {
                    // 01 Do not proceed to fetch the all store

                    // 02 Go back to select tenant screen

                    // 03 Delete store data store
                    /*
                    println("If store not found then name user choose the store first")
                    println("Error message from store resource: ${storeResource.message}")
                    println("Store error: ${storeResource.message}")
                    * */

                    // 01 Fetch again the selected tenant all store
                    _uiEvent.emit(UIEvent.ErrorAndMustNavigateToSelectTenantScreen("Fatal Error while get cashier data."))
                }

                tenantResource is Resource.Loading || storeResource is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    /*
    * Because always repeating from tenant to store is tiring job, For LoginRegisterScreen change from navigating to SELECT_TENANT into CASHIER
    * Don't forget to change the tenantId and storeId while debugging. If we directly change at MainActivity into CASHIER
    * the cookies will apply. Later need change
    * */
    private fun loadAllStoreStock(tenantId: Int, storeId: Int): Job {
        return storeStockUseCase.loadCashierData(tenantId, storeId).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _state.value = StateStatus(isLoading = false, error = resource.message)
                    val title = "Couldn't get store products."
                    val unexpectedErrorMessage =
                        "Unexpected error happened :(]\nWe will try fix this functionality as soon as possible"
                    _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus.error(
                        title,
                        resource.message ?: unexpectedErrorMessage
                    )
                    println("[ERROR] ${resource.message} CashierViewModel.loadAllStoreStock")
                }

                is Resource.Loading -> {
                    _state.value = StateStatus(
                        isLoading = true,
                        loadingMessage = "Please wait...\nRequesting all products data and caching data"
                    )
                }

                is Resource.Success -> {
                    val data: List<CashierItem>? = resource.data
                    if (data == null) {
                        _state.value = StateStatus(
                            isLoading = false,
                            error = "Unexpected error occurred ! Data return nothing / null"
                        )
                        _loadAllProductsDialogStatus.value = GeneralAlertDialogStatus.error(
                            "Data Error",
                            "Unexpected error occurred! Data return nothing / null"
                        )
                        return@onEach
                    }

                    // Applying to viewmodel
                    _cashierItems.value = data

                    val tempCategories = hashMapOf<Int, Category>(
                        -1 to Category(-1, "All", tenantId, now(), data.size),
                        0 to Category(0, "Uncategorized", tenantId, now())
                    )
                    for (cashierItem in data) {
                        val categoryId = cashierItem.categoryId
                        val categoryName = cashierItem.categoryName
                        if (tempCategories.containsKey(categoryId)) {
                            tempCategories[categoryId]!!.count += 1
                        } else {
                            tempCategories.put(
                                categoryId, Category(categoryId, categoryName, tenantId, now(), 1)
                            )
                        }
                    }
                    _categories.value = tempCategories.toMap()
                    _state.value = StateStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    /*
    * This is not immediately required request, Keep it simple for this time
    * maybe change in the future
    * */
    private suspend fun loadProfile() {
        val userPayload = jwtStore.getPayload().first()
        if (userPayload == null) {
            /*
                ERROR
                // Check if user really logged in
                // Then try again
            */

        } else {
            _staffName.value = userPayload.name
            _staffId.intValue = userPayload.sub
        }
    }

    sealed class UIEvent {
        data class ErrorAndMustNavigateToSelectTenantScreen(val message: String) : UIEvent()
        object CloseCashierPartialSheet : UIEvent()
        data class ShowErrorSnackbar(val message: String) : UIEvent()
    }
}