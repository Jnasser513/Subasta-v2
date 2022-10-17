package com.app.subastas.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    val id: Long?,
    @PrimaryKey
    val email: String,
    val roles: String?,
    val accessToken: String?,
    val tokenType: String?
)