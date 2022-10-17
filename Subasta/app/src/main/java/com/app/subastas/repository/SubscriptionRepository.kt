package com.app.subastas.repository

import com.app.subastas.data.dao.UserDao
import com.app.subastas.data.network.AppAPI

class SubscriptionRepository(private val userDao: UserDao, appApi: AppAPI) {

    fun findAllUsers() = userDao.findAllUsers()
    suspend fun deleteUser() = userDao.deleteUsers()

}