package com.app.subastas.data.entity.model.bid

data class PujaDetail(
    val id_puja: Long = 0,
    val fecha: String = "0",
    val monto: Double = 0.0,
    val id_lote: Long = 0,
    val id_usuario: Long = 0
)
