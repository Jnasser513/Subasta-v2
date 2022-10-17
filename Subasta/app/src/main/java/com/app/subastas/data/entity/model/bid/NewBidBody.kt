package com.app.subastas.data.entity.model.bid

data class NewBidBody(
    val idUsuario: Long,
    val idLote: Long,
    val monto: String
)
