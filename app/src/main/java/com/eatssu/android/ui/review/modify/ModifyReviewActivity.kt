package com.eatssu.android.ui.review.modify

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityFixMenuBinding
import com.eatssu.android.util.extension.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ModifyReviewActivity : BaseActivity<ActivityFixMenuBinding>(ActivityFixMenuBinding::inflate) {

    private lateinit var viewModel: ModifyViewModel
    private var reviewId = -1L
    private lateinit var menu: String
    private var comment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 수정하기" // 툴바 제목 설정

        initViewModel()
        getIndex()
        setOnClickListener()
        observeViewModel()
    }

    fun setOnClickListener() {
        binding.btnDone.setOnClickListener {
            postData(reviewId)

            lifecycleScope.launch {
                viewModel.uiState.collectLatest {
                    if (!it.error && !it.loading) {
                        showToast(it.toastMessage)
                        finish()
                    }
                    if (it.error) {
                        showToast(it.toastMessage)
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ModifyViewModel::class.java]

    }

    private fun getIndex() {
        reviewId = intent.getLongExtra("reviewId", -1L)
        binding.menu.text = intent.getStringExtra("menu").toString()

        Log.d("ReviewFixedActivity", reviewId.toString())
    }

    private fun postData(reviewId: Long) {
        val comment = binding.etReview2Comment.text.toString()
        val mainGrade = binding.rbMain.rating.toInt()
        val amountGrade = binding.rbAmount.rating.toInt()
        val tasteGrade = binding.rbTaste.rating.toInt()

        viewModel.modifyMyReview(reviewId, comment, mainGrade, amountGrade, tasteGrade)
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    if (it.isDone) {
                        showToast(it.toastMessage)
                        finish()
                    }
                }
                if (it.error) {
                    showToast(it.toastMessage)
                }
            }
        }
    }
}