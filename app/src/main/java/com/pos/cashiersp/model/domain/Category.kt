package com.pos.cashiersp.model.domain

import java.util.Date

data class Category(
    val id: Int,
    val categoryName: String,
    val tenantId: Int,
    val createdAt: Date,
)
