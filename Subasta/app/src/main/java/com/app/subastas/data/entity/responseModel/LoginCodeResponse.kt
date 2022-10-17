package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.login.LoginCode
import com.google.gson.annotations.SerializedName

data class LoginCodeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("errorCode") val code: Int,
    @SerializedName("data") val data: List<LoginCode>,
    @SerializedName("message") val message: String
)