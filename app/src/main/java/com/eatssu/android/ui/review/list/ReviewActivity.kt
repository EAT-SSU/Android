package com.eatssu.android.ui.review.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.repository.ReviewRepository
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewBinding
import com.eatssu.android.ui.review.delete.DeleteViewModel
import com.eatssu.android.ui.review.write.ReviewWriteRateActivity
import com.eatssu.android.ui.review.write.menu.ReviewWriteMenuActivity
import com.eatssu.android.util.RetrofitImpl.retrofit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReviewActivity :
    BaseActivity<ActivityReviewBinding>(ActivityReviewBinding::inflate) {

//    private val viewModel: ReviewViewModel by viewModels()

    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var deleteViewModel: DeleteViewModel

    private lateinit var reviewService: ReviewService
    private lateinit var reviewRepository: ReviewRepository

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()

    private lateinit var itemName: String
    private var reviewAdapter: ReviewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰" // 툴바 제목 설정

        initViewModel()
        getIndex()
        lodeData()
        bindData()
        setClickListener()

    }

    private fun initViewModel() {
        reviewService = retrofit.create(ReviewService::class.java)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        reviewRepository = ReviewRepository(reviewService)
        reviewViewModel =
            ViewModelProvider(
                this,
                ReviewViewModelFactory(
                    reviewService,
//                    reviewRepository
                )
            )[ReviewViewModel::class.java]

        binding.viewModel = reviewViewModel

        deleteViewModel = ViewModelProvider(this).get(DeleteViewModel::class.java)

    }

    private fun lodeData() {
        when (menuType) {
            "FIXED" -> {
                reviewViewModel.loadReviewList(MenuType.FIXED, 0, itemId)
                reviewViewModel.loadMenuReviewInfo(itemId)
            }

            "VARIABLE" -> {
                reviewViewModel.loadReviewList(MenuType.VARIABLE, itemId, 0)
                reviewViewModel.loadMealReviewInfo(itemId)
            }

            else -> {
                Log.d("ReviewActivity", "잘못된 식당 정보입니다.")
            }
        }
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
                binding.btnNextReview.setOnClickListener {
                    val intent = Intent(this, ReviewWriteRateActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("itemName", itemName)
                    intent.putExtra("menuType", menuType)
                    startActivity(intent)
                }
            }

            "VARIABLE" -> {
                binding.btnNextReview.setOnClickListener {
                    val intent = Intent(this, ReviewWriteMenuActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)
                    startActivity(intent)
                }
            }

            else -> {
                Log.d("ReviewActivity", "잘못된 식당 정보입니다.")
            }
        }
    }

    private fun bindData() {
        lifecycleScope.launch {
            reviewViewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    if (it.isEmpty) {

                        //리뷰 없어도 메뉴명은 있음
                        it.reviewInfo?.apply {
                            binding.tvMenu.text = name.replace(Regex("[\\[\\]]"), "")
                        }

                        Log.d("ReviewActivity", "리뷰가 없음")
                        binding.llNonReview.visibility = View.VISIBLE
                        binding.rvReview.visibility = View.INVISIBLE

                    } else { //리뷰 있다.

                        Log.d("ReviewActivity", "리뷰가 있음")
                        binding.llNonReview.visibility = View.INVISIBLE
                        binding.rvReview.visibility = View.VISIBLE
                        reviewAdapter = it.reviewList?.let { review ->
                            ReviewAdapter(review) { reviewId -> delete(reviewId) }
                        }

                        binding.rvReview.apply {
                            adapter = reviewAdapter
                            layoutManager = LinearLayoutManager(applicationContext)
                            setHasFixedSize(true)
                        }


                        it.reviewInfo?.apply {
                            binding.tvMenu.text = name.replace(Regex("[\\[\\]]"), "")

                            Log.d("ReviewActivity", it.reviewInfo.toString())

                            binding.tvReviewNumCount.text = reviewCnt.toString()

                            binding.tvRate.text = String.format("%.1f", mainRating)


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

    fun delete(reviewId: Long) {

        AlertDialog.Builder(this).apply {
            setTitle("리뷰 삭제")
            setMessage("작성한 리뷰를 삭제하시겠습니까?")
            setNegativeButton("취소") { _, _ ->
                deleteViewModel.handleErrorResponse("삭제를 취소하였습니다.")
            }
            setPositiveButton("삭제") { _, _ ->
                deleteViewModel.postData(reviewId)
            }
        }.create().show()

        observeViewModel()
        //TODO 삭제하고 리뷰리스트 다시 불러오기

    }

    private fun observeViewModel() {
        deleteViewModel.toastMessage.observe(this) { result ->
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRestart() {
        super.onRestart()

        lodeData()
        bindData()
    }

    override fun onResume() {
        super.onResume()

        lodeData()
        bindData()
    }
}
