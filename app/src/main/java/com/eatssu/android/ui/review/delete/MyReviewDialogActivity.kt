package com.eatssu.android.ui.review.delete

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import com.eatssu.android.ui.review.modify.ModifyReviewActivity
import com.eatssu.android.util.DialogUtil

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
            val intent = Intent(this, ModifyReviewActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            startActivity(intent)
            finish()
        }

        binding.btnReviewDelete.setOnClickListener {
            showReviewDeleteDialog()
        }
    }

    private fun showReviewDeleteDialog() {
        DialogUtil.createDialogWithCancelButton(
            "리뷰 삭제",
            this@MyReviewDialogActivity,
            "작성한 리뷰를 삭제하시겠습니까?",
            "취소",
            "삭제"
        ) { _, _ ->
//            ActivityCompat.finishAffinity(this)
//            exitProcess(0)
            viewModel.postData(reviewId)
            finish()
        }
    }
}