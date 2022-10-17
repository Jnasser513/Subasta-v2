package com.app.subastas.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.subastas.data.entity.SliderState

@Dao
interface SliderStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSate(state: SliderState)

    //Borrar estado
    @Query("DELETE FROM slider_state")
    fun deleteState()

    //Actualizar estado
    @Query("UPDATE slider_state SET state = :state")
    fun updateState(state: Boolean)

    @Query("SELECT * FROM slider_state")
    fun findAlStates(): LiveData<List<SliderState>>
}