package com.eatssu.android.ui.review



import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eatssu.android.App
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReview2Binding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.IOException


class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

    private lateinit var retrofit: Retrofit
    private lateinit var reviewService: ReviewService
    private val PERMISSION_REQUEST_CODE = 1


    private var selectedImagePath: String? = null


    lateinit var getResult: ActivityResultLauncher<Intent>

    private var MENU_ID: Int = 0
    private lateinit var menu: String
    private var rate: Int = 0
    private var comment: String? = null
    var reviewTags = listOf("GOOD", "BAD") // Replace with your review tags

    //    private lateinit var bitmap: Bitmap
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
//        rate = intent.getIntExtra("rating", 0)
        rate = 5
        comment = ""
        reviewTags = listOf("GOOD", "BAD") // Replace with your review tags
        path = ""
//
//        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                path = it.data?.data?.let { it1 -> getRealPathFromURI(it1) }.toString()
//                Log.d("post", path)
//                val bitmap = convertImagePathToBitmap(path)
////                 =   MediaStore.Images.Media.getBitmap(contentResolver, path)
//                binding.ivImage.setImageBitmap(bitmap)
//            }
//        }

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
        }
    }

    fun convertImagePathToBitmap(imagePath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(imagePath, options)
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

    private inner class PostDataAsyncTask : AsyncTask<Unit, Unit, String>() {
        override fun doInBackground(vararg params: Unit?): String {


            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reviewCreate", "\"{\n  \\\"grade\\\": 4,\n  \\\"reviewTags\\\": [\\\"GOOD\\\",\\\"BAD\\\"],\n  \\\"content\\\": \\\"맛있어용\\\"\n}\";type=application/json")
                .addFormDataPart("multipartFileList", "$selectedImagePath", File("$selectedImagePath").asRequestBody())
                .build()

            val request = Request.Builder()
                .url("https://eatssu.shop/review/$MENU_ID/detail")
                .post(requestBody)
                .header(
                    "Authorization",
                    "Bearer ${App.token_prefs.accessToken}"
                )
                .header("accept", "application/json")  // Content-Type 수정
                .header("Content-Type", "multipart/form-data")  // Content-Type 수정

                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()


            return response.body?.string() ?: ""
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                Log.d("post",result)
            } else {
                // 서버 응답이 실패한 경우
                // 실패 상황 처리 코드 작성
                Log.d("post", result.toString())
                Toast.makeText(this@WriteReview2Activity, "리뷰를 업로드 하지 못했습니다.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun postData() {
        if (MENU_ID != -1) {
            val reviewCreate = Review.ReviewCreate(comment, 4, reviewTags)
            val filePaths = listOf(selectedImagePath)

            // AsyncTask 실행
            val postDataAsyncTask = PostDataAsyncTask()
            postDataAsyncTask.execute()
        }
    }
}

/*    private fun postData() {
        val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,

        if (MENU_ID != -1) {
            val reviewCreate = Review.ReviewCreate(comment, 4, reviewTags)
//            val multipartFileList = listOf(path) // 이미지 파일 목록
//            val reviewData = WriteReviewDetailRequest(multipartFileList, reviewCreate)
//            val requestBody = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/5fe5707a0d0a0.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/5fe5707a0d0a0.jpg\"").asRequestBody())
//                .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/img.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/img.jpg\"").asRequestBody())
//                .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"").asRequestBody())
//                .addFormDataPart("reviewCreate", "\"{\n  \\\"grade\\\": 4,\n  \\\"reviewTags\\\": [\\\"GOOD\\\",\\\"BAD\\\"],\n  \\\"content\\\": \\\"맛있어용\\\"\n}\";type=application/json")
//                .build()

//            val file1 = File(selectedImagePath)
//            val file1 = File("C:\\Users\\qldls\\Downloads\\free-icon-people-3224689.png")
//            val requestBody1 = RequestBody.create(MediaType.parse("image/*"), file1)

//            val file2 = File("C:/Users/김소연/Pictures/짤/img.jpg")
//            val requestBody2 = RequestBody.create(MediaType.parse("image/jpeg"), file2)
//
//            val file3 = File("C:/Users/김소연/Pictures/짤/IMG_0997.jpg")
//            val requestBody3 = RequestBody.create(MediaType.parse("image/jpeg"), file3)

// ...


// ...
//            val requestBody = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("multipartFileList", selectedImagePath, requestBody1)
////                .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/img.jpg\"", requestBody2)
////                .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"", requestBody3)
//                .addFormDataPart("reviewCreate", "$reviewCreate;type=application/json")
//                .build()

            // 업로드할 파일의 경로 목록
            val filePaths = listOf(
                selectedImagePath
            )

            val fileParts = mutableListOf<MultipartBody.Part>()
            filePaths.forEach { filePath ->
                val file = filePath?.let { File(it) }
                val requestFile = file?.asRequestBody("image/*".toMediaType())
                val filePart = requestFile?.let {
                    MultipartBody.Part.createFormData(
                        "multipartFileList", file.name,
                        it
                    )
                }
                if (filePart != null) {
                    fileParts.add(filePart)
                }
            }
            // 리뷰 데이터
            val reviewData = """
            {
              "grade": $rate,
              "reviewTags": $reviewTags,
              "content": "$comment"
            }
            """.trimIndent()


            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "multipartFileList",
                    "$selectedImagePath",
                    File("$selectedImagePath").asRequestBody()
                )
//                .addFormDataPart(
//                    "multipartFileList",
//                    "\"/C:/Users/김소연/Pictures/짤/img.jpg\"",
//                    File("\"/C:/Users/김소연/Pictures/짤/img.jpg\"").asRequestBody()
//                )
//                .addFormDataPart(
//                    "multipartFileList",
//                    "\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"",
//                    File("\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"").asRequestBody()
//                )
                .addFormDataPart(
                    "reviewCreate",
                    "\"{\n  \\\"grade\\\": 4,\n  \\\"reviewTags\\\": [\\\"GOOD\\\",\\\"BAD\\\"],\n  \\\"content\\\": \\\"맛있어용\\\"\n}\";type=application/json"
                )
                .build()

            val request = Request.Builder()
                .url("https://eatssu.shop/review/$MENU_ID/detail")
                .post(requestBody)
                .header(
                    "Authorization",
                    "Bearer ${App.token_prefs.accessToken}"
                )
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                response.body!!.string()
            }

//            reviewService.writeReview(MENU_ID, fileParts, reviewData.toRequestBody())
////            reviewService.writeReview(MENU_ID, requestBody)
//                .enqueue(object : Callback<String> {
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        if (response.isSuccessful) {
//                            // 정상적으로 통신이 성공한 경우
//                            Log.d("post", "onResponse 성공: " + response.body().toString())
//                            Toast.makeText(
//                                this@WriteReview2Activity,
//                                "리뷰가 등록되었습니다.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            startActivity(intent)  // 화면 전환을 시켜줌
//                            finish()
//                        } else {
//                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
//                            Log.d(
//                                "post",
//                                "onResponse 실패" + response.code()
//                            )
//                        }
//                    }
//
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
//                        Log.d("post", "onFailure 에러: " + t.message.toString())
//                    }
//                })
        }
    }
//
//    private fun getRealPathFromURI(currentImageUri: Uri): String {
//        val buildName = Build.MANUFACTURER
//        if (buildName.equals("Xiaomi")) {
//            Log.d("post3", currentImageUri.path.toString())
//            return currentImageUri.path.toString()
//        }
//
//        var columnIndex = 0
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        var cursor = contentResolver.query(currentImageUri, proj, null, null, null)
//        if (cursor!!.moveToFirst()) {
//            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        }
//
//        return cursor.getString(columnIndex)
//
//    }

//    private fun initRetrofit() {
//        retrofit = RetrofitImpl.getApiClient()
//        reviewService = retrofit.create(ReviewService::class.java)
//    }
//
//
//    private fun initAddPhoto() {
//        var writePermission = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        var readPermission = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE
//        )
//
//        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                ),
//                1
//            )
//        } else {
//            val state = Environment.getExternalStorageState()
//            if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
//                val intent = Intent(Intent.ACTION_PICK)
//                intent.type = "image/*"
//                getResult.launch(intent)
//            }
//
//
//        }
//    }



//import java.io.File
//import java.io.IOException
//import okhttp3.MultipartBody
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.asRequestBody
//
//val client = OkHttpClient()
//
//val requestBody = MultipartBody.Builder()
//    .setType(MultipartBody.FORM)
//    .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/5fe5707a0d0a0.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/5fe5707a0d0a0.jpg\"").asRequestBody())
//    .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/img.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/img.jpg\"").asRequestBody())
//    .addFormDataPart("multipartFileList", "\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"", File("\"/C:/Users/김소연/Pictures/짤/IMG_0997.jpg\"").asRequestBody())
//    .addFormDataPart("reviewCreate", "\"{\n  \\\"grade\\\": 4,\n  \\\"reviewTags\\\": [\\\"GOOD\\\",\\\"BAD\\\"],\n  \\\"content\\\": \\\"맛있어용\\\"\n}\";type=application/json")
//    .build()
//
//val request = Request.Builder()
//    .url("https://eatssu.shop/review/35/detail")
//    .post(requestBody)
//    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJlbWFpbFwiOlwidGVzdEBlbWFpbC5jb21cIn0iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjgwOTE4NjY1fQ.ZZr7LclD5YxRqzfjCnL8kImu4z--KYwo8y2V4VCHn1Oz1h-UbeNww6VVnIYonRL83lUu6zMZ59TlfU_eeqV_QQ")
//    .build()
//
//client.newCall(request).execute().use { response ->
//    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//    response.body!!.string()
//}*/