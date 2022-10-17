package com.app.subastas.data.entity.model.lots

data class SuscriptionDetail(
    val idUsuarioSuscripcion: Int,
    val reserva: Int,
    val porcentajeReserva: Int,
    val fechaAdjudicacion: String,
    val comprobante: String,
    val transaccion: String,
    val banco: String,
    val tipo_suscripcion: Int,
    val idLote: Lote
)

data class Lote(
    val idLote: Int,
    val precio: Double,
    val porcentajeReserva: Int
)
