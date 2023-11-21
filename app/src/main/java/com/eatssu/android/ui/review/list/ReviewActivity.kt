package com.eatssu.android.ui.review.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.repository.ReviewRepository
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewBinding
import com.eatssu.android.ui.review.write.ReviewWriteMenuActivity
import com.eatssu.android.ui.review.write.ReviewWriteRateActivity
import com.eatssu.android.util.RetrofitImpl.retrofit
import kotlin.properties.Delegates

class ReviewActivity :
    BaseActivity<ActivityReviewBinding>(ActivityReviewBinding::inflate) {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewService: ReviewService
    private lateinit var repository: ReviewRepository

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()

    private lateinit var itemName: String
    var adapter: ReviewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰" // 툴바 제목 설정

        reviewService = retrofit.create(ReviewService::class.java)
        repository = ReviewRepository(reviewService)
        viewModel =
            ViewModelProvider(this, ReviewViewModelFactory(repository))[ReviewViewModel::class.java]

        //get menuId
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Log.d("ReviewActivity", "메뉴는 ${itemName}")
        Log.d("ReviewActivity", "메뉴는 ${menuType} ${itemId}")

        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)

                binding.btnNextReview.setOnClickListener {
                    val intent = Intent(this, ReviewWriteRateActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)
//                    intent.putStringArrayListExtra("itemName", itemName)
                    intent.putExtra("itemName", itemName)
                    intent.putExtra("menuType", menuType)
                    startActivity(intent)  // 화면 전환을 시켜줌
                }
            }

            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)
                val mealId = itemId

                binding.btnNextReview.setOnClickListener {
                    val intent = Intent(this, ReviewWriteMenuActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("mealId", mealId)
                    intent.putExtra("menuType", menuType)
//                    intent.putStringArrayListExtra("menuNameArray", menuNameArray)
//                    intent.putExtra("menuIdArray", menuIdArray)
//
//                    if (menuNameArray != null) {
//                        Log.d("post", (menuNameArray + menuIdArray).toString())
//                    }

                    startActivity(intent)  // 화면 전환을 시켜줌
                }
            }

            else -> {
                Log.d("ReviewActivity", "잘못된 식당 정보입니다.")
            }
        }

        setInfoData()
        setListData()
    }

    private fun setListData() {
        viewModel.reviewList.observe(this) { reviewList ->
            Log.d("ReviewActivity", reviewList.dataList.toString())

            if (reviewList.numberOfElements == 0) {
                Log.d("ReviewListActivity","리뷰가 없음")
                binding.llNonReview.visibility = View.VISIBLE
                binding.rvReview.visibility = View.INVISIBLE
            } else {
                binding.llNonReview.visibility = View.INVISIBLE
                binding.rvReview.visibility = View.VISIBLE
                adapter = ReviewAdapter(reviewList)
                val recyclerView = binding.rvReview
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.setHasFixedSize(true)
//                recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))//구분선 주석처리

            }
        }
    }

    private fun setInfoData() {

        viewModel.reviewInfo.observe(this) { reviewInfo ->
            Log.d("post", reviewInfo.toString())

            binding.tvMenu.text = reviewInfo.menuName.toString().replace(Regex("[\\[\\]]"), "")

            binding.tvRate.text =
                String.format("%.1f", reviewInfo.mainGrade.toFloat())
                    .toFloat().toString()
            binding.tvGradeTaste.text =
                String.format("%.1f", reviewInfo.tasteGrade.toFloat())
                    .toFloat().toString()
            binding.tvGradeAmount.text =
                String.format("%.1f", reviewInfo.amountGrade.toFloat())
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

    override fun onRestart() {
        super.onRestart()

        Log.d("post", "onRestart")

        // 다시 데이터를 로드하고 어댑터를 업데이트
        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)
            }

            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)
            }

            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }
        Log.d("post", "onRestart")

        setInfoData()

        Log.d("post", "onRestart" + viewModel.reviewInfo.value)

        setListData()

        Log.d("post", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("post", "resume")

        // 다시 데이터를 로드하고 어댑터를 업데이트
        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)
            }

            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)
            }

            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }
        Log.d("post", "resume시작")

        setInfoData()

        Log.d("post", "resume중간")

        setListData()

        Log.d("post", "resume끝")
    }
}
