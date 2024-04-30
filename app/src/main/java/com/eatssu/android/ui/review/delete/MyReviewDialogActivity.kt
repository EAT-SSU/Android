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
    var content = ""
    var mainGrade = -1
    var amountGrade = -1
    var tasteGrade = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyReviewDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DeleteViewModel::class.java)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()
        content = intent.getStringExtra("content").toString()
        mainGrade = intent.getIntExtra("mainGrade", -1)
        amountGrade = intent.getIntExtra("amountGrade", -1)
        tasteGrade = intent.getIntExtra("tasteGrade", -1)

        Log.d("ReviewFixedActivity", "전:" + reviewId.toString())
        Log.d("ReviewFixedActivity", "전:" + menu.toString())
        Log.d("ReviewFixedActivity", "전:" + content.toString())

        binding.btnReviewFix.setOnClickListener {
            val intent = Intent(this, ModifyReviewActivity::class.java)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("menu", menu)
            intent.putExtra("content", content)
            intent.putExtra("mainGrade", mainGrade)
            intent.putExtra("amountGrade", amountGrade)
            intent.putExtra("tasteGrade", tasteGrade)

            startActivity(intent)
            finish()
        }

        binding.btnReviewDelete.setOnClickListener {
            showReviewDeleteDialog()
        }
    }

    private fun showReviewDeleteDialog() {
        DialogUtil.createDialog(
            "리뷰 삭제", "작성한 리뷰를 삭제하시겠습니까?", this@MyReviewDialogActivity,
        ) { _, _ ->
            viewModel.postData(reviewId)
            finish()
        }
    }
}