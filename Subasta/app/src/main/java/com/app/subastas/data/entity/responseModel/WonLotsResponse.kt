package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.lots.WonLotsDetail

data class WonLotsResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<WonLotsDetail>,
    val message: String
)
