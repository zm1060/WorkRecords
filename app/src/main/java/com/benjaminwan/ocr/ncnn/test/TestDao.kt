package com.benjaminwan.ocr.ncnn.test

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benjaminwan.ocr.ncnn.dao.RecordsDao
import com.benjaminwan.ocr.ncnn.models.Records
import com.benjaminwan.ocr.ncnn.utils.RecordsRoomDatabase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var recordsDao: RecordsDao
    private lateinit var db: RecordsRoomDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // 由于当进程结束的时候会清除这里的数据，所以使用内存数据库
        db = Room.inMemoryDatabaseBuilder(context, RecordsRoomDatabase::class.java)
            // 可以在主线程中发起请求，仅用于测试。
            .allowMainThreadQueries()
            .build()
        recordsDao = db.recordsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }



}