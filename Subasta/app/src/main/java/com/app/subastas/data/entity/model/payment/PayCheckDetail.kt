package com.app.subastas.data.entity.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayCheckDetail(
    val idSuscripcion: String,
    val nombreLote: String,
    val montoPagar: String,
    val porcentajeReserva: String,
    val fechaComprobante: String,
    val precioLote: String
): Parcelable
