package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail

data class MunicipalitiesResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: MutableList<MunicipalitiesDetail>,
    val message: String
)
