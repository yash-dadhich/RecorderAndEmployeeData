package com.task.recordertaskapp.api

import android.telecom.Call
import com.google.gson.JsonObject
import com.task.recordertaskapp.models.EmployeeData
import retrofit2.http.GET

// ApiService.kt
interface ApiService {
    @GET("employees")
    suspend fun getEmployeeData(): EmployeeData
}
