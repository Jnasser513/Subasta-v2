package com.app.subastas.data.entity.model.parameters

class DepartmentsDetail {
    var idDepartamento: Int? = null
    var nombre: String? = null
    val isocode: String? = null
    val zonesvId: Int? = null

    override fun toString(): String {
        return nombre!!
    }
}