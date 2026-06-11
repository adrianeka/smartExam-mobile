package com.p79smartexam.smartexam.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


object Retrofit {

    private const val BASE_URL = "https://unjaded-unapplicably-eddy.ngrok-free.dev/api/"

    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                }.build())
            }.also {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                it.addInterceptor(logging)
            }.build()
    }

    fun getInstance(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}