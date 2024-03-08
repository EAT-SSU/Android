package com.eatssu.android.ui.review.write

import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.service.ImageService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewWriteRateBinding
import com.eatssu.android.util.RetrofitImpl.mRetrofit
import com.eatssu.android.util.RetrofitImpl.retrofit
import java.io.File

class ReviewWriteRateActivity :
    BaseActivity<ActivityReviewWriteRateBinding>(ActivityReviewWriteRateBinding::inflate) {

    private lateinit var viewModel: UploadReviewViewModel
    private lateinit var imageviewModel: ImageViewModel

    private lateinit var reviewService: ReviewService
    private lateinit var imageService: ImageService

    private val PERMISSION_REQUEST_CODE = 1

    private var selectedImagePath: String? = null

    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""
//    private var imageUrlString = ""

    private lateinit var imageFile: File


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
        binding.ibAddPic.setOnClickListener {
            Log.d("re", "클릭")

            selectGallery()
        }

        imageService = mRetrofit.create(ImageService::class.java)
        reviewService = retrofit.create(ReviewService::class.java)


        viewModel = ViewModelProvider(
            this,
            UploadReviewViewModelFactory(reviewService)
        )[UploadReviewViewModel::class.java]
        imageviewModel =
            ViewModelProvider(this, ImageViewModelFactory(imageService))[ImageViewModel::class.java]

        setupUI()
        observeViewModel()


//        private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) {
//
//            //결과 코드 OK , 결가값 null 아니면
//            if (it.resultCode == RESULT_OK && it.data != null) {
//                //값 담기
//                val uri = it.data!!.data
//
//                //화면에 보여주기
////            Glide.with(this)
////                .load(uri) //이미지
////                .into(binding.ivImage) //보여줄 위치
//
//                binding.ivImage.setImageURI(uri)
//            }
//        }
//
//
//        //버튼 이벤트
//        binding.ibAddPic.setOnClickListener {
//
//            //갤러리 호출
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            activityResult.launch(intent)
//        }
    }//onCreate


    //결과 가져오기


    //    // 이미지를 결과값으로 받는 변수
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 이미지를 받으면 ImageView에 적용한다
            val imageUri = result.data?.data
            imageUri?.let {

                // 서버 업로드를 위해 파일 형태로 변환한다
                imageFile = File(getRealPathFromURI(it))

                // 이미지를 불러온다
                Glide.with(this)
                    .load(imageUri)
                    .fitCenter()
                    .apply(RequestOptions().override(500, 500))
                    .into(binding.ivImage)


//                binding.ivImage.setImageURI(imageUri) //이미지 불러다 놓기

//                imageviewModel.viewModelScope.launch {
//                    selectedImagePath = imageUri?.let { getImagePath(it) }
//                    selectedImagePath?.let { Log.d("path", it) }

//                    imageviewModel.getImageString(selectedImagePath) //이미지 url 반환 api 호출

//
                binding.ivImage.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            }
        }

    }

    // 이미지 실제 경로 반환
    fun getRealPathFromURI(uri: Uri): String {

        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    // 갤러리를 부르는 메서드
    private fun selectGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)

            val readMediaImagePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)

            //권한 확인
            if (readMediaImagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                    ), REQ_GALLERY
                )
                Log.d("re", "권한 없음")

            } else {
                Log.d("re", "권한 잇음")
                // 권한이 있는 경우 갤러리 실행
                val intent = Intent(Intent.ACTION_PICK)
                // intent의 data와 type을 동시에 설정하는 메서드
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )

                imageResult.launch(intent)
            }

        } else {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            val writePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            //권한 확인
            if (writePermission == PackageManager.PERMISSION_DENIED ||
                readPermission == PackageManager.PERMISSION_DENIED
            ) {

                // 권한 요청
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), REQ_GALLERY
                )
                Log.d("re", "권한 없음")

            } else {
                Log.d("re", "권한 잇음")
                // 권한이 있는 경우 갤러리 실행
                val intent = Intent(Intent.ACTION_PICK)
                // intent의 data와 type을 동시에 설정하는 메서드
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )

                imageResult.launch(intent)
            }
        }
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
//
//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, REQUEST_IMAGE_PICK)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
//            val imageUri = data.data
//
//            binding.ivImage.setImageURI(imageUri) //이미지 불러다 놓기
//
//            imageviewModel.viewModelScope.launch {
//                selectedImagePath = imageUri?.let { getImagePath(it) }
//                selectedImagePath?.let { Log.d("path", it) }
//
//                imageviewModel.getImageString(selectedImagePath) //이미지 url 반환 api 호출
//
//
//                binding.ivImage.visibility = View.VISIBLE
//                binding.btnDelete.visibility = View.VISIBLE
//            }
//        }
//    }


//    private fun getImagePath(uri: Uri): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                return it.getString(columnIndex)
//            }
//        }
//        return null
//    }


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


        imageviewModel.imageString.observe(this) {
            val reviewData = WriteReviewRequest(
                binding.rbMain.rating.toInt(),
                binding.rbAmount.rating.toInt(),
                binding.rbTaste.rating.toInt(),
                comment,
                it
            )
            viewModel.postReview(itemId, reviewData)
            Log.d("ReviewWriteRateActivity", "리뷰 씀")
        }


        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        Log.d("ReviewWriteRateActivity", "리뷰 다씀")
    }


//
//    companion object {
//        private const val REQUEST_IMAGE_PICK = 1
//    }

    companion object {
        const val REVIEW_MIN_LENGTH = 10

        // 갤러리 권한 요청
        const val REQ_GALLERY = 1

        // API 호출시 Parameter key값
        const val PARAM_KEY_IMAGE = "image"
        const val PARAM_KEY_PRODUCT_ID = "product_id"
        const val PARAM_KEY_REVIEW = "review_content"
        const val PARAM_KEY_RATING = "rating"
    }
}