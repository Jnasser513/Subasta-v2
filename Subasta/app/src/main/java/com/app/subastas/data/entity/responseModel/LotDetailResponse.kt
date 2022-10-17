package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.lots.LotesDetail

data class LotDetailResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<LotesDetail>,
    val message: String
)