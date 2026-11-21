package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.CreatedExpenseCategoryDto
import com.pos.cashiersp.model.dto.CreatedExpenseDto
import com.pos.cashiersp.model.dto.EditedExpenseCategoryDto
import com.pos.cashiersp.model.dto.EditedExpenseDto
import com.pos.cashiersp.model.dto.ExpenseCategoriesDto
import com.pos.cashiersp.model.dto.SearchExpensesDto
import com.pos.cashiersp.model.dto.request_body.ExpenseCategoryRequestBody
import com.pos.cashiersp.model.dto.request_body.ExpenseIdBody
import com.pos.cashiersp.model.dto.request_body.ExpenseRequestBody
import com.pos.cashiersp.model.dto.request_body.SearchExpensesRequestBody
import retrofit2.Response

interface ExpenseRepository {
    suspend fun search(tenantId: Int, body: SearchExpensesRequestBody): Response<HTTPStatus.SuccessResponse<SearchExpensesDto>>
    suspend fun create(tenantId: Int, body: ExpenseRequestBody): Response<HTTPStatus.SuccessResponse<CreatedExpenseDto>>
    suspend fun edit(tenantId: Int, body: ExpenseRequestBody): Response<HTTPStatus.SuccessResponse<EditedExpenseDto>>
    suspend fun delete(tenantId: Int, expenseId: Int): Response<Unit>

    suspend fun getCategories(tenantId: Int): Response<HTTPStatus.SuccessResponse<ExpenseCategoriesDto>>
    suspend fun createCategory(tenantId: Int, name: String): Response<HTTPStatus.SuccessResponse<CreatedExpenseCategoryDto>>
    suspend fun editCategory(tenantId: Int, categoryId: Int, name: String): Response<HTTPStatus.SuccessResponse<EditedExpenseCategoryDto>>
    suspend fun deleteCategory(tenantId: Int, categoryId: Int): Response<Unit>
}

class ExpenseRepositoryImpl(private val api: CashierApi) : ExpenseRepository {
    override suspend fun search(tenantId: Int, body: SearchExpensesRequestBody) = api.searchExpenses(tenantId, body)
    override suspend fun create(tenantId: Int, body: ExpenseRequestBody) = api.createExpense(tenantId, body)
    override suspend fun edit(tenantId: Int, body: ExpenseRequestBody) = api.editExpense(tenantId, body)
    override suspend fun delete(tenantId: Int, expenseId: Int) = api.deleteExpense(tenantId, ExpenseIdBody(expenseId))

    override suspend fun getCategories(tenantId: Int) = api.getExpenseCategories(tenantId)
    override suspend fun createCategory(tenantId: Int, name: String) =
        api.createExpenseCategory(tenantId, ExpenseCategoryRequestBody(name = name))

    override suspend fun editCategory(tenantId: Int, categoryId: Int, name: String) =
        api.editExpenseCategory(tenantId, ExpenseCategoryRequestBody(expenseCategoryId = categoryId, name = name))

    override suspend fun deleteCategory(tenantId: Int, categoryId: Int) =
        api.deleteExpenseCategory(tenantId, ExpenseCategoryRequestBody(expenseCategoryId = categoryId))
}
