package com.eatssu.android.ui.review


import RetrofitImpl.mRetrofit
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

    //    private lateinit var retro_fit: Retrofit
    private lateinit var reviewService: ReviewService
    private val PERMISSION_REQUEST_CODE = 1


    private var selectedImagePath: String? = null


    lateinit var getResult: ActivityResultLauncher<Intent>

    private var MENU_ID: Int = 0
    private lateinit var menu: String
    private var rate: Float = 0.0F
    private var comment: String? = null
//    private lateinit var reviewTags: ArrayList<String> // Replace with your review tags

    private lateinit var path: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)
//        initRetrofit()
        // 외부 저장소에 대한 런타임 퍼미션 요청
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


        MENU_ID = intent.getIntExtra("menuId", -1)
        menu = intent.getStringExtra("menu").toString()
        rate = intent.getFloatExtra("rating", 0F)
        Log.d("post", rate.toString())
//        rate = 5
        comment = ""
//        reviewTags = intent.getStringArrayListExtra("selectedTags") as ArrayList<String>
//        Log.d("post", reviewTags.toString())
//        path = ""

        binding.rbReview2.rating = rate.toFloat()
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


    private fun postData() {

        val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,

        reviewService = mRetrofit.create(ReviewService::class.java)

        val menuId = MENU_ID
        val fileList = listOf(
            selectedImagePath
//                    "/storage/emulated/0/Pictures/Screenshots/Screenshot_20230529-231907.png"
        )

        val parts: MutableList<MultipartBody.Part> = mutableListOf()
        val reviewData = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            "{\n  \"grade\": $rate,\n  \"reviewTags\": [\"BAD\"],\n  \"content\": \"$comment\"\n}"
        )
        fileList.forEach { filePath ->
            val file = File(filePath)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                "multipartFileList",
                file.name,
                requestFile
            )
            parts.add(part)
        }


        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    reviewService.uploadFiles(
                        MENU_ID,
                        parts,
                        reviewData
                    ).execute()
                }
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공한 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(
                        this@WriteReview2Activity,
                        "리뷰가 등록되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)  // 화면 전환을 시켜줌
                    intent.putExtra("menuId", MENU_ID)
                    finish()
                    finish()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d(
                        "post",
                        "onResponse 실패" + response.code() + MENU_ID + parts + reviewData
                    )
                }
            } catch (e: Exception) {
                // 통신 중 예외가 발생한 경우
                Log.d("post", "통신 실패: ${e.message}")
                Toast.makeText(
                    this@WriteReview2Activity,
                    "리뷰를 업로드하지 못했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
    }
}