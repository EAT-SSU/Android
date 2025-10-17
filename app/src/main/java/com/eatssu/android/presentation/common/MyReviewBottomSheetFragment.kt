package com.eatssu.android.presentation.common

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentBottomsheetMyReviewBinding
import com.eatssu.android.presentation.base.FragmentCompanionWithArgs
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyReviewActivity
import com.eatssu.android.presentation.mypage.myreview.MyReviewViewModel
import com.eatssu.android.presentation.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@AndroidEntryPoint
class MyReviewBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetMyReviewBinding? = null
    private val binding get() = _binding!!

    @Parcelize
    data class Args(
        val reviewId: Long,
        val menu: String,
        val content: String,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade: Int
    ) : Parcelable

    companion object : FragmentCompanionWithArgs<Args>(
        ::MyReviewBottomSheetFragment,
        Args::class
    )

    interface OnReviewDeletedListener {
        fun onReviewDeleted()
    }

    var onReviewDeletedListener: OnReviewDeletedListener? = null

    private val viewModel: MyReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomsheetMyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = fragmentOptions ?: return

        Timber.d("넘겨받은 리뷰 정보: ${args.reviewId} ${args.menu} ${args.content}")

        binding.llModify.setOnClickListener {
            ModifyReviewActivity.start(
                requireContext(),
                ModifyReviewActivity.Args(
                    reviewId = args.reviewId,
                    menu = args.menu,
                    content = args.content,
                    mainGrade = args.mainGrade,
                    amountGrade = args.amountGrade,
                    tasteGrade = args.tasteGrade
                )
            )
            dismiss()
        }

        binding.llDelete.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.delete)
                setMessage(R.string.delete_description)
                setNegativeButton("취소") { _, _ ->
                    activity?.showToast(App.appContext.getString(R.string.delete_undo))
                }
                setPositiveButton("삭제") { _, _ ->
                    viewModel.deleteReview(args.reviewId)
                    lifecycleScope.launch {
                        viewModel.uiState.collectLatest {
                            if (it.isDeleted) {
                                onReviewDeletedListener?.onReviewDeleted() // 콜백 호출
                                dismiss()
                            }
                        }
                    }
                }
            }.create().show()
        }

    }
}