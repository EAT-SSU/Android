package com.eatssu.android.ui.review

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.request.WriteReviewDetailRequest
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReview2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

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

    private lateinit var bitmap: Bitmap
    private lateinit var filePath: String
    private lateinit var path: String


    //uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        var MENU_ID: Int = intent.getIntExtra("menuId", -1)
        var menu: String? = intent.getStringExtra("menu")
        var rate: Int = intent.getIntExtra("rating", 0)
        var comment: String = ""
        val reviewTags = listOf("GOOD", "BAD") // Replace with your review tags


        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                path = it.data?.data?.let { it1 -> getRealPathFromURI(it1) }.toString()
                Log.d("post",path)
            }

        }

        binding.rbReview2.rating = rate.toFloat()
        binding.menu.text = menu.toString()
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


        binding.ibGallery.setOnClickListener {
            openGallery()
        }



        binding.btnNextReview2.setOnClickListener() {


            val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,

            if (MENU_ID != -1) {
                val reviewCreate = Review.ReviewCreate(comment, 4, listOf("GOOD", "BAD"))
                val multipartFileList = listOf(path) // 이미지 파일 목록
                val reviewData = WriteReviewDetailRequest(multipartFileList, reviewCreate)
//
//                // 이미지 파일을 서버로 업로드합니다.
//                val imageFiles = multipartFileList.map { File(it) }
//                val imageParts = imageFiles.map { uploadImage(it) }

                val reviewService = RetrofitImpl.getApiClient().create(ReviewService::class.java)
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
    }

    private fun openGallery() {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, OPEN_GALLERY)
        }
    }

    // 카메라 권한, 저장소 권한
    // 요청 권한
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_CODE -> {
                var allGranted = true // 모든 권한이 허용되었는지 확인하기 위한 변수

                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }

                if (allGranted) {
                    // 권한이 허용된 경우에만 카메라 호출
                    CallCamera()
                }
            }
            STORAGE_CODE -> {
                var allGranted = true // 모든 권한이 허용되었는지 확인하기 위한 변수

                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }

                if (allGranted) {
                    // 권한이 허용된 경우에만 갤러리 호출
                    GetAlbum()
                }
            }
        }
    }

    // 다른 권한등도 확인이 가능하도록
    fun checkPermission(permissions: Array<out String>, type: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }

    // 카메라 촬영 - 권한 처리
    fun CallCamera() {
        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }

    // 사진 저장
    fun saveFile(fileName: String, mimeType: String, bitmap: Bitmap): Uri? {

        var CV = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // MediaStore 에 파일을 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if (uri != null) {
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CV.clear()
                // IS_PENDING 을 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
                Log.d("post", uri.toString());

            }
        }
        return uri
    }

    // 결과
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.imageView)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        val img = data.extras?.get("data") as Bitmap
                        currentImageUri = saveFile(RandomFileName(), "image/jpeg", img)
                        imageView.setImageURI(currentImageUri)
                        Log.d("post0",currentImageUri?.path.toString())
                    }
                }
                STORAGE_CODE -> {
                    currentImageUri = data?.data
                    imageView.setImageURI(currentImageUri)
                    Log.d("post1",currentImageUri?.path.toString())

                }
                OPEN_GALLERY -> { //여기가 핵심
                    currentImageUri = data!!.data
//                    path= currentImageUri?.let { getRealPathFromURI(it) }.toString()
                    path=currentImageUri?.path.toString()
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
                        binding.ivImage.setImageBitmap(bitmap)
                        Log.d("post2",currentImageUri?.path.toString())
//                        path=currentImageUri?.path.toString()
//                        path = currentImageUri?.let { getRealPathFromURI(it) }.toString()
                        Log.d("post22", path)
//
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // 파일명을 날짜 저장
    fun RandomFileName(): String {
        val fileName = SimpleDateFormat(
            "yyyyMMddHHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        return fileName
    }

    // 갤러리 취득
    fun GetAlbum() {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, STORAGE_CODE)
        }
    }

    private fun getRealPathFromURI(currentImageUri: Uri): String {
        val buildName = Build.MANUFACTURER
        if(buildName.equals("Xiaomi")) {
            Log.d("post3",currentImageUri.path.toString())
            return currentImageUri.path.toString()
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(currentImageUri, proj, null, null, null)
        if(cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(columnIndex)

    }
}