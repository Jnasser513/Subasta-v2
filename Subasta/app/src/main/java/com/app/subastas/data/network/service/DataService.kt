package com.app.subastas.data.network.service

import com.app.subastas.data.entity.model.GenericResponse
import com.app.subastas.data.entity.model.parameters.DepartmentsDetail
import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail
import com.app.subastas.data.entity.responseModel.DepartmentsResponse
import com.app.subastas.data.entity.responseModel.MunicipalitiesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DataService {

    @GET("datos/departamentos")
    suspend fun getDepartments(): Response<GenericResponse<List<DepartmentsDetail>>>

    @GET("datos/municipios/{department}")
    suspend fun getMunicipalities(@Path("department") department: String): Response<GenericResponse<MutableList<MunicipalitiesDetail>>>

}