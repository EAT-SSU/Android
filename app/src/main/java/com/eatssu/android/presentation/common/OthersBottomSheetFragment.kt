package com.eatssu.android.presentation.common

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eatssu.android.databinding.FragmentBottomsheetOthersBinding
import com.eatssu.android.presentation.base.FragmentCompanionWithArgs
import com.eatssu.android.presentation.cafeteria.review.report.ReportActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@AndroidEntryPoint
class OthersBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetOthersBinding? = null
    private val binding get() = _binding!!

    @Parcelize
    data class Args(
        val reviewId: Long,
        val menu: String
    ) : Parcelable

    companion object : FragmentCompanionWithArgs<Args>(
        ::OthersBottomSheetFragment,
        Args::class
    )

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

        val args = fragmentOptions ?: return

        Timber.d("넘겨받은 리뷰 정보: ${args.reviewId} ${args.menu}")

        binding.llReport.setOnClickListener {
            Timber.d("reviewId ${args.reviewId}")
            ReportActivity.start(
                requireContext(),
                ReportActivity.Args(args.reviewId)
            )
            dismiss()
        }
    }
}