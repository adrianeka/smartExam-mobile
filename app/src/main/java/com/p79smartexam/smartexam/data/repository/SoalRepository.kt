package com.p79smartexam.smartexam.data.repository

import android.util.Log
import com.p79smartexam.smartexam.data.local.SoalDao
import com.p79smartexam.smartexam.data.remote.ApiService
import com.p79smartexam.smartexam.data.remote.AnswersItem
import com.p79smartexam.smartexam.data.remote.SubmitJawabanRequest
import com.p79smartexam.smartexam.model.Soal
import kotlinx.coroutines.flow.Flow
import retrofit2.awaitResponse

class SoalRepository(
    private val soalDao: SoalDao,
    private val apiService: ApiService
) {

    fun getAllSoal(): Flow<List<Soal>> = soalDao.getAll()
    fun getSoalPilihan(): Flow<List<Soal>> = soalDao.getSoalByType(1)
    fun getSoalEssai(): Flow<List<Soal>> = soalDao.getSoalByType(2)
    fun getUnsyncedAnswers(): Flow<List<Soal>> = soalDao.getUnsyncedAnswers()
    fun getSyncedAnswers(): Flow<List<Soal>> = soalDao.getSyncedAnswers()

    suspend fun updateJawaban(soal: Soal) {
        soalDao.update(soal)
    }

    suspend fun fetchSoalFromApi(): Result<String> {
        return try {
            val response = apiService.getSoal().awaitResponse()
            if (response.isSuccessful) {
                val soalResponse = response.body()
                val dataItems = soalResponse?.data
                if (dataItems != null) {
                    soalDao.deleteAll()
                    
                    dataItems.filterNotNull().forEach { dataItem ->
                        val tipeSoal = dataItem.type ?: if (dataItem.options != null && 
                            (!dataItem.options.a.isNullOrEmpty() || !dataItem.options.b.isNullOrEmpty())) 1 else 2
                        
                        val pilihan = if (tipeSoal == 1) {
                            val opt = dataItem.options
                            listOfNotNull(opt?.a, opt?.b, opt?.c, opt?.d).joinToString(" ~ ")
                        } else {
                            ""
                        }

                        val soal = Soal(
                            id = dataItem.id?.toLong() ?: 0L,
                            soal = dataItem.question ?: "",
                            tipeSoal = tipeSoal,
                            pilihan = pilihan,
                            jawaban = "",
                            isSynced = false
                        )
                        soalDao.insert(soal)
                    }
                    Result.success("Soal diperbarui dari API")
                } else {
                    Result.failure(Exception("Gagal memproses data API"))
                }
            } else {
                Result.failure(Exception("Gagal mengambil soal (Server error)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitUnsyncedAnswers(unsyncedList: List<Soal>): Result<Unit> {
        try {
            if (unsyncedList.isEmpty()) {
                return Result.success(Unit)
            }

            val chunks = unsyncedList.chunked(50)
            for (chunk in chunks) {
                val answers = chunk.map {
                    AnswersItem(id = it.id.toInt(), answer = it.jawaban)
                }
                
                val request = SubmitJawabanRequest(answers = answers)
                val response = apiService.submitJawaban(request).awaitResponse()

                if (response.isSuccessful && response.body()?.success == true) {
                    chunk.forEach { 
                        soalDao.update(it.copy(isSynced = true))
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Server Error"
                    Log.e("SoalRepository", "Failed to submit: $errorMsg")
                    return Result.failure(Exception(errorMsg))
                }
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
