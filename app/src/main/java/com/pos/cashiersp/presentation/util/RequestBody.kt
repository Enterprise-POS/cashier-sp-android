package com.pos.cashiersp.presentation.util

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.presentation.item_sales_log.SortColumn

data class Filter(
    @SerializedName("column")
    val column: SortColumn,
    @SerializedName("ascending")
    val ascending: Boolean = true
)

data class LoginRequestBody(
    val email: String,
    val password: String,
)

data class SignUpWithEmailAndPasswordRequestBody(
    val email: String,
    val password: String,
    val name: String,
)

data class NewTenantRequestBody(
    val name: String,

    @SerializedName("owner_user_id")
    val ownerUserId: Int
)

data class PurchasedItemListLogsRequestBody(
    @SerializedName("item_ids")
    val itemIds: List<Int>,
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("filters")
    val filters: List<Filter>?,
    @SerializedName("date_filter")
    val dateFilter: DateFilter?,
)
