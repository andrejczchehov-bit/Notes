package com.example.mynotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskChecked: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.textTitle.text = task.title
            binding.textDescription.text = task.description
            
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            var dueDateText = "Срок: ${sdf.format(Date(task.dueDate))}"
            if (task.reminderTime > 0) {
                dueDateText += " | 🔔 ${sdf.format(Date(task.reminderTime))}"
            }
            binding.textDueDate.text = dueDateText
            
            binding.checkboxCompleted.isChecked = task.isCompleted
            
            val priorityColor = when (task.priority) {
                3 -> android.R.color.holo_red_dark // High
                2 -> android.R.color.holo_orange_dark // Medium
                else -> android.R.color.holo_green_dark // Low
            }
            binding.priorityIndicator.setBackgroundColor(ContextCompat.getColor(binding.root.context, priorityColor))

            binding.root.setOnClickListener { onTaskClick(task) }
            binding.checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (task.isCompleted != isChecked) {
                    onTaskChecked(task, isChecked)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}
