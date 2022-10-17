package com.app.subastas.repository

import com.app.subastas.data.dao.SliderStateDao
import com.app.subastas.data.dao.UserDao
import com.app.subastas.data.entity.SliderState
import com.app.subastas.data.entity.User
import com.app.subastas.data.entity.model.ApiResponse
import com.app.subastas.data.entity.model.register.RegisterNaturalBody
import com.app.subastas.data.entity.responseModel.RegisterNaturalResponse
import com.app.subastas.data.network.AppAPI
import com.google.android.gms.common.api.Api
import java.io.IOException

class AuthRepository(
    private val userDao: UserDao,
    private val stateDao: SliderStateDao,
    private val appApi: AppAPI
) {

    val service = appApi.authService

    //Funciones del usuario
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun deleteUser() = userDao.deleteUsers()
    suspend fun updateUser(
        id: Long,
        roles: List<String>,
        token: String,
        tokenType: String,
        email: String
    ) = userDao.updateContact(id, roles, token, tokenType, email)

    fun findAllUsers() = userDao.findAllUsers()

    //Funciones del estado del slider
    suspend fun insertState(state: SliderState) = stateDao.insertSate(state)
    fun findAllStates() = stateDao.findAlStates()

    suspend fun registerNatural(body: RegisterNaturalBody): ApiResponse<RegisterNaturalResponse> {
        try {
            val call = service.register(body)
            val response = call.body()
            val errorCode = response?.errorCode ?: 0
            val message = response?.message ?: "Error"

            return if(call.isSuccessful) {
                when (errorCode) {
                    1 -> ApiResponse.Success()
                    else -> ApiResponse.Error(message)
                }
            } else {
                when(call.code()) {
                    500 -> {
                        ApiResponse.Error("Error de servidor")
                    }
                    else -> {
                        ApiResponse.Error("Error")
                    }
                }
            }
        } catch (e: IOException) {
            return ApiResponse.ErrorWithException(e)
        }
    }
}