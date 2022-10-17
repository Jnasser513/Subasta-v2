package com.app.subastas.data.entity.model

import java.lang.Exception

sealed class ApiResponse<T> {
    data class Success<T>(val data:T? = null): ApiResponse<T>()
    data class Error<T>(val message: String): ApiResponse<T>()
    data class ErrorWithException<T>(val exception: Exception): ApiResponse<T>()
}