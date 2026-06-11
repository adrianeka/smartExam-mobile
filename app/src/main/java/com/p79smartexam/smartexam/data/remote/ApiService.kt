package com.p79smartexam.smartexam.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("questions")
    fun getSoal(): Call<GetSoalResponse>

    @POST("questions/submit")
    fun submitJawaban(
        @Body submitJawabanRequest: SubmitJawabanRequest
    ): Call<SubmitJawabanResponse>
}