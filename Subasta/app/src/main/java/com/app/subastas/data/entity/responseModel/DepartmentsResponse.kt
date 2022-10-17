package com.app.subastas.data.entity.responseModel

import com.app.subastas.data.entity.model.parameters.DepartmentsDetail

data class DepartmentsResponse(
    val success: Boolean,
    val errorCode: Int,
    val data: List<DepartmentsDetail>,
    val message: String
)