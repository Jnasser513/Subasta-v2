package com.app.subastas.data.entity.model.register

data class RegisterJuridicPersonBody(
    val tipo_persona: Int,
    val nombre: String,
    val dui: String,
    val nit: String,
    val telefono: String,
    val correo: String,
    val nombre_empresa: String,
    val nit_empresa: String,
    val direccion_empresa: String,
    val municipio_empresa: String,
    val departamento_empresa: String,
    val password: String
)
