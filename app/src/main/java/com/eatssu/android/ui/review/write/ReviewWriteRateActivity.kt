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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.ImageService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewWriteRateBinding
import com.eatssu.android.util.RetrofitImpl.mRetrofit
import com.eatssu.android.util.RetrofitImpl.retrofit
import com.eatssu.android.util.extension.showToast
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class ReviewWriteRateActivity :
    BaseActivity<ActivityReviewWriteRateBinding>(ActivityReviewWriteRateBinding::inflate) {

    private lateinit var uploadReviewViewModel: UploadReviewViewModel
    private lateinit var imageviewModel: ImageViewModel

    private lateinit var reviewService: ReviewService
    private lateinit var imageService: ImageService

    private val PERMISSION_REQUEST_CODE = 1

    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""


    private var imageFile: File? = null
    private var compressedImage: File? = null


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

        setOnClickListener()


        imageService = mRetrofit.create(ImageService::class.java)
        reviewService = retrofit.create(ReviewService::class.java)


        uploadReviewViewModel = ViewModelProvider(
            this,
            ReviewWriteViewModelFactory(reviewService)
        )[UploadReviewViewModel::class.java]
        imageviewModel =
            ViewModelProvider(this, ImageViewModelFactory(imageService))[ImageViewModel::class.java]

        binding.viewModel = uploadReviewViewModel

    }

    fun setOnClickListener() {
        // 이미지 추가 버튼 클릭 리스너 설정
        binding.ibAddPic.setOnClickListener {
            Log.d("ReviewWriteRateActivity", "클릭")

            checkPermission()
        }

        binding.btnNextReview2.setOnClickListener {
            if (binding.rbMain.rating.toInt() == 0 || binding.rbAmount.rating.toInt() == 0 || binding.rbTaste.rating.toInt() == 0) {
                Toast.makeText(this, "별점을 등록해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if ((comment?.trim()?.length ?: 0) < 3) {
                Toast.makeText(this, "3자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //파일 업로드가 끝났거나, 파일을 첨부하지 않거나
            if (imageFile?.exists() == true) {
                compressImage()

                Log.d("ImageViewMdoel", "s3 시작")

            } else {
                postReview()

            }
        }

        binding.btnDelete.setOnClickListener { deleteImage() }

    }


    private fun compressImage() {
        imageFile?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression
                compressedImage = Compressor.compress(this@ReviewWriteRateActivity, imageFile)
                Log.d("ImageViewModel", "압축 됨+" + (compressedImage?.length()?.div(1024)).toString())
                setCompressedImage()
            }
        } ?: showError("Please choose an image!")
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setCompressedImage() {
        compressedImage?.let {
            imageviewModel.setImageFile(it)
            imageviewModel.saveS3() //이미지 url 반환 api 호출
            lifecycleScope.launch {
                imageviewModel.uiState.collectLatest {
                    if (it.isDone) {
                        postReview()
                    }
                }
            }
//            post()

//            compressedImageView.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d("Compressor", "Compressed image save in " + getReadableFileSize(it.length()))
        }
    }

//    fun post(){
//        imageviewModel.imageUrl.value?.let {
//            postReview()
//        }
//    }

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }


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


                binding.ivImage.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            }
        }

    }

    // 이미지 실제 경로 반환
    private fun getRealPathFromURI(uri: Uri): String {

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

        Log.d("ReviewWriteRateActivity", result)
        return result
    }

    // 갤러리를 부르는 메서드
    private fun checkPermission() {

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
                Log.d("ReviewWriteRateActivity", "권한 없음")

            } else {
                openGallery()

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
                Log.d("ReviewWriteRateActivity", "권한 없음")

            } else {
                openGallery()
            }
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // intent의 data와 type을 동시에 설정하는 메서드
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )

        imageResult.launch(intent)
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (imageFile?.exists() == true) {
            Toast.makeText(this, "리뷰 작성을 중지합니다.", Toast.LENGTH_SHORT).show()
            binding.ivImage.setImageDrawable(null)
            imageFile!!.delete() //file을 날린다.
            compressedImage?.delete() //file을 날린다.

        }
    }


    private fun deleteImage() {
        Log.d("ReviewWriteRateActivity", imageFile.toString())
        if (imageFile?.exists() == true) {
            Toast.makeText(this, "이미지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            binding.ivImage.setImageDrawable(null)
            imageFile!!.delete() //file을 날린다.
            compressedImage?.delete() //file을 날린다.

            binding.ivImage.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE

            imageviewModel.deleteFile()

        } else {
            Toast.makeText(this, "이미지를 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postReview() {

        //Todo imageurl을 체크해야하는 이유?
        uploadReviewViewModel.setReviewData(
            itemId,
            binding.rbMain.rating.toInt(),
            binding.rbAmount.rating.toInt(),
            binding.rbTaste.rating.toInt(),
            comment.toString(),
            imageviewModel.imageUrl.value ?: ""
        )

        uploadReviewViewModel.postReview()
        Log.d("ReviewWriteRateActivity", "리뷰 전송")


        lifecycleScope.launch {
            uploadReviewViewModel.uiState.collectLatest {
                if (it.error) {
                    showToast(uploadReviewViewModel.uiState.value.toastMessage)
                }
                if (!it.error && !it.loading && it.isUpload) {
                    showToast(uploadReviewViewModel.uiState.value.toastMessage)
                    Log.d("ReviewWriteRateActivity", "리뷰 작성 성공")
                    finish()
                }
            }
        }


    }

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