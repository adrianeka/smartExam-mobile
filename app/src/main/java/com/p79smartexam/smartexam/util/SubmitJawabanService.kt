package com.p79smartexam.smartexam.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.p79smartexam.smartexam.R
import com.p79smartexam.smartexam.api.AnswersItem
import com.p79smartexam.smartexam.api.Retrofit
import com.p79smartexam.smartexam.api.SubmitJawabanRequest
import com.p79smartexam.smartexam.database.SmartExamDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class SubmitJawabanService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var isSyncing = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var isCallbackRegistered = false

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Sinkronisasi Aktif", "Menunggu jaringan untuk mengirim jawaban...", false)
        startForeground(888, notification)

        registerNetworkCallback()

        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            startSync()
        }

        return START_STICKY
    }

    private fun registerNetworkCallback() {
        if (isCallbackRegistered) return
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    startSync()
                }
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        isCallbackRegistered = true
    }

    private fun startSync() {
        if (isSyncing) return
        isSyncing = true

        serviceScope.launch {
            updateNotification("Memproses Sinkronisasi", "Sedang mengirim jawaban...", false)
            
            val dao = SmartExamDb.getInstance(applicationContext).dao
            val apiService = Retrofit.getInstance()

            try {
                val unsyncedList = dao.getUnsyncedAnswers().first()
                if (unsyncedList.isEmpty()) {
                    stopServiceSuccess()
                    return@launch
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
                            dao.update(it.copy(isSynced = true))
                        }
                    } else {
                        val errorMsg = response.body()?.message ?: "Server Error"
                        Log.e("SubmitJawabanService", "Failed to submit: $errorMsg")
                        updateNotification("Sinkronisasi Gagal", "Gagal mengirim: $errorMsg. Menunggu jaringan...", false)
                        isSyncing = false
                        return@launch 
                    }
                }

                stopServiceSuccess()

            } catch (e: Exception) {
                Log.e("SubmitJawabanService", "Error submitting", e)
                updateNotification("Sinkronisasi Gagal", "Koneksi/Error: Menunggu jaringan...", false)
                isSyncing = false
            }
        }
    }

    private fun stopServiceSuccess() {
        updateNotification("Sinkronisasi Sukses", "Berhasil mengirim semua jawaban.", true)
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun updateNotification(title: String, message: String, autoCancel: Boolean) {
        try {
            with(NotificationManagerCompat.from(this)) {
                notify(888, createNotification(title, message, autoCancel))
            }
        } catch (e: SecurityException) {
            Log.e("SubmitJawabanService", "Notification permission not granted", e)
        }
    }

    private fun createNotification(title: String, message: String, autoCancel: Boolean): Notification {
        val channelId = "sync_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sinkronisasi Jawaban",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(autoCancel)
            .setOngoing(!autoCancel)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        if (isCallbackRegistered) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
