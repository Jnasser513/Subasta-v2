package com.app.subastas.data.entity.model.parameters

class MunicipalitiesDetail {
    var idMunicipio: Int? = null
    var nombre: String? = null

    override fun toString(): String {
        return nombre!!
    }
}