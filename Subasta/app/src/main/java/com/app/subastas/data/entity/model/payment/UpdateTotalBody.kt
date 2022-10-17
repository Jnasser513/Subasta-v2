package com.app.subastas.data.entity.model.payment

data class UpdateTotalBody(
    val idSuscripcion: Int,
    val tipoPagoTotal: Int,
    val comprobante: String,
    val montoPagoTotal: Double,
    val fechaPagoTotal: String,
    val transaccionPagoTotal: String,
    val bancoPagoTotal: String
)