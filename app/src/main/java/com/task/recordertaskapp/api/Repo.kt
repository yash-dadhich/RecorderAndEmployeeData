package com.task.recordertaskapp.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// YourRepository.kt
class Repo {

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    fun fetchData(): LiveData<Resource<YourResponseType>> {
        val data = MutableLiveData<Resource<YourResponseType>>()
        apiService.getYourData().enqueue(object : Callback<YourResponseType> {
            override fun onResponse(call: Call<YourResponseType>, response: Response<YourResponseType>) {
                if (response.isSuccessful) {
                    data.value = Resource.success(response.body())
                } else {
                    data.value = Resource.error("Error: ${response.message()}", null)
                }
            }

            override fun onFailure(call: Call<YourResponseType>, t: Throwable) {
                data.value = Resource.error("Failure: ${t.message}", null)
            }
        })
        return data
    }
}
