package com.eatssu.android.view.review

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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.data.RetrofitImpl.mRetrofit
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReviewBinding
import com.eatssu.android.repository.ReviewRepository
import com.eatssu.android.viewmodel.UploadReviewViewModel
import com.eatssu.android.viewmodel.factory.UploadReviewViewModelFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class WriteReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReviewBinding

    private lateinit var viewModel: UploadReviewViewModel
    private lateinit var reviewService: ReviewService

    private val PERMISSION_REQUEST_CODE = 1

    private var selectedImagePath: String? = null

    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemName = intent.getStringExtra("itemName").toString()
        Log.d("post","고정메뉴${itemName}")

        itemId = intent.getLongExtra("itemId", 0)

        // 현재 메뉴명을 표시합니다.
        binding.menu.text = itemName

        // 외부 저장소에 대한 런타임 퍼미션 요청
        requestStoragePermission()

        // 텍스트 리뷰 입력 관련 설정
        setupTextReviewInput()

        // 이미지 추가 버튼 클릭 리스너 설정
        binding.ibAddPic.setOnClickListener { openGallery() }

        reviewService = mRetrofit.create(ReviewService::class.java)
        val repository = ReviewRepository(reviewService)
        viewModel = ViewModelProvider(this, UploadReviewViewModelFactory(repository))
            .get(UploadReviewViewModel::class.java)

        binding.btnNextReview2.setOnClickListener { postReview() }
        binding.btnDelete.setOnClickListener { deleteImage() }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setupTextReviewInput() {
        binding.etReview2Comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                comment = binding.etReview2Comment.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
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
            binding.ivImage.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.VISIBLE
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

    private fun deleteImage() {
        Log.d("post",selectedImagePath.toString())
        if (selectedImagePath != null) {
            val imageFile = File(selectedImagePath)
            if (imageFile.exists()) {
                Toast.makeText(this, "이미지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                binding.ivImage.setImageDrawable(null)
                selectedImagePath = null
                binding.ivImage.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            } else {
                Toast.makeText(this, "이미지를 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postReview() {
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
                Log.d("post","사진 없는 리뷰")
            } else {
                viewModel.postReview(itemId, compressedPartsList, reviewData)
                Log.d("post","사진 있는 리뷰")
            }
        }

        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        Log.d("post","리뷰 다씀")
        finish()
    }

    private suspend fun compressImage(fileList: List<String?>): List<MultipartBody.Part>? {
        val compressedPartsList: MutableList<MultipartBody.Part> = mutableListOf()

        fileList.forEach { filePath ->
            // Check if the file path is not null and compress the image
            if (filePath != null) {
                val file = File(filePath)
                val compressedFile = Compressor.compress(this@WriteReviewActivity, file) {
                    quality(80)
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

        return if (compressedPartsList.isEmpty()) null else compressedPartsList
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
