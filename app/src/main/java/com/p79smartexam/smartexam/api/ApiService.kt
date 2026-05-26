package com.p79smartexam.smartexam.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("questions")
    fun getSoal(): Call<SoalResponse>
}