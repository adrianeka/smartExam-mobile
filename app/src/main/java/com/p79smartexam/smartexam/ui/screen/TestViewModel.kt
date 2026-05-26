package com.p79smartexam.smartexam.ui.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.p79smartexam.smartexam.database.DummyData
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

@OptIn(ExperimentalCoroutinesApi::class)
class TestViewModel(private val dao: SoalDao, context: Context) : ViewModel() {

    enum class Filter {
        ALL, PILIHAN, ESSAI, UNSYNCED, SYNCED
    }

    val currentFilter = MutableStateFlow(Filter.ALL)

    private val _saveStatus = MutableStateFlow("Siap")
    val saveStatus: StateFlow<String> = _saveStatus.asStateFlow()

    // Internet Check
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

    init {
        viewModelScope.launch {
            dao.deleteAll()
            DummyData.defaultDummyData.forEach { dao.insert(it) }
        }
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
        started = SharingStarted.WhileSubscribed(),
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
