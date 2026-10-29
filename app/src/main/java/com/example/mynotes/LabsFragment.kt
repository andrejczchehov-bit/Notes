package com.example.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LabsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    LabsContainerScreen(
                        onBackToTasks = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}
