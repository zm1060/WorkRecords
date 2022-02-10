package com.benjaminwan.ocr.ncnn

import android.icu.text.AlphabeticIndex
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.benjaminwan.ocr.ncnn.dao.RecordsDao
import com.benjaminwan.ocr.ncnn.models.Records
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class RecordsRepository(private val RecordsDao: RecordsDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    private lateinit var allRecords: Flow<List<Records>>

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(Records: Records) {
        RecordsDao.insert(Records)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(Records: Records) {
        RecordsDao.update(Records)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(Records: Records) {
        RecordsDao.delete(Records)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun selectAll(): Flow<List<Records>> {
        allRecords = RecordsDao.getAll()
        return allRecords
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun searchByAddress(address: String) : Flow<List<Records>> {
        return RecordsDao.findByAddress(address)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun searchByStatus(status: String) : Flow<List<Records>>{
        return RecordsDao.findByStatus(status)
    }
}