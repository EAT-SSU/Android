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
    private var menu = ""

    private var content = ""

    private var main = 0
    private var amount = 0
    private var taste = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 수정하기" // 툴바 제목 설정

        initViewModel()
        getIndex()
        setData()
        setOnClickListener()
        observeViewModel()
    }

    fun setOnClickListener() {
        binding.btnDone.setOnClickListener {
            postData(reviewId)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ModifyViewModel::class.java]

    }

    private fun getIndex() {

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()
        content = intent.getStringExtra("content").toString()

        main = intent.getIntExtra("mainGrade", 0)
        amount = intent.getIntExtra("amountGrade", 0)
        taste = intent.getIntExtra("tasteGrade", 0)

        Log.d("ReviewFixedActivity", reviewId.toString() + menu)
        Log.d("ReviewFixedActivity", content)
    }

    private fun setData() {
        binding.menu.text = menu
        binding.etReview2Comment.setText(content)
        binding.rbMain.rating = main.toFloat()
        binding.rbAmount.rating = intent.getIntExtra("amountGrade", 0).toFloat()
        binding.rbTaste.rating = intent.getIntExtra("tasteGrade", 0).toFloat()
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