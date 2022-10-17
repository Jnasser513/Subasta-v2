package com.app.subastas.repository

import com.app.subastas.data.dao.UserDao
import com.app.subastas.data.entity.model.ApiResponse
import com.app.subastas.data.entity.model.parameters.DepartmentsDetail
import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail
import com.app.subastas.data.network.AppAPI
import java.io.IOException

class DataRepository(private val userDao: UserDao, private val appApi: AppAPI) {

    private val service = appApi.dataService

    suspend fun getDepartments(): ApiResponse<List<DepartmentsDetail>> {
        try {
            val call = service.getDepartments()
            val response = call.body()
            val errorCode = response?.code ?: 0
            val data = response?.data
            val message = response?.message ?: "Error"

            return if(call.isSuccessful) {
                when(errorCode) {
                    1 -> {
                        if(data != null) {
                            ApiResponse.Success(data)
                        } else {
                            ApiResponse.Error("No hay ningun departamento")
                        }
                    }
                    else -> ApiResponse.Error(message)

                }
            } else {
                when(call.code()) {
                    500 -> ApiResponse.Error("Error de servidor")
                    else -> ApiResponse.Error(message)
                }

            }
        } catch (e: IOException) {
            return ApiResponse.ErrorWithException(e)
        }
    }

    suspend fun getMunicipalities(department: String): ApiResponse<MutableList<MunicipalitiesDetail>> {
        try {
            val call = service.getMunicipalities(department)
            val response = call.body()
            val errorCode = response?.code ?: 0
            val data = response?.data
            val message = response?.message ?: "Error"

            return if(call.isSuccessful) {
                when(errorCode) {
                    1 -> {
                        if(data != null) {
                            ApiResponse.Success(data)
                        } else {
                            ApiResponse.Error("No hay ningun municipio")
                        }
                    }
                    else -> ApiResponse.Error(message)

                }
            } else {
                when(call.code()) {
                    500 -> ApiResponse.Error("Error de servidor")
                    else -> ApiResponse.Error(message)
                }

            }
        } catch (e: IOException) {
            return ApiResponse.ErrorWithException(e)
        }
    }

}