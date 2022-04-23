/*
package com.bancempo.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AdvDAO {

    @Query( "SELECT * from advertisments")
    fun findAll() : LiveData<List<Advertisment>>

    @Query( "SELECT COUNT(*) from advertisments")
    fun count() : LiveData<Int>

    @Insert
    fun addAdv(adv : Advertisment)

    @Query("DELETE from advertisments")
    fun clear()
}
*/