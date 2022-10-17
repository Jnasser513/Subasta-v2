package com.app.subastas.data.network.service

import com.app.subastas.data.entity.model.lots.ContentResponse
import com.app.subastas.data.entity.model.GenericResponse
import com.app.subastas.data.entity.model.lots.WonLotsDetail
import com.app.subastas.data.entity.model.parameters.BankDetail
import com.app.subastas.data.entity.model.payment.PayCheckDetail
import com.app.subastas.data.entity.model.payment.ReserveAgainBody
import com.app.subastas.data.entity.model.payment.PayReserveBody
import com.app.subastas.data.entity.model.payment.PayTotalBody
import com.app.subastas.data.entity.model.payment.UpdateTotalBody
import com.app.subastas.data.entity.responseModel.BankResponse
import com.app.subastas.data.entity.responseModel.PayCheckResponse
import com.app.subastas.data.entity.responseModel.WonLotsResponse
import retrofit2.Response
import retrofit2.http.*

interface LotService {

    @GET("lote/porsubastar")
    suspend fun getLotes(@Header("Authorization") token: String): Response<GenericResponse<ContentResponse>>

    @POST("lote/sub")
    suspend fun payCheck(@Header("Authorization") token: String, @Body requestBody: PayReserveBody): Response<GenericResponse<List<PayCheckDetail>>>

    @GET("lote/bancos")
    suspend fun getBankList(@Header("Authorization") token: String): Response<GenericResponse<List<BankDetail>>>

    @GET("lote/ganados")
    suspend fun getWonLots(@Header("Authorization") token: String): Response<GenericResponse<List<WonLotsDetail>>>

    @POST("lote/pagototal")
    suspend fun payTotal(@Header("Authorization") token: String, @Body requestBody: PayTotalBody): Response<GenericResponse<List<PayCheckDetail>>>

    @PUT("lote/update_sub")
    suspend fun reserveAgain(@Header("Authorization") token: String , @Body reserveAgainBody: ReserveAgainBody): Response<GenericResponse<List<PayCheckDetail>>>

    @PUT("lote/update_total")
    suspend fun updateTotalPayment(@Header("Authorization") token: String, @Body requestBody: UpdateTotalBody): Response<GenericResponse<List<PayCheckDetail>>>

}