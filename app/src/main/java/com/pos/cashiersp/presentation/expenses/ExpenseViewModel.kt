package com.pos.cashiersp.presentation.expenses

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.Expense
import com.pos.cashiersp.model.domain.ExpenseCategory
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.request_body.ExpenseRequestBody
import com.pos.cashiersp.model.dto.request_body.SearchExpensesRequestBody
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.StoreUseCase
import com.pos.cashiersp.use_case.expense_use_case.ExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/** Which expenses the list shows. "This store" = the store this device is logged into. */
enum class ExpenseScope(val label: String) {
    THIS_STORE("This store"),
    WHOLE_BUSINESS("Whole business"),
    ALL("All"),
}

data class ExpenseListState(
    val expenses: List<Expense> = emptyList(),
    val totalCount: Int = 0,
    val sumAmount: Long = 0, // every matched expense, not only the loaded pages
    val loadedPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val canLoadMore: Boolean get() = expenses.size < totalCount
}

data class ExpenseFormState(
    val isOpen: Boolean = false,
    val editing: Expense? = null, // null = add
    val amount: String = "", // digits only
    val categoryId: Int? = null,
    val forStore: Boolean = true,
    val paymentType: String = "CASH",
    val date: Calendar = Calendar.getInstance(),
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
)

data class CategoryManagerState(
    val isOpen: Boolean = false,
    val newName: String = "",
    val renamingId: Int? = null,
    val renamingName: String = "",
    val isBusy: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val storeUseCase: StoreUseCase,
    private val expenseUseCase: ExpenseUseCase,
) : ViewModel() {
    private var tenantId = 0
    private var storeId = 0
    private var searchJob: Job? = null

    private val _storeName = mutableStateOf("")
    val storeName: State<String> = _storeName
    private val _storeNames = mutableStateOf<Map<Int, String>>(emptyMap())
    val storeNames: State<Map<Int, String>> = _storeNames // to label expenses of other stores

    // Filters, default: this store, this month
    private val _scope = mutableStateOf(ExpenseScope.THIS_STORE)
    val scope: State<ExpenseScope> = _scope
    private val _categoryFilter = mutableStateOf(0)
    val categoryFilter: State<Int> = _categoryFilter
    private val _dateRange = mutableStateOf(thisMonth())
    val dateRange: State<Pair<Long, Long>> = _dateRange // local start-of-day millis, both days included

    private val _list = mutableStateOf(ExpenseListState())
    val list: State<ExpenseListState> = _list
    private val _categories = mutableStateOf<List<ExpenseCategory>>(emptyList())
    val categories: State<List<ExpenseCategory>> = _categories
    private val _form = mutableStateOf(ExpenseFormState())
    val form: State<ExpenseFormState> = _form
    private val _categoryManager = mutableStateOf(CategoryManagerState())
    val categoryManager: State<CategoryManagerState> = _categoryManager

    // Categories / store names failed to load: chips would be empty and rows "Store #12" without a word
    private val _setupError = mutableStateOf<String?>(null)
    val setupError: State<String?> = _setupError

    init {
        viewModelScope.launch {
            val (tenant, store) = combine(
                dataStoreUseCase.getCurrentTenant(),
                dataStoreUseCase.getCurrentStore()
            ) { tenant, store -> tenant to store }
                .first { (tenant, store) -> tenant !is Resource.Loading && store !is Resource.Loading }

            if (tenant !is Resource.Success || store !is Resource.Success) {
                _list.value = ExpenseListState(
                    error = "Could not load tenant / store. Try logout and sign in again.\nDetail: ${tenant.message ?: store.message}"
                )
                return@launch
            }
            tenantId = tenant.data!!.id
            storeId = store.data!!.id
            _storeName.value = store.data.name

            loadCategories()
            loadStoreNames()
            loadPage(1)
        }
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.OnSetScope -> {
                _scope.value = event.scope
                loadPage(1)
            }

            is ExpenseEvent.OnSetCategoryFilter -> {
                _categoryFilter.value = event.categoryId
                loadPage(1)
            }

            is ExpenseEvent.OnSetDateRange -> {
                _dateRange.value = event.startDayMillis to event.endDayMillis
                loadPage(1)
            }

            ExpenseEvent.OnLoadMore -> if (_list.value.canLoadMore) loadPage(_list.value.loadedPage + 1)
            ExpenseEvent.OnRefresh -> loadPage(1)
            ExpenseEvent.OnDismissListError -> _list.value = _list.value.copy(error = null)
            ExpenseEvent.OnRetrySetup -> {
                _setupError.value = null
                viewModelScope.launch {
                    loadCategories()
                    loadStoreNames()
                }
            }

            ExpenseEvent.OnOpenAddForm -> _form.value = ExpenseFormState(isOpen = true, forStore = true)
            is ExpenseEvent.OnOpenEditForm -> _form.value = ExpenseFormState(
                isOpen = true,
                editing = event.expense,
                amount = event.expense.amount.toString(),
                categoryId = event.expense.categoryId,
                forStore = event.expense.storeId != null,
                paymentType = event.expense.paymentType,
                date = event.expense.expenseDate.clone() as Calendar,
                description = event.expense.description,
            )

            ExpenseEvent.OnCloseForm -> _form.value = ExpenseFormState()
            is ExpenseEvent.OnChangeAmount -> editForm { copy(amount = event.digits.filter(Char::isDigit).take(15)) }
            is ExpenseEvent.OnChangeCategory -> editForm { copy(categoryId = event.categoryId) }
            is ExpenseEvent.OnChangeForStore -> editForm { copy(forStore = event.forStore) }
            is ExpenseEvent.OnChangePaymentType -> editForm { copy(paymentType = event.paymentType) }
            is ExpenseEvent.OnChangeDate -> editForm { copy(date = event.date) }
            is ExpenseEvent.OnChangeDescription -> editForm { copy(description = event.description.take(500)) }
            ExpenseEvent.OnSaveForm -> saveForm()
            ExpenseEvent.OnDeleteEditing -> deleteEditing()

            is ExpenseEvent.OnSetCategoryManager -> _categoryManager.value = CategoryManagerState(isOpen = event.open)
            is ExpenseEvent.OnChangeNewCategoryName ->
                _categoryManager.value = _categoryManager.value.copy(newName = event.name.take(100))

            ExpenseEvent.OnAddCategory -> {
                val name = _categoryManager.value.newName.trim()
                if (name.isEmpty()) return
                runCategoryAction({ expenseUseCase.createCategory(tenantId, name) }) { copy(newName = "") }
            }

            is ExpenseEvent.OnStartRenameCategory -> _categoryManager.value = _categoryManager.value.copy(
                renamingId = event.categoryId, renamingName = event.name, error = null
            )

            is ExpenseEvent.OnChangeRenameCategory ->
                _categoryManager.value = _categoryManager.value.copy(renamingName = event.name.take(100))

            ExpenseEvent.OnSaveRenameCategory -> {
                val id = _categoryManager.value.renamingId ?: return
                val name = _categoryManager.value.renamingName.trim()
                if (name.isEmpty()) return
                runCategoryAction({ expenseUseCase.editCategory(tenantId, id, name) }) { copy(renamingId = null) }
            }

            is ExpenseEvent.OnDeleteCategory ->
                runCategoryAction({ expenseUseCase.deleteCategory(tenantId, event.categoryId) }) {
                    // the form may point at the deleted category
                    if (_form.value.categoryId == event.categoryId) updateForm { copy(categoryId = null) }
                    if (_categoryFilter.value == event.categoryId) _categoryFilter.value = 0
                    this
                }
        }
    }

    private fun updateForm(change: ExpenseFormState.() -> ExpenseFormState) {
        _form.value = _form.value.change()
    }

    // Any edit by the user clears the last validation / save error
    private fun editForm(change: ExpenseFormState.() -> ExpenseFormState) = updateForm { change().copy(error = null) }

    private fun loadPage(page: Int) {
        if (tenantId == 0) return
        val (start, end) = _dateRange.value
        val body = SearchExpensesRequestBody(
            storeId = when (_scope.value) {
                ExpenseScope.THIS_STORE -> storeId
                ExpenseScope.WHOLE_BUSINESS -> -1
                ExpenseScope.ALL -> 0
            },
            expenseCategoryId = _categoryFilter.value,
            limit = PAGE_SIZE,
            page = page,
            filters = listOf(QueryFilter(column = "expense_date", ascending = false)),
            dateFilter = DateFilter(
                column = "expense_date",
                startDate = (start / 1000).toInt(),
                endDate = (nextDay(end) / 1000).toInt(), // end is exclusive at the API
            ),
        )
        _list.value = _list.value.copy(isLoading = true, error = null)
        searchJob?.cancel() // a slower, older response must not overwrite a newer filter
        searchJob = viewModelScope.launch {
            when (val result = expenseUseCase.search(tenantId, body).last()) {
                is Resource.Success -> {
                    val data = result.data!!
                    val loaded = data.expenses.map { it.toDomain() }
                    _list.value = ExpenseListState(
                        expenses = if (page == 1) loaded else _list.value.expenses + loaded,
                        totalCount = data.totalCount,
                        sumAmount = data.sumAmount,
                        loadedPage = page,
                    )
                }

                else -> _list.value = _list.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    private suspend fun loadCategories() {
        val result = expenseUseCase.getCategories(tenantId).last()
        if (result is Resource.Success) _categories.value = result.data!!
        else _setupError.value = "Couldn't load expense categories. ${result.message ?: ""}"
    }

    private suspend fun loadStoreNames() {
        // Same limit as the web: a tenant has at most 10 stores for now
        val result = storeUseCase.getAll(tenantId, 1, 10, true).last()
        if (result is Resource.Success) _storeNames.value = result.data!!.stores.associate { it.id to it.name }
        else _setupError.value = "Couldn't load store names. ${result.message ?: ""}"
    }

    private fun saveForm() {
        val form = _form.value
        val amount = form.amount.toLongOrNull() ?: 0
        if (amount <= 0) {
            updateForm { copy(error = "Amount must be greater than 0") }
            return
        }
        val body = ExpenseRequestBody(
            expenseId = form.editing?.id,
            // Editing another store's expense keeps that store
            storeId = if (form.forStore) form.editing?.storeId ?: storeId else null,
            expenseCategoryId = form.categoryId,
            amount = amount,
            description = form.description.trim(),
            expenseDate = form.date.timeInMillis / 1000,
            paymentType = form.paymentType,
        )
        updateForm { copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val request = if (form.editing == null) expenseUseCase.create(tenantId, body) else expenseUseCase.edit(tenantId, body)
            when (val result = request.last()) {
                is Resource.Success -> {
                    _form.value = ExpenseFormState()
                    loadPage(1)
                }

                else -> updateForm { copy(isSaving = false, error = result.message) }
            }
        }
    }

    private fun deleteEditing() {
        val expense = _form.value.editing ?: return
        updateForm { copy(isSaving = true, error = null) }
        viewModelScope.launch {
            when (val result = expenseUseCase.delete(tenantId, expense.id).last()) {
                is Resource.Success -> {
                    _form.value = ExpenseFormState()
                    loadPage(1)
                }

                else -> updateForm { copy(isSaving = false, error = result.message) }
            }
        }
    }

    // Every category action: call API, show error or reload categories + list (names are joined into the list)
    private fun <T> runCategoryAction(
        action: () -> Flow<Resource<T>>,
        onSuccess: CategoryManagerState.() -> CategoryManagerState,
    ) {
        _categoryManager.value = _categoryManager.value.copy(isBusy = true, error = null)
        viewModelScope.launch {
            when (val result = action().last()) {
                is Resource.Success -> {
                    _categoryManager.value = _categoryManager.value.copy(isBusy = false).onSuccess()
                    loadCategories()
                    loadPage(1)
                }

                else -> _categoryManager.value = _categoryManager.value.copy(isBusy = false, error = result.message)
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20

        fun startOfDay(calendar: Calendar): Long = (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        fun nextDay(dayMillis: Long): Long =
            Calendar.getInstance().apply { timeInMillis = dayMillis; add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis

        fun thisMonth(): Pair<Long, Long> {
            val today = Calendar.getInstance()
            val firstDay = (today.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
            return startOfDay(firstDay) to startOfDay(today)
        }
    }
}
