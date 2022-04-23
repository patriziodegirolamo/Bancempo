package com.bancempo.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Query
import kotlin.concurrent.thread

class AdvRepository(application: Application) {
    private val advDao = AdvertismentDB.getDB(application).advDao()

    fun add(title: String, date: String){
        val adv = Advertisment().also {
            it.title = title
            it.date = date
        }

        advDao.addAdv(adv)

    }

    fun findAll() : LiveData<List<Advertisment>> = advDao.findAll()

    fun count() : LiveData<Int> = advDao.count()

    fun clear() : Unit = advDao.clear()
}