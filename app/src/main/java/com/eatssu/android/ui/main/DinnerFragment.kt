package com.eatssu.android.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eatssu.android.databinding.FragmentDinnerBinding
import com.eatssu.android.ui.infopage.*

class DinnerFragment : Fragment() {
    private var _binding: FragmentDinnerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentDinnerBinding.inflate(inflater, container, false)

        binding.btnHaksikInfo.setOnClickListener{
            val intent = Intent(context, InfoActivity_Haksik::class.java);
            startActivity(intent);
        }
        binding.btnDodamInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Dodam::class.java);
            startActivity(intent);
        }
        binding.btnGisikInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Gisik::class.java);
            startActivity(intent);
        }
        binding.btnKitchenInfo.setOnClickListener{
            val intent = Intent(context, InfoActivity_Kitchen::class.java);
            startActivity(intent);
        }
        binding.btnFoodInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Food::class.java);
            startActivity(intent);
        }
        binding.btnSnackInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Snack::class.java);
            startActivity(intent);
        }
        return binding.root
    }

}