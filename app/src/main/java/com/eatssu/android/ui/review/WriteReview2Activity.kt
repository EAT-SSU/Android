package com.eatssu.android.ui.review

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.request.WriteReviewDetailRequest
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReview2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding


    private lateinit var retrofit: Retrofit
    private lateinit var reviewService: ReviewService


    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    private val OPEN_GALLERY = 1

    private var currentImageUri: Uri? = null // 선택한 이미지의 Uri를 저장할 변수

    //private lateinit var selectedImageUri: Uri
    lateinit var getResult: ActivityResultLauncher<Intent>


    private var MENU_ID: Int = 0
    private lateinit var menu: String
    private var rate: Int = 0
    private var comment: String? = null
    var reviewTags = listOf("GOOD", "BAD") // Replace with your review tags


    //    private lateinit var bitmap: Bitmap
    private lateinit var filePath: String
    private lateinit var path: String


    //uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initRetrofit()

        MENU_ID = intent.getIntExtra("menuId", -1)
        menu = intent.getStringExtra("menu").toString()
        rate = intent.getIntExtra("rating", 0)
        comment = ""
        reviewTags = listOf("GOOD", "BAD") // Replace with your review tags

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                path = it.data?.data?.let { it1 -> getRealPathFromURI(it1) }.toString()
                Log.d("post", path)
                val bitmap =convertImagePathToBitmap(path)
//                 =   MediaStore.Images.Media.getBitmap(contentResolver, path)
                binding.ivImage.setImageBitmap(bitmap)
            }
        }

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
            initAddPhoto()
        }



        binding.btnNextReview2.setOnClickListener() {
            postData()
        }
    }

    fun convertImagePathToBitmap(imagePath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(imagePath, options)
    }

    private fun postData() {
        val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,

        if (MENU_ID != -1) {
            val reviewCreate = Review.ReviewCreate(comment, 4, reviewTags)
            val multipartFileList = listOf(path) // 이미지 파일 목록
            val reviewData = WriteReviewDetailRequest(multipartFileList, reviewCreate)

            reviewService.writeReview(MENU_ID, reviewData)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공한 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())
                            Toast.makeText(
                                this@WriteReview2Activity,
                                "리뷰가 등록되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)  // 화면 전환을 시켜줌
                            finish()
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d(
                                "post",
                                "onResponse 실패" + response.code() + reviewData.toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
        }
    }

    private fun getRealPathFromURI(currentImageUri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            Log.d("post3", currentImageUri.path.toString())
            return currentImageUri.path.toString()
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(currentImageUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(columnIndex)

    }

    private fun initRetrofit() {
        retrofit = RetrofitImpl.getApiClient()
        reviewService = retrofit.create(ReviewService::class.java)
    }


    fun initAddPhoto() {
        var writePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            var state = Environment.getExternalStorageState()
            if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getResult.launch(intent)
            }


        }
    }

//
//        // 결과
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//
//            val imageView = findViewById<ImageView>(R.id.imageView)
//
//            if (resultCode == Activity.RESULT_OK) {
//                when (requestCode) {
//
//                    OPEN_GALLERY -> { //여기가 핵심
//                        currentImageUri = data!!.data
////                    path= currentImageUri?.let { getRealPathFromURI(it) }.toString()
//                        path = currentImageUri?.path.toString()
//                        try {
//                            val bitmap =
//                                MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
//                            binding.ivImage.setImageBitmap(bitmap)
//                            Log.d("post2", currentImageUri?.path.toString())
////                        path=currentImageUri?.path.toString()
////                        path = currentImageUri?.let { getRealPathFromURI(it) }.toString()
//                            Log.d("post22", path)
////
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            }
//        }
}