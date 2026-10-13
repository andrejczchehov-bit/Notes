package com.example.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynotes.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskAdapter(
            onTaskClick = { task ->
                val bundle = Bundle().apply {
                    putLong("taskId", task.id)
                }
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            },
            onTaskChecked = { task, isChecked ->
                viewModel.update(task.copy(isCompleted = isChecked))
            }
        )
        binding.recyclerTasks.adapter = adapter

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            if (tasks.isNullOrEmpty()) {
                binding.textEmptyState.visibility = View.VISIBLE
                binding.recyclerTasks.visibility = View.GONE
            } else {
                binding.textEmptyState.visibility = View.GONE
                binding.recyclerTasks.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
