package com.app.subastas.data.entity.model.bid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuctionModel(
    val idLote: Long,
    val startHour: Long,
    val finishHour: Long,
    val entryHour: Long
): Parcelable