package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DateFilter(
    @Deprecated("Move to SortColumn")
    val column: String,

    @SerializedName("start_date")
    val startDate: Int,
    @SerializedName("end_date")
    val endDate: Int,
)
