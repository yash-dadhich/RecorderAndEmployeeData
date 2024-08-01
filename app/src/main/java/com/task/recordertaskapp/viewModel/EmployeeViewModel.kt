package com.task.recordertaskapp.viewModel

import ApiResponse
import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.task.recordertaskapp.api.Repo
import com.task.recordertaskapp.models.EmployeeData
import kotlinx.coroutines.launch


class EmployeeViewModel : ViewModel() {
    private val repository = Repo()

    private val _employeeData = MutableLiveData<ApiResponse<EmployeeData>>()

    var employeeData : LiveData<ApiResponse<EmployeeData>>  = _employeeData

    fun getData() {
        viewModelScope.launch {
            _employeeData.postValue(ApiResponse.Loading)
            val response = repository.getData()
            _employeeData.postValue(response)

        }
    }
}