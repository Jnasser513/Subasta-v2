package com.app.subastas.data.network.service

import com.app.subastas.data.entity.model.GenericResponse
import com.app.subastas.data.entity.model.lots.RegisteredLotsDetail
import com.app.subastas.data.entity.model.lots.SuscriptionDetail
import com.app.subastas.data.entity.responseModel.RegisteredLotsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SubscriptionService {

    @GET("usersubscripcion")
    suspend fun getRegisteredLots(@Header("Authorization") token: String): Response<GenericResponse<List<RegisteredLotsDetail>>>

    @GET("usersubscripcion/obtener_suscripcion/{id}")
    suspend fun getSuscriptionDetail(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<SuscriptionDetail>>

}