package com.app.subastas.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.subastas.data.entity.User

@Dao
interface UserDao {

    //Insertar usuario en la base de datos local
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    //Borrar usuario
    @Query("DELETE FROM user_table")
    fun deleteUsers()

    @Query("UPDATE user_table SET id = :id, roles = :roles, accessToken = :token, tokenType = :tokenType WHERE email = :email")
    fun updateContact(id: Long, roles: List<String>, token: String, tokenType: String, email: String)

    //Buscar usuario
    @Query("SELECT * FROM user_table WHERE id = :id")
    fun searchUser(id: Long): User

    @Query("SELECT * FROM user_table")
    fun findAllUsers(): LiveData<List<User>>
}