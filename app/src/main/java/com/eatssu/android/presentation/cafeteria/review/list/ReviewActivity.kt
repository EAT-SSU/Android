package com.eatssu.android.presentation.cafeteria.review.list

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityReviewBinding
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.base.ActivityCompanionWithArgs
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteRateActivity
import com.eatssu.android.presentation.cafeteria.review.write.menu.ReviewWriteMenuActivity
import com.eatssu.android.presentation.common.MyReviewBottomSheetFragment
import com.eatssu.android.presentation.common.OthersBottomSheetFragment
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@AndroidEntryPoint
class ReviewActivity :
    BaseActivity<ActivityReviewBinding>(ActivityReviewBinding::inflate, ScreenId.REVIEW_V1_VIEW),
    MyReviewBottomSheetFragment.OnReviewDeletedListener {

    @Parcelize
    data class Args(
        val menuType: String,
        val itemId: Long,
        val itemName: String
    ) : Parcelable

    companion object : ActivityCompanionWithArgs<Args>(ReviewActivity::class, Args::class)

    private val reviewViewModel: ReviewViewModel by viewModels()

    private val menuType by lazy { intentOptions?.menuType ?: "" }
    private val itemId by lazy { intentOptions?.itemId ?: 0 }
    private val itemName by lazy { intentOptions?.itemName?.replace(Regex("[\\[\\]]"), "") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰" // 툴바 제목 설정

        Timber.d("메뉴는 $itemName $menuType $itemId")
        lodeData()
        bindData()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()


        //todo 이거 안하면 바로바로 갱신이 안되는디
        lodeData()
        bindData()
    }


    private fun lodeData() {
        //Todo 여기서는 메뉴 타입이 뭔지 몰라도 됨. 추상화 해도 됨

        reviewViewModel.loadReview(menuType, itemId)
    }


    private fun bindData() {
        lifecycleScope.launch {
            reviewViewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    if (it.isEmpty) {
                        //리뷰 없어도 메뉴명은 있음
                        Timber.d("리뷰가 없음")
                        binding.llNonReview.visibility = View.VISIBLE
                        binding.rvReview.visibility = View.INVISIBLE

                        it.reviewInfo?.apply {
                            binding.tvMenu.text = name.replace(Regex("[\\[\\]]"), "")
                        }

                    } else { //리뷰 있다.
                        Timber.d("리뷰가 있음")
                        binding.llNonReview.visibility = View.INVISIBLE
                        binding.rvReview.visibility = View.VISIBLE

                        it.reviewList?.let { reviewList -> setAdapter(reviewList = reviewList) }

                        it.reviewInfo?.apply {

                            Timber.d(it.reviewInfo.toString())

                            binding.tvMenu.text = name.replace(Regex("[\\[\\]]"), "")
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


    private fun setAdapter(reviewList: List<Review>) {

        val adapter = ReviewAdapter()
        adapter.submitList(reviewList)

        val linearLayoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener(object :
            ReviewAdapter.OnItemClickListener {

            override fun onMyReviewClicked(view: View, reviewData: Review) {
                onMyReviewClicked(review = reviewData)
            }

            override fun onOthersReviewClicked(view: View, reviewData: Review) {
                onOthersReviewClicked(reviewData = reviewData)
            }
        })

        binding.rvReview.adapter = adapter
        binding.rvReview.layoutManager = linearLayoutManager
        binding.rvReview.setHasFixedSize(true)
    }


    private fun setClickListener() {
        when (menuType) {
            MenuType.FIXED.name -> {
                binding.btnNextReview.setOnClickListener {
                    ReviewWriteRateActivity.start(
                        this,
                        ReviewWriteRateActivity.Args(
                            itemName = itemName,
                            itemId = itemId,
                            menuType = menuType
                        )
                    )
                    EventLogger.writeReview()
                }
            }

            MenuType.VARIABLE.name -> {
                binding.btnNextReview.setOnClickListener {
                    ReviewWriteMenuActivity.start(
                        this,
                        ReviewWriteMenuActivity.Args(
                            itemId = itemId,
                            menuType = menuType
                        )
                    )
                    EventLogger.writeReview()
                }
            }

            else -> {
                Timber.d("잘못된 식당 정보입니다.")
            }
        }
    }


    fun onMyReviewClicked(review: Review) {
        val modalBottomSheet = MyReviewBottomSheetFragment.newInstance(
            MyReviewBottomSheetFragment.Args(
                reviewId = review.reviewId,
                menu = review.menu,
                content = review.content,
                mainGrade = review.mainGrade,
                amountGrade = review.amountGrade,
                tasteGrade = review.tasteGrade
            )
        ) as MyReviewBottomSheetFragment

        modalBottomSheet.onReviewDeletedListener = this@ReviewActivity
        modalBottomSheet.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
        modalBottomSheet.show(supportFragmentManager, "Open Bottom Sheet")
    }

    fun onOthersReviewClicked(reviewData: Review) {
        val modalBottomSheet = OthersBottomSheetFragment.newInstance(
            OthersBottomSheetFragment.Args(
                reviewId = reviewData.reviewId,
                menu = reviewData.menu
            )
        ) as OthersBottomSheetFragment

        modalBottomSheet.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
        modalBottomSheet.show(supportFragmentManager, "Open Bottom Sheet")
    }


    override fun onReviewDeleted() {
        lodeData()
        bindData()
    }
}
