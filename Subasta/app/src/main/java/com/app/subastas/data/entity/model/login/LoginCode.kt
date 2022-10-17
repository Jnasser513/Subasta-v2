package com.app.subastas.data.entity.model.login

data class LoginCode(
    val id: Long,
    val email: String,
    val roles: List<String>,
    val accessToken: String,
    val tokenType: String
)
