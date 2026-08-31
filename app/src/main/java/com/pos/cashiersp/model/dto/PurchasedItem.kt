package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.controller.ReceiptLineItem
import com.pos.cashiersp.presentation.util.parseDateString
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class PurchasedItemDto(
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
    val totalAmount: Int,
    @SerializedName("order_item_created_at")
    val createdAt: String
)

fun PurchasedItemDto.toDomain(): com.pos.cashiersp.model.domain.PurchasedItem {
    val calendar = parseDateString(this.createdAt)

    return com.pos.cashiersp.model.domain.PurchasedItem(
        id = this.id,
        totalAmount = this.totalAmount,
        discountAmount = this.discountAmount,
        itemId = this.itemId,
        storePriceSnapshot = this.storePriceSnapshot,
        quantity = this.quantity,
        itemNameSnapshot = this.itemNameSnapshot,
        orderItemId = this.orderItemId,
        createdAt = calendar
    )
}

fun PurchasedItemDto.toReceiptLine(): ReceiptLineItem {
    return ReceiptLineItem(
        name = this.itemNameSnapshot,
        qty = this.quantity,
        unitPrice = this.storePriceSnapshot.toDouble(),
    )
}
