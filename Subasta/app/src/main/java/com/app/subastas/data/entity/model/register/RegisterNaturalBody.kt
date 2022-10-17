package com.app.subastas.data.entity.model.register

data class RegisterNaturalBody(
    val tipo_persona: Int,
    val nombre: String,
    val direccion: String,
    val municipio: String,
    val departamento: String,
    val dui: String,
    val nit: String,
    val telefono: String,
    val correo: String,
    val password: String
)