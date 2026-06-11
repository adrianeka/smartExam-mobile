package com.p79smartexam.smartexam.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.p79smartexam.smartexam.model.Soal

@Database(entities = [Soal::class], version = 1, exportSchema = false)
abstract class SmartExamDb: RoomDatabase() {
    abstract val dao: SoalDao

    companion object {
        @Volatile
        private var INSTANCE: SmartExamDb? = null

        fun getInstance(context: Context): SmartExamDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SmartExamDb::class.java,
                        "catatan.db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}