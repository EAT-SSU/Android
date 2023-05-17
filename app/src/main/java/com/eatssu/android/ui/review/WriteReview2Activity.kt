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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityWriteReview2Binding
import com.google.gson.Gson
import okhttp3.MultipartBody
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class WriteReview2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview2Binding

    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    private val OPEN_GALLERY =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.included.actionBar.text= "리뷰남기기"

        var MENU_ID:Int=intent.getIntExtra("menuId",-1)
        var menu: String? = intent.getStringExtra("menu")
        var comment : String

        //여기서는 레이팅 바꿀 수 있나?
        binding.rbReview2.rating=intent.getFloatExtra("rating", 0F)
        binding.menu.text=menu.toString()


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


        binding.ibCamera.setOnClickListener(){
            CallCamera()
        }
        // 사진 저장
        binding.ibGallery.setOnClickListener {
//            GetAlbum()
            openGallery()
        }

        // 리뷰 데이터 생성
        val reviewCreate = Review(
            grade = 4,
            reviewTags = listOf("GOOD", "BAD"),
            content = "맛있어용"
        )

//
//
//        // 리뷰 데이터를 RequestBody로 변환
//        val reviewRequestBody = Gson().toJson(reviewCreate).toRequestBody("application/json".toMediaTypeOrNull())
//
//
//        // 이미지 파일을 MultipartBody.Part로 변환
//        val imagePart = if (imageUri != null) {
//            val imageFile = File(imageUri.path)
//            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaType())
//            MultipartBody.Part.createFormData("multipartFileList", imageFile.name, imageRequestBody)
//        } else {
//            null
//        }
//
//        // menuId를 RequestBody로 변환
//        val menuIdRequestBody = menuId.toString().toRequestBody("text/plain".toMediaType())
//
//
//        binding.btnNextReview2.setOnClickListener(){
//            val intent = Intent(this, ReviewListActivity::class.java)  // 인텐트를 생성해줌,
//
//            val reviewService = RetrofitImpl.getApiClient().create(ReviewService::class.java)
//            reviewService.writeReview(imagePart, reviewRequestBody, menuIdRequestBody).enqueue(object : Callback<BaseResponse> {
//                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
//                    if(response.isSuccessful){
//                        // 정상적으로 통신이 성공된 경우
//                        Log.d("post", "onResponse 성공: " + response.body().toString());
//                        Toast.makeText(this@WriteReview2Activity, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show()
//                    }else{
//                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
//                        Log.d("post", "onResponse 실패")
//                    }
//                }
//
//                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
//                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
//                    Log.d("post", "onFailure 에러: " + t.message.toString());
//                }
//            })ㅁㄴㅇㄹ


//            startActivity(intent)  // 화면 전환을 시켜줌
//            finish()
//        }
    }


    private fun openGallery(){
        val intent : Intent=Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent,OPEN_GALLERY)
    }

    // 카메라 권한, 저장소 권한
    // 요청 권한
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_CODE -> {
                var allGranted = true // 모든 권한이 허용되었는지 확인하기 위한 변수

                for (grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
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

                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
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
    fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }

    // 카메라 촬영 - 권한 처리
    fun CallCamera(){
        if(checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)){
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }

    // 사진 저장
    fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{

        var CV = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // MediaStore 에 파일을 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if(uri != null){
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CV.clear()
                // IS_PENDING 을 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
                Log.d("pic",uri.toString());

            }
        }
        return uri
    }

    // 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.imageView)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_CODE -> {
                    if(data?.extras?.get("data") != null){
                        val img = data.extras?.get("data") as Bitmap
                        val uri = saveFile(RandomFileName(), "image/jpeg", img)
                        imageView.setImageURI(uri)
                    }
                }
                STORAGE_CODE -> {
                    val uri = data?.data
                    imageView.setImageURI(uri)
                }
                OPEN_GALLERY->{ //여기가 핵심
                    var currentImageUri : Uri?=data?.data
                    try{
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUri)
                        binding.ivImage.setImageBitmap(bitmap)
                    }
                    catch(e: Exception){
                      e.printStackTrace()
                    }
                }
            }
        }
    }

    // 파일명을 날짜 저장
    fun RandomFileName() : String{
        val fileName = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        return fileName
    }

    // 갤러리 취득
    fun GetAlbum(){
        if(checkPermission(STORAGE, STORAGE_CODE)){
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, STORAGE_CODE)
        }
    }
}