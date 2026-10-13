package com.ahmetakca.aurum.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("photo_url")
    val photoUrl: String?
)
