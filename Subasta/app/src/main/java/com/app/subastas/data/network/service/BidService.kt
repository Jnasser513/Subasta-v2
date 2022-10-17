package com.app.subastas.data.network.service

import com.app.subastas.data.entity.model.GenericResponse
import com.app.subastas.data.entity.model.bid.NewBidBody
import com.app.subastas.data.entity.model.bid.PujaDetail
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.data.entity.responseModel.LastBidResponse
import com.app.subastas.data.entity.responseModel.LotDetailResponse
import com.app.subastas.data.entity.responseModel.PujaResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BidService {

    @POST("puja")
    suspend fun bidUp(@Header("Authorization") token: String, @Body requestBody: NewBidBody): Response<GenericResponse<List<String>>>

    @GET("puja/last/{id}")
    suspend fun getLastBid(@Header("Authorization") token: String, @Path("id") id: String): Response<GenericResponse<List<PujaDetail>>>

    @GET("lote/{id}")
    suspend fun getLotDetail(@Header("Authorization") token: String, @Path("id") id: String): Response<GenericResponse<List<LotesDetail>>>

}