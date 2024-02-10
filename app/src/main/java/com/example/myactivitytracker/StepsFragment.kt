package com.example.myactivitytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myactivitytracker.databinding.FragmentStepsBinding

class StepsFragment : Fragment() {

    private lateinit var binding: FragmentStepsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStepsBinding.inflate(inflater, container, false)
        return binding.root
    }
}