package com.eatssu.android.view.review

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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReviewBinding
import com.eatssu.android.repository.ReviewRepository
import com.eatssu.android.viewmodel.ReviewViewModel
import com.eatssu.android.viewmodel.UploadReviewViewModel
import com.eatssu.android.viewmodel.factory.ReviewViewModelFactory
import com.eatssu.android.viewmodel.factory.UploadReviewViewModelFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody

class WriteReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReviewBinding

    private lateinit var viewModel: UploadReviewViewModel
    private lateinit var reviewService: ReviewService
    private val PERMISSION_REQUEST_CODE = 1

    private var selectedImagePath: String? = null

    private var itemId: Long = 0
    private lateinit var itemName: String

    private var comment: String? = null
//    private var mainRating: Int = 0
//    private var amountRating: Int = 0
//    private var tasteRating: Int = 0

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

        itemId = intent.getLongExtra("itemId", -1)
        itemName = intent.getStringArrayListExtra("itemName").toString()
        Log.d("post", itemName)
//        rate = intent.getIntExtra("rating", 0)


        comment = binding.etReview2Comment.text.toString()

        binding.menu.text = itemName
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


        reviewService = mRetrofit.create(ReviewService::class.java)

        val repository = ReviewRepository(reviewService)
        viewModel =
            ViewModelProvider(
                this,
                UploadReviewViewModelFactory(repository)
            )[UploadReviewViewModel::class.java]


        binding.btnNextReview2.setOnClickListener() {

            val reviewData = """
    {
        "mainGrade": ${binding.rbMain.rating.toInt()},
        "amountGrade": ${binding.rbAmount.rating.toInt()},
        "tasteGrade": ${binding.rbTaste.rating.toInt()},
        "content": "$comment"
    }
""".trimIndent().toRequestBody("application/json".toMediaTypeOrNull())


            val fileList: List<String?> = listOf(selectedImagePath)

            lifecycleScope.launch {
                val compressedPartsList = compressImage(fileList)
                // Make the file list nullable
                if (compressedPartsList == null) {
                    viewModel.postReview(itemId, reviewData)
                } else { viewModel.postReview(itemId, compressedPartsList, reviewData) }
            }

            viewModel.shouldStartActivity.observe(
                this
            ) {
                val intent = Intent(this, ReviewListActivity::class.java)
                intent.putExtra("itemId", itemId)
                Log.d("post", itemId.toString())
                finish()
            }
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

    // Coroutine function to compress the image
    private suspend fun compressImage(fileList: List<String?>): List<MultipartBody.Part> {
        val compressedPartsList: MutableList<MultipartBody.Part> = mutableListOf()

        fileList.forEach { filePath ->
            // Check if the file path is not null and compress the image
            if (filePath != null) {
                val file = File(filePath)
                val compressedFile = Compressor.compress(this@WriteReviewActivity, file) {
                    quality(50) // Adjust the quality level (0-100)
                }
                val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(
                    "multipartFileList",
                    compressedFile.name,
                    requestFile
                )
                compressedPartsList.add(part)
            }
        }

        return compressedPartsList
    }
}