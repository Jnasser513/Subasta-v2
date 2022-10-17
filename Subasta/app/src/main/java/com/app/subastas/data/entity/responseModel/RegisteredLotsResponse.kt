package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.lots.RegisteredLotsDetail

data class RegisteredLotsResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<RegisteredLotsDetail>,
    val message: String
)
