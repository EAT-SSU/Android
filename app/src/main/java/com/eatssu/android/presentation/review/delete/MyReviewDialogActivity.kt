package com.eatssu.android.presentation.review.delete

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import com.eatssu.android.presentation.review.modify.ModifyReviewActivity

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