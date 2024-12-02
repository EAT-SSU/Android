package com.eatssu.android.presentation.mypage.myreview

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyReviewListBinding
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.common.MyReviewBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReviewListActivity :
    BaseActivity<ActivityMyReviewListBinding>(ActivityMyReviewListBinding::inflate),
    MyReviewBottomSheetFragment.OnReviewDeletedListener {

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    lateinit var menu: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내가 쓴 리뷰" // 툴바 제목 설정

        lodeReview()
    }

    private fun setAdapter(reviewList: List<Review>) {

        val adapter = MyReviewAdapter()
        adapter.submitList(reviewList)

        val linearLayoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener(object :
            MyReviewAdapter.OnItemClickListener {

            override fun onMyReviewClicked(view: View, reviewData: Review) {
                onMyReviewClicked(review = reviewData)
            }
        })

        binding.rvReview.adapter = adapter
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

    fun onMyReviewClicked(review: Review) {

        val modalBottomSheet = MyReviewBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putLong("reviewId", review.reviewId)
                putString("menu", review.menu)
                putString("content", review.content)
                putInt("mainGrade", review.mainGrade)
                putInt("amountGrade", review.amountGrade)
                putInt("tasteGrade", review.tasteGrade)
            }
            onReviewDeletedListener = this@MyReviewListActivity
        }
        modalBottomSheet.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
        modalBottomSheet.show(supportFragmentManager, "Open Bottom Sheet")
    }

    override fun onReviewDeleted() {
        lodeReview()
    }

}