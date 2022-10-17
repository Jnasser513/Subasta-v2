package com.app.subastas

import android.app.Application
import com.app.subastas.data.AuctionsDataBase
import com.app.subastas.data.network.AppAPI
import com.app.subastas.repository.*
import com.google.firebase.FirebaseApp

class AppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    private val database by lazy {
        AuctionsDataBase.getDatabase(this)
    }

    val authRepository by lazy {
        val userDao = database.userDao()
        val stateDao = database.stateDao()
        AuthRepository(userDao, stateDao, AppAPI)
    }

    val lotRepository by lazy {
        val userDao = database.userDao()
        LotRepository(userDao,  AppAPI)
    }

    val dataRepository by lazy {
        val userDao = database.userDao()
        DataRepository(userDao, AppAPI)
    }

    val bidRepository by lazy {
        val userDao = database.userDao()
        BidRepository(userDao, AppAPI)
    }

    val subscriptionRepository by lazy {
        val userDao = database.userDao()
        SubscriptionRepository(userDao, AppAPI)
    }
}