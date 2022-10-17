 package com.app.subastas.data.network

import com.app.subastas.data.network.service.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val API_BASE_URL = "https://desadga2.mh.gob.sv/subastaol/api/"

private val interceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .writeTimeout(20, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .connectTimeout(20, TimeUnit.SECONDS)
    .addInterceptor(interceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

object AppAPI {
    val authService: AuthService = retrofit.create(AuthService::class.java)
    val lotService: LotService = retrofit.create(LotService::class.java)
    val bidService: BidService = retrofit.create(BidService::class.java)
    val dataService: DataService = retrofit.create(DataService::class.java)
    val subscriptionService: SubscriptionService = retrofit.create(SubscriptionService::class.java)
}