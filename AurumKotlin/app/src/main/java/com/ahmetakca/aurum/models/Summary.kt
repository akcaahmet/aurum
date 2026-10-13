package com.ahmetakca.aurum.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class Summary(
    @SerializedName("id")
    val id: Int,
    @SerializedName("gram_high")
    val gramHigh: BigDecimal,
    @SerializedName("gram_low")
    val gramLow: BigDecimal,
    @SerializedName("gram_close_buy")
    val gramCloseBuy: BigDecimal,
    @SerializedName("gram_close_sell")
    val gramCloseSell: BigDecimal,
    @SerializedName("quarter_high")
    val quarterHigh: BigDecimal,
    @SerializedName("quarter_low")
    val quarterLow: BigDecimal,
    @SerializedName("quarter_close_buy")
    val quarterCloseBuy: BigDecimal,
    @SerializedName("quarter_close_sell")
    val quarterCloseSell: BigDecimal,
    @SerializedName("half_high")
    val halfHigh: BigDecimal,
    @SerializedName("half_low")
    val halfLow: BigDecimal,
    @SerializedName("half_close_buy")
    val halfCloseBuy: BigDecimal,
    @SerializedName("half_close_sell")
    val halfCloseSell: BigDecimal,
    @SerializedName("full_high")
    val fullHigh: BigDecimal,
    @SerializedName("full_low")
    val fullLow: BigDecimal,
    @SerializedName("full_close_buy")
    val fullCloseBuy: BigDecimal,
    @SerializedName("full_close_sell")
    val fullCloseSell: BigDecimal,
    @SerializedName("change_rate")
    val changeRate: BigDecimal,
    @SerializedName("created_at")
    val createdAt: Date
)
