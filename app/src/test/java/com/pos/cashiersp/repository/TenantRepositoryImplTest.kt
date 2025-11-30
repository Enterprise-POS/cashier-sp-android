package com.pos.cashiersp.repository

import com.google.common.truth.Truth.assertThat
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.success
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import retrofit2.Response

class TenantRepositoryImplTest {
    private lateinit var api: CashierApi
    private lateinit var repository: TenantRepository

    @Before
    fun setUp(): Unit {
        api = mock(CashierApi::class.java)
        repository = TenantRepositoryImpl(api)
    }

    @Test
    fun `Get Tenant Members`() = runBlocking {
        val successResponse = HTTPStatus.SuccessResponse(
            code = 200,
            status = success,
            data = TenantGetMembersDto(
                members = listOf(),
                requestedTenant = 1,
                requestedBy = 1
            )
        )
        val response: Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>> =
            Response.success(successResponse)
        Mockito.`when`(api.getTenantMembers("1")).thenReturn(response)

        val results = repository.getTenantMembers(1)
        assertThat(results.code()).isEqualTo(200)
        assertThat(results.body()!!.data.requestedTenant).isEqualTo(1)
    }
}