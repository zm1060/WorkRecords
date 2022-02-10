package com.benjaminwan.ocr.ncnn.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.benjaminwan.ocr.ncnn.models.Records
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordsDao {
    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAll(): Flow<List<Records>>

    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<Records>>
    @Query("SELECT * FROM records WHERE date = :date")
    fun loadAllByDate(date: String): Flow<List<Records>>

    @Query("SELECT * FROM records WHERE status = :status")
    fun findByStatus(status: String): Flow<List<Records>>

    @Query("SELECT * FROM records WHERE about_address LIKE :about_address ")
    fun findByAddress(about_address: String?): Flow<List<Records>>

    @Insert
    suspend fun insert(vararg Records: Records)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(vararg Records: Records)

    @Delete
    suspend fun delete(vararg Records: Records)

    @Query("DELETE FROM records")
    fun deleteAll()
}


