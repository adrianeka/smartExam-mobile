package com.p79smartexam.smartexam.ui.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.p79smartexam.smartexam.api.Retrofit
import com.p79smartexam.smartexam.database.SoalDao
import com.p79smartexam.smartexam.model.Soal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

@OptIn(ExperimentalCoroutinesApi::class)
class TestViewModel(private val dao: SoalDao, context: Context) : ViewModel() {

    companion object {
        var hasFetched = false
    }

    enum class Filter {
        ALL, PILIHAN, ESSAI, UNSYNCED, SYNCED
    }

    val currentFilter = MutableStateFlow(Filter.ALL)

    private val _saveStatus = MutableStateFlow("Siap")
    val saveStatus: StateFlow<String> = _saveStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val apiService = Retrofit.getInstance()

    // Connectivity Observation
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    val isOnline: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Initial state
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        trySend(caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    fun fetchSoalFromApi() {
        if (hasFetched) return
        hasFetched = true

        viewModelScope.launch {
            _isLoading.value = true
            _saveStatus.value = "Mengambil soal..."
            try {
                val response = apiService.getSoal().awaitResponse()
                if (response.isSuccessful) {
                    val soalResponse = response.body()
                    val dataItems = soalResponse?.data
                    if (dataItems != null) {
                        dao.deleteAll()
                        
                        dataItems.filterNotNull().forEach { dataItem ->
                            val tipeSoal = dataItem.type!!
                            
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
                            dao.insert(soal)
                        }
                        _saveStatus.value = "Soal diperbarui dari API"
                    } else {
                        _saveStatus.value = "Gagal memproses data API"
                    }
                } else {
                    _saveStatus.value = "Gagal mengambil soal (Server error)"
                }
            } catch (e: Exception) {
                Log.e("TestViewModel", "Error fetching from API", e)
                _saveStatus.value = "Offline / Gagal mengambil soal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryFetch() {
        hasFetched = false
        fetchSoalFromApi()
    }

    val data: StateFlow<List<Soal>> = currentFilter.flatMapLatest { f ->
        when (f) {
            Filter.ALL -> dao.getAll()
            Filter.PILIHAN -> dao.getSoalByType(1)
            Filter.ESSAI -> dao.getSoalByType(2)
            Filter.UNSYNCED -> dao.getUnsyncedAnswers()
            Filter.SYNCED -> dao.getSyncedAnswers()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun setFilter(filter: Filter) {
        currentFilter.value = filter
    }

    fun updateJawaban(soal: Soal, jawaban: String) {
        viewModelScope.launch {
            _saveStatus.value = "Menyimpan..."
            dao.update(soal.copy(jawaban = jawaban, isSynced = false))
            delay(500)
            _saveStatus.value = "Tersimpan Lokal"
        }
    }
}

class TestViewModelFactory(private val dao: SoalDao, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestViewModel(dao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
