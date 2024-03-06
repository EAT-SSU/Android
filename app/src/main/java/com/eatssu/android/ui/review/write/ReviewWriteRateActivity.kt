package com.eatssu.android.ui.review.write

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.service.ImageService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewWriteRateBinding
import com.eatssu.android.util.RetrofitImpl.mRetrofit
import com.eatssu.android.util.RetrofitImpl.retrofit
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ReviewWriteRateActivity :
    BaseActivity<ActivityReviewWriteRateBinding>(ActivityReviewWriteRateBinding::inflate) {

    private lateinit var viewModel: UploadReviewViewModel
    private lateinit var reviewService: ReviewService
    private lateinit var imageService: ImageService

    private val PERMISSION_REQUEST_CODE = 1

    private var selectedImagePath: String? = null

    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정

        itemName = intent.getStringExtra("itemName").toString()
        Log.d("post", "고정메뉴${itemName}")

        itemId = intent.getLongExtra("itemId", 16)

        // 현재 메뉴명을 표시합니다.
        binding.menu.text = itemName

        // 외부 저장소에 대한 런타임 퍼미션 요청
        requestStoragePermission()

        // 텍스트 리뷰 입력 관련 설정
        setupTextReviewInput()

        // 이미지 추가 버튼 클릭 리스너 설정
        binding.ibAddPic.setOnClickListener { openGallery() }

        imageService = mRetrofit.create(ImageService::class.java)
        reviewService = retrofit.create(ReviewService::class.java)

        viewModel = ViewModelProvider(
            this,
            UploadReviewViewModelFactory(reviewService)
        )[UploadReviewViewModel::class.java]

        setupUI()
        observeViewModel()

    }

    private fun setupUI() {
        binding.btnNextReview2.setOnClickListener { postReview() }
        binding.btnDelete.setOnClickListener { deleteImage() }
    }

    private fun observeViewModel() {
        viewModel.isUpload.observe(this) { isUpload ->
            if (isUpload == true) {
                finish()
            }

        }
        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestStoragePermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            binding.ivImage.setImageURI(imageUri) //이미지 불러다 놓기
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
        Log.d("ReviewWriteRateActivity", selectedImagePath.toString())
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

        if (binding.rbMain.rating.toInt() == 0 || binding.rbAmount.rating.toInt() == 0 || binding.rbTaste.rating.toInt() == 0) {
            Toast.makeText(this, "별점을 등록해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if ((comment?.trim()?.length ?: 0) < 3) {
            Toast.makeText(this, "3자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }


        lifecycleScope.launch {//리뷰 작성
            val compressImageString = compressImage(selectedImagePath)

            if (compressImageString != null) { //null일 때는 할 필요가 없음
                imageService.getImageUrl(compressImageString).enqueue(
                    object : Callback<BaseResponse<ImageResponse>> {
                        override fun onResponse(
                            call: Call<BaseResponse<ImageResponse>>,
                            response: Response<BaseResponse<ImageResponse>>,
                        ) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우

                                val imageUrl = response.body()?.result?.url
                                val reviewData = WriteReviewRequest(
                                    binding.rbMain.rating.toInt(),
                                    binding.rbAmount.rating.toInt(),
                                    binding.rbTaste.rating.toInt(),
                                    comment,
                                    imageUrl
                                )
                                viewModel.postReview(itemId, reviewData)
                                Log.d("ReviewWriteRateActivity", "리뷰")
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d("post", "onResponse 리뷰 작성 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<BaseResponse<ImageResponse>>,
                            t: Throwable,
                        ) {
                            // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                            Log.d("post", "onFailure 에러: " + t.message.toString())
                        }
                    })

            } else {
                val reviewData = WriteReviewRequest(
                    binding.rbMain.rating.toInt(),
                    binding.rbAmount.rating.toInt(),
                    binding.rbTaste.rating.toInt(),
                    comment,
                    ""
                )
                viewModel.postReview(itemId, reviewData)
                Log.d("ReviewWriteRateActivity", "리뷰")
            }
        }


        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        Log.d("ReviewWriteRateActivity", "리뷰 다씀")
    }


    private suspend fun compressImage(imageString: String?): MultipartBody.Part? {

        if (imageString != null) {
            val file = File(imageString)
            val compressedFile =
                Compressor.compress(this@ReviewWriteRateActivity, file) { quality(80) }
            val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())

            return MultipartBody.Part.createFormData(
                "multipartFileList",
                compressedFile.name,
                requestFile
            )
        }
        return null
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
