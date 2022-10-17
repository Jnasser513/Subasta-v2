package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.payment.PayCheckDetail

data class PayCheckResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<PayCheckDetail>,
    val message: String
)
