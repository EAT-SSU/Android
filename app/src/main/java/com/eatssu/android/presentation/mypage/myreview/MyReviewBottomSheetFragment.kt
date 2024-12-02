package com.eatssu.android.presentation.mypage.myreview

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

        val intent = Intent(requireContext(), ModifyReviewActivity::class.java)

//        reviewId = intent.getLongExtra("reviewId", -1L)
//        menu = intent.getStringExtra("menu").toString()
//        content = intent.getStringExtra("content").toString()
//        mainGrade = intent.getIntExtra("mainGrade", -1)
//        amountGrade = intent.getIntExtra("amountGrade", -1)
//        tasteGrade = intent.getIntExtra("tasteGrade", -1)

        reviewId = arguments?.getLong("reviewId")!!
        menu = arguments?.getString("menu").toString()
        content = arguments?.getString("content").toString()

        mainGrade = arguments?.getInt("mainGrade")!!
        amountGrade = arguments?.getInt("amountGrade")!!
        tasteGrade = arguments?.getInt("tasteGrade")!!



        Timber.d("전:" + reviewId.toString())
        Timber.d("전:" + menu)
        Timber.d("전:" + content)
        Timber.d(reviewId.toString())

        binding.llModify.setOnClickListener {
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            intent.putExtra("content", content)
            intent.putExtra("mainGrade", mainGrade)
            intent.putExtra("amountGrade", amountGrade)
            intent.putExtra("tasteGrade", tasteGrade)

            startActivity(intent)
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
//                                finish()
                            }
                        }
                    }
                }
            }.create().show()

        }
    }
}