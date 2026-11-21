package com.pos.cashiersp.use_case.expense_use_case

import com.google.gson.Gson
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.Expense
import com.pos.cashiersp.model.domain.ExpenseCategory
import com.pos.cashiersp.model.dto.SearchExpensesDto
import com.pos.cashiersp.model.dto.request_body.ExpenseRequestBody
import com.pos.cashiersp.model.dto.request_body.SearchExpensesRequestBody
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/*
All expense + expense category calls. One class instead of one class per call:
every call shares the same Loading -> Success / Error handling below.
*/
class ExpenseUseCase(private val repository: ExpenseRepository) {
    fun search(tenantId: Int, body: SearchExpensesRequestBody): Flow<Resource<SearchExpensesDto>> =
        call({ repository.search(tenantId, body) }) { it!!.data }

    fun create(tenantId: Int, body: ExpenseRequestBody): Flow<Resource<Expense>> =
        call({ repository.create(tenantId, body) }) { it!!.data.expense.toDomain() }

    fun edit(tenantId: Int, body: ExpenseRequestBody): Flow<Resource<Expense>> =
        call({ repository.edit(tenantId, body) }) { it!!.data.expense.toDomain() }

    fun delete(tenantId: Int, expenseId: Int): Flow<Resource<Unit>> =
        call({ repository.delete(tenantId, expenseId) }) { }

    fun getCategories(tenantId: Int): Flow<Resource<List<ExpenseCategory>>> =
        call({ repository.getCategories(tenantId) }) { body -> body!!.data.expenseCategories.map { it.toDomain() } }

    fun createCategory(tenantId: Int, name: String): Flow<Resource<ExpenseCategory>> =
        call({ repository.createCategory(tenantId, name) }) { it!!.data.category.toDomain() }

    fun editCategory(tenantId: Int, categoryId: Int, name: String): Flow<Resource<ExpenseCategory>> =
        call({ repository.editCategory(tenantId, categoryId, name) }) { it!!.data.category.toDomain() }

    fun deleteCategory(tenantId: Int, categoryId: Int): Flow<Resource<Unit>> =
        call({ repository.deleteCategory(tenantId, categoryId) }) { }

    private fun <B, T> call(request: suspend () -> Response<B>, map: (B?) -> T): Flow<Resource<T>> = flow {
        emit(Resource.Loading())
        try {
            val response = request()
            if (!response.isSuccessful) {
                when (response.code()) {
                    400, 401, 403 -> {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()?.charStream(),
                            HTTPStatus.ErrorResponse::class.java
                        )
                        emit(Resource.Error(errorResponse?.message ?: "Request failed (${response.code()})"))
                    }

                    else -> {
                        println("[INTERNAL ERROR] ${response.code()} ${response.message()}")
                        emit(Resource.Error("[INTERNAL ERROR] Something went wrong. Please try again later."))
                    }
                }
                return@flow
            }

            emit(Resource.Success(map(response.body())))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            // Empty body / unparsable date must show an error, not crash the screen
            println("Unexpected expense response: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Unexpected response from server."))
        }
    }
}
