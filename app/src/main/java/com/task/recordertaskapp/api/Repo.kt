package com.task.recordertaskapp.api


import ApiResponse
import com.task.recordertaskapp.models.EmployeeData


// YourRepository.kt
class Repo {

    private val apiService: ApiService = RetrofitInstance.api

//    suspend fun fetchData(): JsonObject {
//        return apiService.getEmployeeData()
//    }


    suspend fun getData(): ApiResponse<EmployeeData> {
        return try {
            val response = RetrofitInstance.api.getEmployeeData()
            ApiResponse.Success(response)
        } catch (e: Exception) {
            ApiResponse.Error(e)
        }
    }
}
