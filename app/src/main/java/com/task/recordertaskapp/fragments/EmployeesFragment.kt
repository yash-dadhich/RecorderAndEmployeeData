package com.task.recordertaskapp.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.recordertaskapp.R
import com.task.recordertaskapp.adapters.EmployeesAdapter
import com.task.recordertaskapp.models.EmployeeData
import com.task.recordertaskapp.viewModel.EmployeeViewModel

class EmployeesFragment : Fragment() {

    private lateinit var viewModel: EmployeeViewModel
    private lateinit var progressBackground: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeesAdapter: EmployeesAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var employeeData: EmployeeData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_employees, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBackground = view.findViewById(R.id.progressBackground)
        recyclerView = view.findViewById(R.id.rvEmployee)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        sharedPreferences = requireActivity().getSharedPreferences("emp_prefs", AppCompatActivity.MODE_PRIVATE)

        employeeData = retrieveEmployeeData()
        if (employeeData?.data?.isNotEmpty() == true) {
            Log.d("TAG", "onViewCreated: $employeeData")
            // Use cached data
            setupRecyclerView(employeeData!!)
        } else {
            // Fetch new data from API
            viewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)
            viewModel.getData()
            viewModel.employeeData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        progressBackground.visibility = View.VISIBLE
                    }
                    is ApiResponse.Success -> {
                        progressBackground.visibility = View.GONE
                        saveEmployeeData(response.data)
                        setupRecyclerView(response.data)
                    }
                    is ApiResponse.Error -> {
                        progressBackground.visibility = View.GONE
                        Snackbar.make(view, "Error: ${response.exception.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> { }
                }
            }
        }
    }

    private fun setupRecyclerView(data: EmployeeData) {
        employeesAdapter = EmployeesAdapter(
            employeeList = data.data ?: listOf(),
            onDeleteClick = { position ->
                deleteEmployee(position)
            }
        )
        recyclerView.adapter = employeesAdapter
    }

    private fun saveEmployeeData(employeeData: EmployeeData) {
        val gson = Gson()
        val json = gson.toJson(employeeData)
        sharedPreferences.edit().putString("EMP_DATA", json).apply()
    }

    private fun retrieveEmployeeData(): EmployeeData? {
        val gson = Gson()
        val json = sharedPreferences.getString("EMP_DATA", null)
        return if (json != null) {
            val type = object : TypeToken<EmployeeData>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    private fun deleteEmployee(position: Int) {
        employeeData?.let {
            val updatedList = it.data?.toMutableList()
            if (updatedList != null && position >= 0 && position < updatedList.size) {
                updatedList.removeAt(position)
                val updatedEmployeeData = EmployeeData(
                    data = updatedList,
                    message = it.message, // Keep the existing message
                    status = it.status  // Keep the existing status
                )
                saveEmployeeData(updatedEmployeeData)
                // Update the adapter with the new list
                employeesAdapter.updateEmployeeList(updatedList)
                employeeData = updatedEmployeeData
            }
        }
    }
}
