package com.app.subastas.data.entity.responseModel

data class PujaResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<String>,
    val message: String
)