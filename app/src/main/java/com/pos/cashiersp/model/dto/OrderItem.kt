package com.pos.cashiersp.model.dto


import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.Calendar

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

@RequiresApi(Build.VERSION_CODES.O)
fun OrderItem.toDomain(): com.pos.cashiersp.model.domain.OrderItem {
    val instant = OffsetDateTime.parse(this.createdAt).toInstant()

    val cal = Calendar.getInstance().apply {
        timeInMillis = instant.toEpochMilli()
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