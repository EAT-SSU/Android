package com.eatssu.android.view.review

import RetrofitImpl
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewListBinding
import com.eatssu.android.repository.ReviewListRepository
import com.eatssu.android.viewmodel.ReviewListViewModel
import com.eatssu.android.viewmodel.factory.ReviewListViewModelFactory
import kotlin.properties.Delegates

class ReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewListBinding

    private lateinit var viewModel: ReviewListViewModel
    private lateinit var reviewService: ReviewService

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.included.actionBar.text = "리뷰"

        supportActionBar?.title = "리뷰"


        reviewService = RetrofitImpl.retrofit.create(ReviewService::class.java)

        val repository = ReviewListRepository(reviewService)
        viewModel =
            ViewModelProvider(
                this,
                ReviewListViewModelFactory(repository)
            )[ReviewListViewModel::class.java]

        //get menuId
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
        Log.d("post", menuType + itemId)

        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)

                binding.btnNextReview.setOnClickListener() {
                    val intent = Intent(this, MenuPickActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)

                    startActivity(intent)  // 화면 전환을 시켜줌
                }
            }
            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)

                binding.btnNextReview.setOnClickListener() {
                    val intent = Intent(this, MenuPickActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)

                    startActivity(intent)  // 화면 전환을 시켜줌
                }

            }
            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }


        viewModel.reviewList.observe(this) { reviewList ->
            Log.d("post", reviewList.dataList.toString())
            val listAdapter = ReviewAdapter(reviewList)
            val recyclerView = binding.rvReview
            recyclerView.adapter = listAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        }

        viewModel.reviewInfo.observe(this) { reviewInfo ->
            Log.d("post", reviewInfo.toString())

            binding.tvMenu.text = reviewInfo.menuName.toString()

            binding.tvRate.text =
                String.format("%.1f", reviewInfo.mainGrade.toFloat())
                    .toFloat().toString()
            binding.tvGradeTaste.text =
                String.format("%.1f", reviewInfo.tasteGrade!!.toFloat())
                    .toFloat().toString()
            binding.tvGradeAmount.text =
                String.format("%.1f", reviewInfo.amountGrade!!.toFloat())
                    .toFloat().toString()

            binding.tvReviewNumCount.text = reviewInfo.totalReviewCount.toString()

            val cnt = reviewInfo.totalReviewCount

            binding.progressBar1.max = cnt
            binding.progressBar2.max = cnt
            binding.progressBar3.max = cnt
            binding.progressBar4.max = cnt
            binding.progressBar5.max = cnt

            binding.progressBar1.progress = reviewInfo.reviewGradeCnt.oneCnt
            binding.progressBar2.progress = reviewInfo.reviewGradeCnt.twoCnt
            binding.progressBar3.progress = reviewInfo.reviewGradeCnt.threeCnt
            binding.progressBar4.progress = reviewInfo.reviewGradeCnt.fourCnt
            binding.progressBar5.progress = reviewInfo.reviewGradeCnt.fiveCnt

        }
    }
}
