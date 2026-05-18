package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.domain.OrderItem
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class CreateTransactionParams(
    @SerializedName("items")
    val items: List<Item>,

    @SerializedName("purchased_price")
    val purchasedPrice: Int,
    @SerializedName("total_quantity")
    val totalQuantity: Int,
    @SerializedName("total_amount")
    val totalAmount: Int,
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("sub_total")
    val subTotal: Int,

    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("tenant_id")
    val tenantId: Int,
    @SerializedName("store_id")
    val storeId: Int,
)

// Because CreateTransactionParams is for requesting. createdAt,transactionId must be assigned manually
fun CreateTransactionParams.toOrderItemDomain(
    transactionId: Int,
    createdAt: Calendar,
    storeName: String = ""
): OrderItem {
    return OrderItem(
        id = transactionId,
        createdAt = createdAt,
        discountAmount = this.discountAmount,
        storeId = this.storeId,
        tenantId = this.tenantId,
        totalAmount = this.totalAmount,
        subtotal = this.subTotal,
        totalQuantity = this.totalQuantity,
        purchasedPrice = this.purchasedPrice,
        storeName = storeName
    )
}