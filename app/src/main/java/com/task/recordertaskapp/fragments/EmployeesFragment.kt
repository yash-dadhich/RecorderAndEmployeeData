package com.task.recordertaskapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.task.recordertaskapp.R
import com.task.recordertaskapp.adapters.EmployeesAdapter
import com.task.recordertaskapp.models.EmployeeData
import com.task.recordertaskapp.viewModel.EmployeeViewModel

class EmployeesFragment : Fragment() {

    private lateinit var viewModel: EmployeeViewModel
    private lateinit var progressBackground: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeesAdapter: EmployeesAdapter

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

        viewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)
        viewModel.getData()
        viewModel.employeeData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    progressBackground.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    progressBackground.visibility = View.GONE
                    Snackbar.make(view, "Success", Snackbar.LENGTH_LONG).show()
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

    private fun setupRecyclerView(data: EmployeeData) {
        employeesAdapter = EmployeesAdapter(
            employeeList = data.data,
            onDeleteClick = { employee ->
                // Implement delete logic if needed

            }
        )
        recyclerView.adapter = employeesAdapter
    }
}
