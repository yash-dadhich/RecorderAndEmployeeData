package com.task.recordertaskapp.api

import android.telecom.Call
import retrofit2.http.GET

// ApiService.kt
interface ApiService {
    @GET("your-endpoint")
    fun getYourData(): Call<YourResponseType>
}
