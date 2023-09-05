package com.example.shoppinglist.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopItemDBModel::class], version = 1, exportSchema = false)
abstract class ApplicationDB : RoomDatabase() {

    abstract fun shopListDao(): ShopListDao

    companion object {
        private var INSTANCE: ApplicationDB? = null
        private val LOCK = Any()
        private const val DB_NAME = "shop_item.db"


        fun getInstance(application: Application): ApplicationDB {
            INSTANCE?.let {
                return it
            }

            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }

                val db = Room.databaseBuilder(
                    application,
                    ApplicationDB::class.java,
                    DB_NAME
                ).build()

                INSTANCE = db

                return db
            }
        }
    }
}