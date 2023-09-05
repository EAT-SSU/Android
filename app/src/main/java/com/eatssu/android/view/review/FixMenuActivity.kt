package com.eatssu.android.view.review

import RetrofitImpl.mRetrofit
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityFixMenuBinding
import com.eatssu.android.view.main.MainActivity
import com.eatssu.android.viewmodel.CalendarViewModel
import com.eatssu.android.viewmodel.MenuIdViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class FixMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFixMenuBinding
    private var reviewId = -1L
    private var menuId = -1L
    private lateinit var menu: String
    private var comment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixMenuBinding.inflate(layoutInflater)

        supportActionBar?.title = "수정하기"

        setContentView(binding.root)

        menu = intent.getStringExtra("menu").toString()
        binding.menu.text = menu

        reportInfo()

    }

    private fun reportInfo() {

        reviewId = intent.getLongExtra("reviewId", -1L)
        Log.d("menufix", reviewId.toString())

        /*val menuViewModel = ViewModelProvider(this@FixMenuActivity).get(MenuIdViewModel::class.java)
        menuViewModel.getData().observe(this, Observer { dataReceived ->
            menuId = dataReceived.toLong()
            Log.d("menufix", menuId.toString())
        })*/

        binding.btnFixReview.setOnClickListener {

            postData(reviewId)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun postData(reviewId: Long) {
        val service = mRetrofit.create(ReviewService::class.java)

        comment = binding.etReview2Comment.text.toString()

        val reviewData = """
    {
        "mainGrade": ${binding.rbMain.rating.toInt()},
        "amountGrade": ${binding.rbAmount.rating.toInt()},
        "tasteGrade": ${binding.rbTaste.rating.toInt()},
        "content": "$comment"
    }
""".trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

        /*val menuViewModel = ViewModelProvider(this)[MenuIdViewModel::class.java]
        menuViewModel.getData().observe(this, Observer {dataReceived ->
            menuId = dataReceived.toLong()
            Log.d("menufix", menuId.toString()+reviewId.toString())*/

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.modifyReview(reviewId, reviewData).execute()
                }
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공한 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(
                        this@FixMenuActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d(
                        "post",
                        "onResponse 실패 write" + response.code()
                    )
                }
            } catch (e: Exception) {
                // 통신 중 예외가 발생한 경우
                Log.d("post", "통신 실패: ${e.message}")
                Toast.makeText(
                    this@FixMenuActivity, "수정이 실패하였습니다.", Toast.LENGTH_SHORT
                ).show()
            }
        }
        //})
    }
}