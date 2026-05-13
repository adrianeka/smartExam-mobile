package com.p79smartexam.smartexam.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.p79smartexam.smartexam.model.Soal
import kotlinx.coroutines.flow.Flow

@Dao
interface SoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soal: Soal)

    @Update
    suspend fun update(soal: Soal)

    @Query("SELECT * FROM soal")
    fun getAll(): Flow<List<Soal>>

    @Query("SELECT * FROM soal WHERE tipeSoal = :tipeSoal")
    fun getSoalByType(tipeSoal: Int): Flow<List<Soal>>

    @Query("SELECT * FROM soal WHERE id = :id")
    suspend fun getById(id: Long): Soal?

    @Query("SELECT * FROM soal WHERE isSynced = 0")
    fun getUnsyncedAnswers(): Flow<List<Soal>>

    @Query("SELECT * FROM soal WHERE isSynced = 1")
    fun getSyncedAnswers(): Flow<List<Soal>>

    @Query("DELETE FROM soal WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM soal")
    suspend fun deleteAll()
}