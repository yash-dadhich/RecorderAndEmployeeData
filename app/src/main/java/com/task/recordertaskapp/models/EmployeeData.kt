package com.task.recordertaskapp.models

data class EmployeeData(
    val `data`: List<Data?>?,
    val message: String?,
    val status: String?
) {
    data class Data(
        val employee_age: Int?,
        val employee_name: String?,
        val employee_salary: Int?,
        val id: Int?,
        val profile_image: String?
    )
}