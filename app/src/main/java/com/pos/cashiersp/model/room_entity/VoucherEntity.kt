package com.pos.cashiersp.model.room_entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.model.domain.Voucher
import com.pos.cashiersp.model.dto.VoucherDto
import com.pos.cashiersp.model.dto.VoucherType

@Entity
@TypeConverters(VoucherTypeConverters::class)
data class VoucherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val voucherId: Int,
    val code: String,
    val name: String,
    val tenantId: Int,
    val createdAt: String?,
    val voucherType: VoucherType,
    val usedCount: Int,
    val usageLimit: Int,
    val startAt: String?,
    val endAt: String?,
    val percentage: Int,
    val maximumDecrement: Int,
    val roundingPolicy: String,
    val decrement: Int,
    val itemIdCombinations: List<Int>,
    val itemQuantities: Int,
    val storeId: Int,
)

class VoucherTypeConverters {
    @TypeConverter
    fun fromItemIdCombinations(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun toItemIdCombinations(value: String): List<Int> =
        Gson().fromJson(value, object : TypeToken<List<Int>>() {}.type)
}

fun VoucherEntity.toVoucher(): Voucher = Voucher(
    id = voucherId,
    code = code,
    name = name,
    tenantId = tenantId,
    createdAt = createdAt,
    voucherType = voucherType,
    usedCount = usedCount,
    usageLimit = usageLimit,
    startAt = startAt,
    endAt = endAt,
    percentage = percentage,
    maximumDecrement = maximumDecrement,
    roundingPolicy = roundingPolicy,
    decrement = decrement,
    itemIdCombinations = itemIdCombinations.orEmpty(),
    itemQuantities = itemQuantities,
)

fun VoucherDto.toDBEntity(tenantId: Int, storeId: Int): VoucherEntity = VoucherEntity(
    voucherId = id,
    code = code,
    name = name,
    tenantId = tenantId,
    createdAt = createdAt,
    voucherType = voucherType,
    usedCount = usedCount,
    usageLimit = usageLimit,
    startAt = startAt,
    endAt = endAt,
    percentage = percentage,
    maximumDecrement = maximumDecrement,
    roundingPolicy = roundingPolicy,
    decrement = decrement,
    itemIdCombinations = itemIdCombinations.orEmpty(),
    itemQuantities = itemQuantities,
    storeId = storeId,
)
