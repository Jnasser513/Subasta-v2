package com.app.subastas.data.entity.model.change_password

data class VerifyRecoveryBody(
    val correo: String,
    val codigo: String
)