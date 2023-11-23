package com.eatssu.android.ui.review.etc
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityFixMenuBinding
import com.eatssu.android.ui.review.etc.FixViewModel
import com.eatssu.android.util.RetrofitImpl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FixedReviewActivity : BaseActivity<ActivityFixMenuBinding>(ActivityFixMenuBinding::inflate) {

    private lateinit var viewModel : FixViewModel
    private var reviewId = -1L
    private lateinit var menu: String
    private var comment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 수정하기" // 툴바 제목 설정

        viewModel = ViewModelProvider(this).get(FixViewModel::class.java)

        reviewId = intent.getLongExtra("reviewId", -1L)
        Log.d("ReviewFixedActivity", reviewId.toString())

        menu = intent.getStringExtra("menu").toString()
        binding.menu.text = menu

        binding.btnDone.setOnClickListener {
            postData(reviewId)
        }

        observeViewModel()
    }

    private fun postData(reviewId: Long) {
        val comment = binding.etReview2Comment.text.toString()
        val mainGrade = binding.rbMain.rating.toInt()
        val amountGrade = binding.rbAmount.rating.toInt()
        val tasteGrade = binding.rbTaste.rating.toInt()

        viewModel.postData(reviewId, comment, mainGrade, amountGrade, tasteGrade)
    }

    private fun observeViewModel() {
        viewModel.toastMessage.observe(this, Observer { result ->
            Toast.makeText(this@FixedReviewActivity, result, Toast.LENGTH_SHORT).show()
        })

        viewModel.isDone.observe(this) { isDone ->
            if(isDone) {
                finish()
            }
        }
    }
}