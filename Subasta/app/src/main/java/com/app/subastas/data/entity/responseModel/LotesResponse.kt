package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.lots.ContentResponse

data class LotesResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: ContentResponse? = null,
    val message: String
)
