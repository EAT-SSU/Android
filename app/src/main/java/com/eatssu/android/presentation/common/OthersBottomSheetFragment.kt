package com.eatssu.android.presentation.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eatssu.android.databinding.FragmentBottomsheetOthersBinding
import com.eatssu.android.presentation.review.report.ReportActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class OthersBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetOthersBinding? = null
    private val binding get() = _binding!!

    var reviewId = -1L
    var menu = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomsheetOthersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            reviewId = it.getLong("reviewId")
            menu = it.getString("menu").toString()
        }

        Timber.d("넘겨받은 리뷰 정보: $reviewId $menu")

        binding.llReport.setOnClickListener {

            val intent = Intent(requireContext(), ReportActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            Timber.d("reviewId $reviewId")
            startActivity(intent)
            dismiss()
        }
    }
}