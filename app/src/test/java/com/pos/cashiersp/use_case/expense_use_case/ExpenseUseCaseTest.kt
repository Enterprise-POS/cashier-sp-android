package com.pos.cashiersp.use_case.expense_use_case

import com.google.common.truth.Truth.assertThat
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.CreatedExpenseCategoryDto
import com.pos.cashiersp.model.dto.CreatedExpenseDto
import com.pos.cashiersp.model.dto.EditedExpenseCategoryDto
import com.pos.cashiersp.model.dto.EditedExpenseDto
import com.pos.cashiersp.model.dto.ExpenseCategoriesDto
import com.pos.cashiersp.model.dto.ExpenseCategoryDto
import com.pos.cashiersp.model.dto.ExpenseDto
import com.pos.cashiersp.model.dto.SearchExpensesDto
import com.pos.cashiersp.model.dto.request_body.ExpenseRequestBody
import com.pos.cashiersp.model.dto.request_body.SearchExpensesRequestBody
import com.pos.cashiersp.repository.ExpenseRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ExpenseUseCaseTest {
    private val body = ExpenseRequestBody(storeId = 1, expenseCategoryId = null, amount = 150_000, description = "Ice", expenseDate = 0, paymentType = "CASH")

    private fun dto(date: String) = ExpenseDto(
        id = 7, storeId = 1, expenseCategoryId = 3, expenseCategoryName = "Supplies", amount = 150_000, description = "Ice",
        expenseDate = date, paymentType = "CASH", responsibleUserNameSnapshot = "Budi", updatedAt = null,
    )

    /** Repository where create() does [create]; everything else is unused. */
    private fun repo(create: () -> Response<HTTPStatus.SuccessResponse<CreatedExpenseDto>>) = object : ExpenseRepository {
        override suspend fun create(tenantId: Int, body: ExpenseRequestBody) = create()
        override suspend fun search(tenantId: Int, body: SearchExpensesRequestBody): Response<HTTPStatus.SuccessResponse<SearchExpensesDto>> = TODO()
        override suspend fun edit(tenantId: Int, body: ExpenseRequestBody): Response<HTTPStatus.SuccessResponse<EditedExpenseDto>> = TODO()
        override suspend fun delete(tenantId: Int, expenseId: Int): Response<Unit> = TODO()
        override suspend fun getCategories(tenantId: Int): Response<HTTPStatus.SuccessResponse<ExpenseCategoriesDto>> = TODO()
        override suspend fun createCategory(tenantId: Int, name: String): Response<HTTPStatus.SuccessResponse<CreatedExpenseCategoryDto>> = TODO()
        override suspend fun editCategory(tenantId: Int, categoryId: Int, name: String): Response<HTTPStatus.SuccessResponse<EditedExpenseCategoryDto>> = TODO()
        override suspend fun deleteCategory(tenantId: Int, categoryId: Int): Response<Unit> = TODO()
    }

    private fun errorResponse(code: Int, json: String): Response<HTTPStatus.SuccessResponse<CreatedExpenseDto>> =
        Response.error(code, json.toResponseBody("application/json".toMediaType()))

    private fun last(useCase: ExpenseUseCase) = runBlocking { useCase.create(1, body).toList() }

    @Test
    fun `success maps the dto to the domain expense`() {
        val emitted = last(ExpenseUseCase(repo {
            Response.success(HTTPStatus.SuccessResponse(200, "success", CreatedExpenseDto(dto("2026-11-21T11:05:29+07:00"))))
        }))
        assertThat(emitted.first()).isInstanceOf(Resource.Loading::class.java)
        val expense = (emitted.last() as Resource.Success).data!!
        assertThat(expense.amount).isEqualTo(150_000L)
        assertThat(expense.categoryName).isEqualTo("Supplies")
        assertThat(expense.expenseDate.timeInMillis).isEqualTo(1795233929000L) // date without fraction parses
    }

    @Test
    fun `400 shows the API message`() {
        val emitted = last(ExpenseUseCase(repo {
            errorResponse(400, """{"code":400,"status":"error","message":"Store 99 not found"}""")
        }))
        assertThat(emitted.last().message).isEqualTo("Store 99 not found")
    }

    @Test
    fun `500 shows a generic message, not the server details`() {
        val emitted = last(ExpenseUseCase(repo { errorResponse(500, """{"message":"pq: SQLSTATE 42P01"}""") }))
        assertThat(emitted.last()).isInstanceOf(Resource.Error::class.java)
        assertThat(emitted.last().message).doesNotContain("SQLSTATE")
    }

    @Test
    fun `no network is an error, not a crash`() {
        val emitted = last(ExpenseUseCase(repo { throw IOException("timeout") }))
        assertThat(emitted.last().message).contains("Couldn't reach server")
    }

    @Test
    fun `unparsable date is an error, not a crash`() {
        val emitted = last(ExpenseUseCase(repo {
            Response.success(HTTPStatus.SuccessResponse(200, "success", CreatedExpenseDto(dto("not-a-date"))))
        }))
        assertThat(emitted.last()).isInstanceOf(Resource.Error::class.java)
    }
}
