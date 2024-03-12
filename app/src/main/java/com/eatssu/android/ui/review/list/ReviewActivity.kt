package com.eatssu.android.ui.review.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewBinding
import com.eatssu.android.ui.review.write.ReviewWriteMenuActivity
import com.eatssu.android.ui.review.write.ReviewWriteRateActivity
import com.eatssu.android.util.RetrofitImpl.retrofit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class ReviewActivity :
    BaseActivity<ActivityReviewBinding>(ActivityReviewBinding::inflate) {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewService: ReviewService

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()

    private lateinit var itemName: String
    var adapter: ReviewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰" // 툴바 제목 설정

        reviewService = retrofit.create(ReviewService::class.java)
        viewModel =
            ViewModelProvider(
                this,
                ReviewViewModelFactory(reviewService)
            )[ReviewViewModel::class.java]

        getIndex()
        setClickListener()
        bindData()
    }

    private fun getIndex() {
        //get menuId
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Log.d("ReviewActivity", "메뉴는 ${itemName}")
        Log.d("ReviewActivity", "메뉴는 ${menuType} ${itemId}")
    }

    private fun setClickListener() {
        when (menuType) {
            "FIXED" -> {
                viewModel.loadReviewList(MenuType.FIXED, 0, itemId)
                viewModel.loadMenuReviewInfo(itemId)

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

            "VARIABLE" -> {
                viewModel.loadReviewList(MenuType.VARIABLE, itemId, 0)
                viewModel.loadMealReviewInfo(itemId)
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
    }

    private fun bindData() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {

                    if (it.isEmpty) {
                        Log.d("ReviewListActivity", "리뷰가 없음")
                        binding.llNonReview.visibility = View.VISIBLE
                        binding.rvReview.visibility = View.INVISIBLE
                    } else { //리뷰 있다.
                        binding.llNonReview.visibility = View.INVISIBLE
                        binding.rvReview.visibility = View.VISIBLE
                        adapter = ReviewAdapter(it.reviewList)

                        binding.rvReview.apply {
                            adapter = adapter
                            layoutManager = LinearLayoutManager(applicationContext)
                            setHasFixedSize(true)
                        }


                        it.reviewInfo?.apply {
                            binding.tvMenu.text = name.replace(Regex("[\\[\\]]"), "")

                            binding.tvReviewNumCount.text = reviewCnt.toString()

                            binding.tvRate.text =
                                String.format("%.1f", mainRating.toFloat())
                                    .toFloat().toString()
                            binding.tvGradeTaste.text =
                                String.format("%.1f", tasteRating.toFloat())
                                    .toFloat().toString()
                            binding.tvGradeAmount.text =
                                String.format("%.1f", amountRating.toFloat())
                                    .toFloat().toString()

                            val totalReviewCount = reviewCnt
                            binding.progressBar1.max = totalReviewCount
                            binding.progressBar2.max = totalReviewCount
                            binding.progressBar3.max = totalReviewCount
                            binding.progressBar4.max = totalReviewCount
                            binding.progressBar5.max = totalReviewCount

                            binding.progressBar1.progress = one
                            binding.progressBar2.progress = two
                            binding.progressBar3.progress = three
                            binding.progressBar4.progress = four
                            binding.progressBar5.progress = five
                        }
                    }
                }
            }
        }
    }
}
