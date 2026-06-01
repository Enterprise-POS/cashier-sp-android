package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.presentation.util.parseDateString
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("purchased_price")
    val purchasedPrice: Int,
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("subtotal")
    val subtotal: Int,
    @SerializedName("tenant_id")
    val tenantId: Int,
    @SerializedName("total_amount")
    val totalAmount: Int,
    @SerializedName("total_quantity")
    val totalQuantity: Int,

    // Store
    @SerializedName("store_name")
    val storeName: String?
)

fun OrderItem.toDomain(): com.pos.cashiersp.model.domain.OrderItem {
    val calendar = parseDateString(this.createdAt)

    return com.pos.cashiersp.model.domain.OrderItem(
        id = this.id,
        subtotal = this.subtotal,
        tenantId = this.tenantId,
        totalAmount = this.totalAmount,
        storeId = this.storeId,
        discountAmount = this.discountAmount,
        totalQuantity = this.totalQuantity,
        purchasedPrice = this.purchasedPrice,
        createdAt = calendar,

        // Store. May not available
        storeName = this.storeName ?: ""
    )
}