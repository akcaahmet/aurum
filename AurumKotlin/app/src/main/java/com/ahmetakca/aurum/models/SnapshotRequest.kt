package com.ahmetakca.aurum.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

open class SnapshotRequest(
    @SerializedName("gram_buy")
    val gramBuy: BigDecimal,
    @SerializedName("gram_sell")
    val gramSell: BigDecimal,
    @SerializedName("quarter_buy")
    val quarterBuy: BigDecimal,
    @SerializedName("quarter_sell")
    val quarterSell: BigDecimal,
    @SerializedName("half_buy")
    val halfBuy: BigDecimal,
    @SerializedName("half_sell")
    val halfSell: BigDecimal,
    @SerializedName("full_buy")
    val fullBuy: BigDecimal,
    @SerializedName("full_sell")
    val fullSell: BigDecimal
)
