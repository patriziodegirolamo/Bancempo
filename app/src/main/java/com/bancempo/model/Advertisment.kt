/*
package com.bancempo.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bancempo.SmallAdv

@Entity(tableName = "advertisments")
class Advertisment {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    var title : String = ""
    var date : String = ""


    override fun toString(): String = "{ id: $id, title: $title, date: $date}"

    fun toSmallAdv() : SmallAdv = SmallAdv(title, date)
}

 */