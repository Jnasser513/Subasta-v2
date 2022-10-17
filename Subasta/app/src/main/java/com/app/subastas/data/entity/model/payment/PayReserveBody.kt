package com.app.subastas.data.entity.model.payment

data class PayReserveBody(
    val tipo_suscripcion: Int,
    val comprobante: String = "",
    val reserva: String,
    val idLote: Int,
    val idUsuario: Long,
    val transaccion: String,
    val banco: String,
    val fechaComprobante: String
)
