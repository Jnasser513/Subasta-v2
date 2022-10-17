package com.app.subastas.repository

import com.app.subastas.data.dao.UserDao
import com.app.subastas.data.network.AppAPI

class LotRepository(private val userDao: UserDao, appApi: AppAPI) {

    suspend fun deleteUser() = userDao.deleteUsers()
    fun findAllUsers() = userDao.findAllUsers()

}