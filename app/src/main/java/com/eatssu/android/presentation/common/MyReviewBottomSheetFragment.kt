package com.eatssu.android.presentation.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentBottomsheetMyReviewBinding
import com.eatssu.android.presentation.mypage.myreview.MyReviewViewModel
import com.eatssu.android.presentation.review.modify.ModifyReviewActivity
import com.eatssu.android.presentation.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyReviewBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetMyReviewBinding? = null
    private val binding get() = _binding!!

    interface OnReviewDeletedListener {
        fun onReviewDeleted()
    }

    var onReviewDeletedListener: OnReviewDeletedListener? = null

    private val viewModel: MyReviewViewModel by activityViewModels()

    var reviewId = -1L
    var menu = ""
    var content = ""
    var mainGrade = -1
    var amountGrade = -1
    var tasteGrade = -1

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

        arguments?.let {
            reviewId = it.getLong("reviewId")
            menu = it.getString("menu").toString()
            content = it.getString("content").toString()
            mainGrade = it.getInt("mainGrade")
            amountGrade = it.getInt("amountGrade")
            tasteGrade = it.getInt("tasteGrade")
        }

        Timber.d("넘겨받은 리뷰 정보: $reviewId $menu $content $reviewId")

        binding.llModify.setOnClickListener {
            val intent = Intent(requireContext(), ModifyReviewActivity::class.java)

            intent.let {
                it.putExtra("reviewId", reviewId)
                it.putExtra("menu", menu)
                it.putExtra("content", content)
                it.putExtra("mainGrade", mainGrade)
                it.putExtra("amountGrade", amountGrade)
                it.putExtra("tasteGrade", tasteGrade)
            }

            startActivity(intent)
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
                    viewModel.deleteReview(reviewId)
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