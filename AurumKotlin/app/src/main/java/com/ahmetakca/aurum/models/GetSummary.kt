package com.ahmetakca.aurum.models

import com.google.gson.annotations.SerializedName

data class GetSummary(
    @SerializedName("summaries")
    val summaries: List<Summary>
)
