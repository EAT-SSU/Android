package com.eatssu.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eatssu.android.databinding.FragmentBreakfastBinding

class BreakfastFragment : Fragment() {
    private var _binding: FragmentBreakfastBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentBreakfastBinding.inflate(inflater, container, false)
        return binding.root
    }

}