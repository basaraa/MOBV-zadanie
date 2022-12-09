package com.example.zadaniemobv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PubUsersEntity::class,FriendsEntity::class], version = 2, exportSchema = false)
abstract class CreateDatabaseClass : RoomDatabase() {
    abstract fun databaseDAO(): DatabaseDAO
    companion object {
        @Volatile
        private var databaseInstance: CreateDatabaseClass? = null
        fun loadDatabase(context: Context): CreateDatabaseClass {
            return databaseInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, CreateDatabaseClass::class.java,
                    "mobv_database"
                ).fallbackToDestructiveMigration().build()
                databaseInstance = instance
                return instance
            }
        }
        fun getDatabaseInstance (context:Context): CreateDatabaseClass =
            databaseInstance ?: synchronized(this) {
                databaseInstance ?: loadDatabase(context).also {  databaseInstance = it }
            }
    }
}