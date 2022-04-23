/*
package com.bancempo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Advertisment::class], version = 1)
abstract class AdvertismentDB : RoomDatabase() {
    abstract fun advDao(): AdvDAO

    companion object {
        @Volatile
        private var INSTANCE: AdvertismentDB? = null

        fun getDB(context: Context): AdvertismentDB =
            (
                    INSTANCE ?: synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            AdvertismentDB::class.java,
                            "advertisments"
                        ).build()
                        println("------------è entrato $i")

                        INSTANCE = i
                        INSTANCE
                    }
                    )!!

    }
}

 */