package com.task.recordertaskapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.task.recordertaskapp.R
import com.task.recordertaskapp.models.EmployeeData

class EmployeesAdapter(
    private var employeeList: List<EmployeeData.Data?>?,
    private val onDeleteClick: (EmployeeData.Data) -> Unit
) : RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val titleTextView: TextView = itemView.findViewById(R.id.rvItemTitle)
        val rvItemId: TextView = itemView.findViewById(R.id.rvItemId)
        val rvEmpName: TextView = itemView.findViewById(R.id.rvEmpName)
        val rvEmpSalary: TextView = itemView.findViewById(R.id.rvEmpSalary)
        val rvEmpAge: TextView = itemView.findViewById(R.id.rvEmpAge)
        val deleteEmployeeData: ImageView = itemView.findViewById(R.id.deleteEmployeeData)
//        val playButton: ImageView = itemView.findViewById(R.id.play)
//        val deleteButton: ImageView = itemView.findViewById(R.id.deleteRecording)
//        val itemLayout: ConstraintLayout = itemView.findViewById(R.id.recording_item_layout)

        init {
            deleteEmployeeData.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    employeeList?.get(position)?.let { it1 ->
                        onDeleteClick(it1)
//                        employeeList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.employee_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employeeData = employeeList?.get(position)
        if (employeeData != null) {
            holder.rvItemId.text = employeeData.id.toString()
            holder.rvEmpName.text = employeeData.employee_name.toString()
            holder.rvEmpSalary.text = employeeData.employee_salary.toString()
            holder.rvEmpAge.text = employeeData.employee_age.toString()
        }
//        holder.itemLayout.setBackgroundColor(holder.itemView.context.getColor(employeeData.backgroundColor))
    }

    override fun getItemCount(): Int {
        return employeeList?.size ?: 0
    }
}
