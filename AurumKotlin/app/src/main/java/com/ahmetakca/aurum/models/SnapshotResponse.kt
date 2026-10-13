package com.ahmetakca.aurum.models

import com.google.gson.annotations.SerializedName

data class SnapshotResponse(
    @SerializedName("snapshots")
    var snapshots: List<GetSnapshotAndId>
)
