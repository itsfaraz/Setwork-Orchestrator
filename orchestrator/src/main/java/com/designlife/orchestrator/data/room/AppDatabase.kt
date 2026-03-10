package com.designlife.orchestrator.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [NotificationEntity::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao() : NotificationDao

    companion object{
        public val DB_NAME = "Setwork-Notification"
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}