package com.p79smartexam.smartexam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.p79smartexam.smartexam.data.repository.SoalRepository
import com.p79smartexam.smartexam.model.Soal
import com.p79smartexam.smartexam.util.NetworkConnectivityObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Filter {
    ALL, PILIHAN, ESSAI, UNSYNCED, SYNCED
}

data class TestUiState(
    val data: List<Soal> = emptyList(),
    val isLoading: Boolean = false,
    val saveStatus: String = "Siap",
    val isOnline: Boolean = true,
    val currentFilter: Filter = Filter.ALL
)

@OptIn(ExperimentalCoroutinesApi::class)
class TestViewModel(
    private val repository: SoalRepository,
    private val networkObserver: NetworkConnectivityObserver,
    private val startSubmitService: () -> Unit
) : ViewModel() {

    companion object {
        var hasFetched = false
    }

    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    private val saveJobs = mutableMapOf<Long, Job>()

    init {
        // Pantau status koneksi internet
        viewModelScope.launch {
            networkObserver.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }

        // Ambil data sesuai filter yang dipilih
        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                when (state.currentFilter) {
                    Filter.ALL -> repository.getAllSoal()
                    Filter.PILIHAN -> repository.getSoalPilihan()
                    Filter.ESSAI -> repository.getSoalEssai()
                    Filter.UNSYNCED -> repository.getUnsyncedAnswers()
                    Filter.SYNCED -> repository.getSyncedAnswers()
                }
            }.collect { list ->
                _uiState.update { it.copy(data = list) }
            }
        }
    }

    fun fetchSoalFromApi() {
        if (hasFetched) return
        hasFetched = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, saveStatus = "Mengambil soal...") }
            
            val result = repository.fetchSoalFromApi()
            result.onSuccess { msg ->
                _uiState.update { it.copy(isLoading = false, saveStatus = msg) }
            }.onFailure { _ ->
                _uiState.update { it.copy(isLoading = false, saveStatus = "Gagal memproses data API") }
            }
        }
    }

    fun retryFetch() {
        hasFetched = false
        fetchSoalFromApi()
    }

    fun setFilter(filter: Filter) {
        _uiState.update { it.copy(currentFilter = filter) }
    }

    fun updateJawaban(soal: Soal, jawaban: String) {
        saveJobs[soal.id]?.cancel()
        saveJobs[soal.id] = viewModelScope.launch {
            _uiState.update { it.copy(saveStatus = "Menunggu 3 detik...") }
            delay(3000)
            _uiState.update { it.copy(saveStatus = "Menyimpan...") }
            repository.updateJawaban(soal.copy(jawaban = jawaban, isSynced = false))
            _uiState.update { it.copy(saveStatus = "Tersimpan Lokal") }
        }
    }

    fun submitJawaban() {
        _uiState.update { it.copy(saveStatus = "Proses diserahkan ke background") }
        startSubmitService()
    }
}

class TestViewModelFactory(
    private val repository: SoalRepository,
    private val networkObserver: NetworkConnectivityObserver,
    private val startSubmitService: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestViewModel(repository, networkObserver, startSubmitService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
