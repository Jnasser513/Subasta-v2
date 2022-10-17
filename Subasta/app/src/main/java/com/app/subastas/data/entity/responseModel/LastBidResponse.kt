package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.bid.PujaDetail

data class LastBidResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<PujaDetail>,
    val message: String
)
