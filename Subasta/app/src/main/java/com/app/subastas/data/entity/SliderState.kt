package com.app.subastas.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slider_state")
data class SliderState(
    @PrimaryKey
    val state: Boolean
)
