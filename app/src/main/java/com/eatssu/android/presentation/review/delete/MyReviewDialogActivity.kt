package com.eatssu.android.presentation.review.delete

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyReviewDialogBinding
import com.eatssu.android.presentation.review.modify.ModifyReviewActivity
import com.eatssu.android.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyReviewDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewDialogBinding

    private val deleteViewModel: DeleteViewModel by viewModels()

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

        reviewId = intent.getLongExtra("reviewId", -1L)
        menu = intent.getStringExtra("menu").toString()
        content = intent.getStringExtra("content").toString()
        mainGrade = intent.getIntExtra("mainGrade", -1)
        amountGrade = intent.getIntExtra("amountGrade", -1)
        tasteGrade = intent.getIntExtra("tasteGrade", -1)

        Timber.d("전:" + reviewId.toString())
        Timber.d("전:" + menu.toString())
        Timber.d("전:" + content.toString())
        Timber.d(reviewId.toString())

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
                setTitle(R.string.delete)
                setMessage(R.string.delete_description)
                setNegativeButton("취소") { _, _ ->
                    showToast(App.appContext.getString(R.string.delete_undo))
                }
                setPositiveButton("삭제") { _, _ ->
                    deleteViewModel.deleteReview(reviewId)
                    lifecycleScope.launch {
                        deleteViewModel.uiState.collectLatest {
                            if (it.isDeleted) {
                                finish()
                            }
                        }
                    }
                }
            }.create().show()

        }
    }
}