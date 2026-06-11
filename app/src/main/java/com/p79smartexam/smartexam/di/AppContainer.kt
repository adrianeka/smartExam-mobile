package com.p79smartexam.smartexam.di

import android.content.Context
import com.p79smartexam.smartexam.data.local.SmartExamDb
import com.p79smartexam.smartexam.data.remote.Retrofit
import com.p79smartexam.smartexam.data.repository.SoalRepository
import com.p79smartexam.smartexam.util.NetworkConnectivityObserver

interface AppContainer {
    val soalRepository: SoalRepository
    val networkObserver: NetworkConnectivityObserver
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy { SmartExamDb.getInstance(context) }
    private val apiService by lazy { Retrofit.getInstance() }
    
    override val networkObserver: NetworkConnectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }

    override val soalRepository: SoalRepository by lazy {
        SoalRepository(
            soalDao = database.dao,
            apiService = apiService
        )
    }
}
