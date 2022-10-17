package com.app.subastas.data.entity.model.login

data class LoginCodeRequest(
    val correo: String,
    val password: String,
    val codigo: String,
    val token_push: String
)