package com.eatssu.android.presentation.mypage.myreview

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.databinding.ActivityMyReviewListBinding
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReviewListActivity :
    BaseActivity<ActivityMyReviewListBinding>(ActivityMyReviewListBinding::inflate) {

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    lateinit var menu: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내가 쓴 리뷰" // 툴바 제목 설정

        lodeReview()
    }

    private fun setAdapter(reviewList: List<Review>) {
        val listAdapter = MyReviewAdapter(reviewList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvReview.adapter = listAdapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
    }

    private fun lodeReview() {
        myReviewViewModel.getMyReviews()

        lifecycleScope.launch {
            myReviewViewModel.uiState.collectLatest {
                if (it.isEmpty) {
                    binding.llNonReview.visibility = View.VISIBLE
                    binding.nestedScrollView.visibility = View.GONE
                } else {
                    binding.llNonReview.visibility = View.GONE
                    binding.nestedScrollView.visibility = View.VISIBLE
                    it.myReviews?.let { reviews -> setAdapter(reviews) }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        lodeReview()
    }

    override fun onResume() {
        super.onResume()
        lodeReview()
    }
}