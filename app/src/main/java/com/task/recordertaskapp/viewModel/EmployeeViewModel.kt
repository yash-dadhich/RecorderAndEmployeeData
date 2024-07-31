package com.task.recordertaskapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.task.recordertaskapp.api.Repo
import com.task.recordertaskapp.api.Resource


class EmployeeViewModel : ViewModel() {
    private val repository = Repo()
    val data: LiveData<Resource<YourResponseType>> = repository.fetchData()
}
