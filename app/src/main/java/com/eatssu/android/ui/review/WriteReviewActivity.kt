package com.eatssu.android.ui.review

import RetrofitImpl.mRetrofit
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReview2Binding
import com.eatssu.android.databinding.ActivityWriteReviewBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody

class WriteReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReviewBinding

    private lateinit var reviewService: ReviewService
    private val PERMISSION_REQUEST_CODE = 1

    private var selectedImagePath: String? = null

    private var MENU_ID: Int = 0
    private lateinit var menu: String

    private var comment: String? = null
    private var mainRating: Int = 0
    private var amountRating: Int = 0
    private var tasteRating: Int = 0

//    private lateinit var path: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initRetrofit()
        // 외부 저장소에 대한 런타임 퍼미션 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }

        MENU_ID = intent.getIntExtra("menuId", -1)
        menu = intent.getStringExtra("menu").toString()
//        rate = intent.getIntExtra("rating", 0)


        comment = binding.etReview2Comment.text.toString()


        mainRating = binding.rbMain.rating.toInt()
        tasteRating = binding.rbTaste.rating.toInt()
        amountRating = binding.rbAmount.rating.toInt()

        binding.menu.text = menu
        //텍스트 리뷰
        binding.etReview2Comment.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                comment = binding.etReview2Comment.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.ibAddPic.setOnClickListener {
//            initAddPhoto()
            openGallery()

        }

        binding.btnNextReview2.setOnClickListener() {
            postData()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            binding.ivImage.setImageURI(imageUri)
            selectedImagePath = imageUri?.let { getImagePath(it) }
            selectedImagePath?.let { Log.d("path", it) }
        }
    }


    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun getFilePart(filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("multipartFileList", file.name, requestFile)
    }

    private fun postData() {
        val filePaths = listOf(selectedImagePath)

        val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,

        reviewService = mRetrofit.create(ReviewService::class.java)

        val parts: MutableList<MultipartBody.Part> =
            mutableListOf() // Use MutableList to add elements


        val reviewData = """
    {
        "mainGrade": $mainRating,
        "amountGrade": $amountRating,
        "tasteGrade": $tasteRating,
        "content": "$comment"
    }
""".trimIndent().toRequestBody("application/json".toMediaTypeOrNull())
        // Make the file list nullable
        val fileList: List<String?> = listOf(selectedImagePath)

        fileList.forEach { filePath ->
            // Check if the file path is null
            if (filePath != null) {
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("multipartFileList", file.name, requestFile)
                parts.add(part)
            }
        }

        val partsList: List<MultipartBody.Part> = parts.toList()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (fileList == null) {
                            reviewService.uploadFiles(
                                3, reviewData
                            ).execute()
                    } else {
                        reviewService.uploadFiles(
                            3, partsList, reviewData
                        ).execute()
                    }
                }
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공한 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(
                        this@WriteReviewActivity, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)  // 화면 전환을 시켜줌
                    intent.putExtra("menuId", MENU_ID)
                    finish()
                    finish()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d(
                        "post", "onResponse 실패 write" + response.code() + MENU_ID + parts + reviewData
                    )
                }
            } catch (e: Exception) {
                // 통신 중 예외가 발생한 경우
                Log.d("post", "통신 실패: ${e.message}")
                Toast.makeText(
                    this@WriteReviewActivity, "리뷰를 업로드하지 못했습니다.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}