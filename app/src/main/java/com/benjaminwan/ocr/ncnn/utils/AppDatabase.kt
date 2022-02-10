package com.benjaminwan.ocr.ncnn.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.benjaminwan.ocr.ncnn.RecordActivity
import com.benjaminwan.ocr.ncnn.dao.RecordsDao
import com.benjaminwan.ocr.ncnn.models.Records
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(Records::class), version = 1, exportSchema = true)
public abstract class RecordsRoomDatabase : RoomDatabase() {

    abstract fun recordsDao(): RecordsDao


    private class RecordsDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var RecordsDao = database.recordsDao()

                }
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: RecordsRoomDatabase? = null

        @Synchronized
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RecordsRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsRoomDatabase::class.java,
                    "records_database"
                ).allowMainThreadQueries()
                    .addCallback(RecordsDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }


    }

}