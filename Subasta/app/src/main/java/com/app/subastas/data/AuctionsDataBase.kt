package com.app.subastas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.subastas.data.dao.SliderStateDao
import com.app.subastas.data.dao.UserDao
import com.app.subastas.data.entity.SliderState
import com.app.subastas.data.entity.User

@Database(version = 4, entities = [User::class, SliderState::class])
//@TypeConverters(Converters::class)
abstract class AuctionsDataBase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stateDao(): SliderStateDao

    companion object {
        @Volatile
        private var INSTANCE: AuctionsDataBase? = null
        fun getDatabase(context: Context)= INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                AuctionsDataBase::class.java,
                "subastas_db"
            ).fallbackToDestructiveMigration().build()

            INSTANCE = instance
            instance
        }
    }
}