package com.app.subastas.data.network.service

import com.app.subastas.data.entity.model.GenericResponse
import com.app.subastas.data.entity.model.login.LoginCode
import com.app.subastas.data.entity.model.register.UploadFileBody
import com.app.subastas.data.entity.model.change_password.ChangePasswordBody
import com.app.subastas.data.entity.model.change_password.ForgotPasswordBody
import com.app.subastas.data.entity.model.change_password.RecoveryCodeBody
import com.app.subastas.data.entity.model.change_password.VerifyRecoveryBody
import com.app.subastas.data.entity.model.login.LoginCodeRequest
import com.app.subastas.data.entity.model.login.LoginRequest
import com.app.subastas.data.entity.model.register.RegisterJuridicPersonBody
import com.app.subastas.data.entity.model.register.RegisterNaturalBody
import com.app.subastas.data.entity.responseModel.LoginUserResponse
import com.app.subastas.data.entity.responseModel.RegisterNaturalResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("auth/signincode")
    suspend fun loginUser(@Body requestBody: LoginRequest): Response<GenericResponse<List<String>>>

    @POST("auth/signin")
    suspend fun loginCode(@Body requestBody: LoginCodeRequest): Response<GenericResponse<List<LoginCode>>>

    @POST("auth/signup/natural")
    suspend fun register(@Body requestBody: RegisterNaturalBody): Response<RegisterNaturalResponse>

    @POST("auth/signup/juridico")
    suspend fun registerJuridic(@Body requestBody: RegisterJuridicPersonBody): Response<RegisterNaturalResponse>

    @GET("auth/delete_push/{id}")
    suspend fun deleteTokenPush(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<GenericResponse<String>>

    @POST("auth/signup/filem")
    suspend fun uploadFile(@Body uploadFileBody: UploadFileBody): Response<GenericResponse<String>>

    @POST("auth/change_password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body requestBody: ForgotPasswordBody
    ): Response<GenericResponse<String>>

    @POST("auth/send_recovery_code")
    suspend fun sendRecoveryCode(@Body requestBody: RecoveryCodeBody): Response<GenericResponse<String>>

    @POST("auth/verify_recovery_code")
    suspend fun verifyRecoveryCode(@Body requestBody: VerifyRecoveryBody): Response<GenericResponse<String>>

    @POST("auth/forgot_password")
    suspend fun changePassword(@Body requestBody: ChangePasswordBody): Response<GenericResponse<String>>

}