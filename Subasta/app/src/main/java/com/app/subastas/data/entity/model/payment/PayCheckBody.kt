package com.app.subastas.data.entity.model.payment

class payCheckBody(
    var tipo_suscripcion: Int,
    var idLote: Int,
    var idUsuario: Int,
    var banco: String,
    var transaccion: String,
    var comprobante: String,
    var fechaComprobante: String,
    var reserva: String
)