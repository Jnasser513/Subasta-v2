package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.parameters.BankDetail

data class BankResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<BankDetail>,
    val message: String
)
