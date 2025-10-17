package com.eatssu.android.presentation.cafeteria.review.modify

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.databinding.ActivityFixMenuBinding
import com.eatssu.android.presentation.base.ActivityCompanionWithArgs
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@AndroidEntryPoint
class ModifyReviewActivity : BaseActivity<ActivityFixMenuBinding>(
    ActivityFixMenuBinding::inflate,
    ScreenId.REVIEW_V1_MODIFY
) {

    @Parcelize
    data class Args(
        val reviewId: Long,
        val menu: String,
        val content: String,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade: Int
    ) : Parcelable

    companion object : ActivityCompanionWithArgs<Args>(ModifyReviewActivity::class, Args::class)

    private val modifyViewModel: ModifyViewModel by viewModels()

    private var reviewId = -1L
    private var menu = ""

    private var content = ""

    private var main = 0
    private var amount = 0
    private var taste = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 수정하기" // 툴바 제목 설정

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


    private fun getIndex() {
        val args = intentOptions
        reviewId = args?.reviewId ?: -1L
        menu = args?.menu ?: ""
        content = args?.content ?: ""

        main = args?.mainGrade ?: 0
        amount = args?.amountGrade ?: 0
        taste = args?.tasteGrade ?: 0

        Timber.tag("ReviewFixedActivity")
            .d("reviewID: %s, menu: %s, content: %s", reviewId.toString(), menu, content)
    }

    private fun setData() {
        binding.menu.text = menu
        binding.etReview2Comment.setText(content)
        binding.rbMain.rating = main.toFloat()
        binding.rbAmount.rating = amount.toFloat()
        binding.rbTaste.rating = taste.toFloat()
    }

    private fun postData(reviewId: Long) {
        val comment = binding.etReview2Comment.text.toString()
        val mainGrade = binding.rbMain.rating.toInt()
        val amountGrade = binding.rbAmount.rating.toInt()
        val tasteGrade = binding.rbTaste.rating.toInt()

        modifyViewModel.modifyMyReview(
            reviewId,
            ModifyReviewRequest(mainGrade, amountGrade, tasteGrade, comment)
        )
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            modifyViewModel.uiState.collectLatest {
                if (it.isDone) {
                    showToast(it.toastMessage)
                    finish()
                }

                if (it.error) {
                    showToast(it.toastMessage)
                }
            }
        }
    }

    //Todo 쓰다 뒤로 갔을 때 undo
}