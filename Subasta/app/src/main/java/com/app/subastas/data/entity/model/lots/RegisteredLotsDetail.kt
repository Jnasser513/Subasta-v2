package com.app.subastas.data.entity.model.lots

data class RegisteredLotsDetail(
    val id_lote: Long,
    val id_usuario_suscripcion: Long,
    val id_usuario: Long,
    val id_estado_sub: Int,
    val id_estado_lote: Int,
    val lote: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val server_time: String,
    val precio: Double,
    val image: String,
    val id_winner: Long,
    val isStarted: Boolean
)
