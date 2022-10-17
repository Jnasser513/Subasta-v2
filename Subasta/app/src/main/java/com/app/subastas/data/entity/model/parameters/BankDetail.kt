package com.app.subastas.data.entity.model.parameters

class BankDetail{
    var id_banco: Int? = null
    var nombre: String? = null

    override fun toString(): String {
        return nombre!!
    }
}
