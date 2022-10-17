package com.app.subastas.data.entity.model.lots

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubastaDetail(
    val idSubasta: Long?,
    val nombre: String?,
    val descripcion: String?,
    val horaInicio: String?,
    val fechaInicio: String?,
    val horaFin: String?,
    val horaDefecto: String?,
    val fechaFin: String?,
    val fechaInicioSuscripcion: String?,
    val fechaFinSuscripcion: String?,
    val horaInicioSuscripcion: String?,
    val horaFinSuscripcion: String?,
    val activo: Boolean?,
    val delete: Boolean?
): Parcelable

@Parcelize
data class EstadoLoteDetail(
    val idEstadoLote: Long,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
): Parcelable

@Parcelize
data class TipoLoteDetail(
    val idTipoLote: Long,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
): Parcelable

@Parcelize
data class ImagenesDetail(
    val idImage: Long,
    val src: String,
    val orden: Int
): Parcelable

data class ContentResponse(
    val content: List<LotesDetail>,
    val pageable: Pageable,
    val totalElements: Int,
    val totalPages: Int
)

data class Pageable(
    val pageSize: Int,
    val pageNumber: Int
)

@Parcelize
data class LotesDetail(
    val idLote: Long?,
    val precio: Long?,
    val descripcion: String?,
    val nombre: String?,
    val impuestos: Long?,
    val peso: String?,
    val medidas: String?,
    val horaInicio: String?,
    val fechaInicio: String?,
    val horaFin: String?,
    val fechaFin: String?,
    val server_time: String?,
    val start_subasta: String?,
    val finish_subasta: String?,
    val delete: Boolean?,
    val porcentajeReserva: Long?,
    val monto_puja: Int?,
    val tipo_puja: Int?,
    val subasta: SubastaDetail?,
    val estadoLote: EstadoLoteDetail?,
    val tipoLote: TipoLoteDetail?,
    val imagenes: List<ImagenesDetail>?,
    val images: String?
): Parcelable
