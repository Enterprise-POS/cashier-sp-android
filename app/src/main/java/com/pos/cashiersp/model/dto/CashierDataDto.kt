package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CashierDataDto(
    // Maybe it could delete this file and only load List of CashierItem
    @SerializedName("cashier_data")
    val cashierData: List<CashierItem>
)