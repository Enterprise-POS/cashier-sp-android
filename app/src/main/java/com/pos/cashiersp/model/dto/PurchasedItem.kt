package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.controller.ReceiptLineItem
import kotlinx.serialization.Serializable

@Serializable
data class PurchasedItem(
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_name_snapshot")
    val itemNameSnapshot: String,
    @SerializedName("order_item_id")
    val orderItemId: Int,

    @SerializedName("store_price_snapshot")
    val storePriceSnapshot: Int,
    // @SerializedName("purchased_price")
    // val purchasedPrice: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("total_amount")
    val totalAmount: Int
)

fun PurchasedItem.toDomain() = com.pos.cashiersp.model.domain.PurchasedItem(
    id = this.id,
    totalAmount = this.totalAmount,
    discountAmount = this.discountAmount,
    itemId = this.itemId,
    storePriceSnapshot = this.storePriceSnapshot,
    quantity = this.quantity,
    itemNameSnapshot = this.itemNameSnapshot,
    orderItemId = this.orderItemId,
)

fun PurchasedItem.toReceiptLine(): ReceiptLineItem {
    return ReceiptLineItem(
        name = this.itemNameSnapshot,
        qty = this.quantity,
        unitPrice = this.storePriceSnapshot.toDouble(),
    )
}
