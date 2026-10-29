package com.example.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class FirstFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    MainTaskAppScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { taskId ->
                            val bundle = Bundle().apply {
                                putLong("taskId", taskId)
                            }
                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
                        }
                    )
                }
            }
        }
    }
}
