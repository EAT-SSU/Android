package com.eatssu.android.view.review

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityOthersReviewDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OthersReviewDialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOthersReviewDialogBinding
    var reviewId = -1L
    var menuId = -1L
    var menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOthersReviewDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()


        binding.btnReviewReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra(
                "reviewId", reviewId
            )
            Log.d("dialogid", reviewId.toString())
            startActivity(intent)
            finish()
        }


    }
}