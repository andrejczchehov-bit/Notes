package com.example.mynotes

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynotes.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedReminderDate: Calendar = Calendar.getInstance()
    private var isReminderSet: Boolean = false
    private var taskId: Long = -1L

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Разрешение на уведомления не предоставлено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskId = arguments?.getLong("taskId") ?: -1L
        if (taskId != -1L) {
            setupEditMode()
        }

        binding.buttonPickDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            isReminderSet = isChecked
            if (isChecked) {
                binding.buttonPickReminder.visibility = View.VISIBLE
                binding.textReminderDate.visibility = View.VISIBLE
                updateReminderDateText()
                checkNotificationPermission()
            } else {
                binding.buttonPickReminder.visibility = View.GONE
                binding.textReminderDate.visibility = View.GONE
            }
        }

        binding.buttonPickReminder.setOnClickListener {
            showReminderDateTimePicker()
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }

        binding.buttonDelete.setOnClickListener {
            deleteTask()
        }
        
        updateDateText()
        updateReminderDateText()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupEditMode() {
        binding.buttonDelete.visibility = View.VISIBLE
        
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val task = db.taskDao().getTaskById(taskId)
            task?.let {
                binding.editTitle.setText(it.title)
                binding.editDescription.setText(it.description)
                selectedDate.timeInMillis = it.dueDate
                updateDateText()
                when (it.priority) {
                    3 -> binding.radioHigh.isChecked = true
                    2 -> binding.radioMedium.isChecked = true
                    else -> binding.radioLow.isChecked = true
                }

                if (it.reminderTime > 0) {
                    isReminderSet = true
                    selectedReminderDate.timeInMillis = it.reminderTime
                    binding.switchReminder.isChecked = true
                    binding.buttonPickReminder.visibility = View.VISIBLE
                    binding.textReminderDate.visibility = View.VISIBLE
                    updateReminderDateText()
                }
            }
        }
    }

    private fun showDateTimePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDate.set(Calendar.MINUTE, minute)
                        updateDateText()
                    },
                    selectedDate.get(Calendar.HOUR_OF_DAY),
                    selectedDate.get(Calendar.MINUTE),
                    true
                ).show()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showReminderDateTimePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedReminderDate.set(Calendar.YEAR, year)
                selectedReminderDate.set(Calendar.MONTH, month)
                selectedReminderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        selectedReminderDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedReminderDate.set(Calendar.MINUTE, minute)
                        updateReminderDateText()
                    },
                    selectedReminderDate.get(Calendar.HOUR_OF_DAY),
                    selectedReminderDate.get(Calendar.MINUTE),
                    true
                ).show()
            },
            selectedReminderDate.get(Calendar.YEAR),
            selectedReminderDate.get(Calendar.MONTH),
            selectedReminderDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateText() {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.textSelectedDate.text = sdf.format(selectedDate.time)
    }

    private fun updateReminderDateText() {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.textReminderDate.text = sdf.format(selectedReminderDate.time)
    }

    private fun saveTask() {
        val title = binding.editTitle.text.toString()
        val description = binding.editDescription.text.toString()
        val priority = when {
            binding.radioHigh.isChecked -> 3
            binding.radioMedium.isChecked -> 2
            else -> 1
        }

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "Введите название", Toast.LENGTH_SHORT).show()
            return
        }

        val reminderTime = if (isReminderSet) selectedReminderDate.timeInMillis else 0L

        val task = Task(
            id = if (taskId == -1L) 0 else taskId,
            title = title,
            description = description,
            dueDate = selectedDate.timeInMillis,
            priority = priority,
            reminderTime = reminderTime
        )

        if (taskId == -1L) {
            viewModel.insert(task)
        } else {
            viewModel.update(task)
        }

        // Schedule or cancel notification
        NotificationWorker.schedule(requireContext(), task)

        findNavController().popBackStack()
    }

    private fun deleteTask() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val task = db.taskDao().getTaskById(taskId)
            task?.let {
                NotificationWorker.cancel(requireContext(), it.id)
                viewModel.delete(it)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
