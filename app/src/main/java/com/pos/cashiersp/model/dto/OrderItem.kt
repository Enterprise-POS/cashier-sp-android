package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    val totalQuantity: Int
)

fun OrderItem.toDomain(): com.pos.cashiersp.model.domain.OrderItem {
    val cal = Calendar.getInstance().apply {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC") // important: Z means UTC
        time = sdf.parse(createdAt) ?: Date()
    }
    return com.pos.cashiersp.model.domain.OrderItem(
        id = this.id,
        subtotal = this.subtotal,
        tenantId = this.tenantId,
        totalAmount = this.totalAmount,
        storeId = this.storeId,
        discountAmount = this.discountAmount,
        totalQuantity = this.totalQuantity,
        purchasedPrice = this.purchasedPrice,
        createdAt = cal,
    )
}