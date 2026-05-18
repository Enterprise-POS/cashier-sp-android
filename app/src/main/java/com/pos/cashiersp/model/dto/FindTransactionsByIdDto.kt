package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FindTransactionsByIdDto(
    @SerializedName("order_item")
    val orderItem: OrderItem,
    @SerializedName("purchased_item_list")
    val purchasedItemList: List<PurchasedItem>,
    @SerializedName("requested_order_item_id")
    val requestedOrderItemId: Int
)