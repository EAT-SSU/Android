package com.eatssu.android.ui.review.etc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReviewDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewDialogBinding
    private lateinit var viewModel: DeleteViewModel
    var reviewId = -1L
    var menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DeleteViewModel::class.java)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()

        Log.d("reviewId", reviewId.toString())

        binding.btnReviewFix.setOnClickListener {
            val intent = Intent(this, FixedReviewActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            startActivity(intent)
            finish()
        }

        binding.btnReviewDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("리뷰 삭제")
                setMessage("작성한 리뷰를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    viewModel.handleErrorResponse("삭제를 취소하였습니다.")
                }
                setPositiveButton("삭제") { _, _ ->
                    viewModel.postData(reviewId)
                }
            }.create().show()

            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.toastMessage.observe(this, Observer { result ->
            Toast.makeText(this@MyReviewDialogActivity, result, Toast.LENGTH_SHORT).show()
        })

        viewModel.isDone.observe(this) { isDone ->
            if(isDone) {
                finish()
            }
        }
    }
}