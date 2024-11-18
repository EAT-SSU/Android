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
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityReviewWriteRateBinding
import com.eatssu.android.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

@AndroidEntryPoint
class ReviewWriteRateActivity :
    BaseActivity<ActivityReviewWriteRateBinding>(ActivityReviewWriteRateBinding::inflate) {

    private val viewModel: UploadReviewViewModel by viewModels()
    private val imageviewModel: ImageViewModel by viewModels()

    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""

    private var imageFile: File? = null
    private var compressedImage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정
        binding.viewModel = viewModel

        itemName = intent.getStringExtra("itemName").toString()
        Timber.d("고정메뉴 $itemName")

        itemId = intent.getLongExtra("itemId", -1)

        // 현재 메뉴명을 표시합니다.
        binding.menu.text = itemName

        // 외부 저장소에 대한 런타임 퍼미션 요청
        requestStoragePermission()

        // 텍스트 리뷰 입력 관련 설정
        setupTextReviewInput()

        setOnClickListener()
    }

    fun setOnClickListener() {
        // 이미지 추가 버튼 클릭 리스너 설정
        binding.ibAddPic.setOnClickListener {
            Timber.d("클릭")

            checkPermission()
        }

        binding.btnNextReview2.setOnClickListener {
            if (binding.rbMain.rating.toInt() == 0 || binding.rbAmount.rating.toInt() == 0 || binding.rbTaste.rating.toInt() == 0) {
                showToast("별점을 등록해주세요")
            }

            if ((comment?.trim()?.length ?: 0) < 3) {
                showToast("3자 이상 입력해주세요")
            }

            //파일 업로드가 끝났거나, 파일을 첨부하지 않거나
            if (imageFile?.exists() == true) {
                showToast("리뷰 업로드 중")
                compressImage()

                Timber.d("s3 시작")

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
                Timber.d("압축 된 사이즈+" + (compressedImage?.length()?.div(1024)).toString())
                setCompressedImage()
            }
        } ?: showError("Please choose an image!")
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setCompressedImage() {
        compressedImage?.let { it ->
            imageviewModel.setImageFile(it)
            imageviewModel.saveS3() //이미지 url 반환 api 호출
            lifecycleScope.launch {
                imageviewModel.uiState.collectLatest {
                    if (it.isImageUploadDone) {
                        postReview()
                    }
                }
            }
            Timber.d("Compressed image save in " + getReadableFileSize(it.length()))
        }
    }


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

        Timber.d("realPath: $result")
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
                    ), PERMISSION_REQUEST_CODE
                )
                Timber.e("권한 없음")

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
                    ), PERMISSION_REQUEST_CODE
                )
                Timber.e("권한 없음")

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
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
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

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
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
        Timber.d("imageFile: " + imageFile.toString())
        if (imageFile?.exists() == true) {
            showToast("이미지가 삭제되었습니다.")
            binding.ivImage.setImageDrawable(null)
            imageFile!!.delete() //file을 날린다.
            compressedImage?.delete() //file을 날린다.

            binding.ivImage.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE

            imageviewModel.deleteFile()

        } else {
            showToast("이미지를 삭제할 수 없습니다.")
        }
    }

    private fun postReview() {

        //Todo imageurl을 체크해야하는 이유?
        viewModel.setReviewData(
            itemId,
            binding.rbMain.rating.toInt(),
            binding.rbAmount.rating.toInt(),
            binding.rbTaste.rating.toInt(),
            comment.toString(),
            imageviewModel.imageUrl.value ?: ""
        )

        viewModel.postReview()
        Timber.d("리뷰 전송")


        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it.error) {
                    showToast(viewModel.uiState.value.toastMessage)
                }
                if (!it.error && !it.loading && it.isUpload) {
                    showToast(viewModel.uiState.value.toastMessage)
                    Timber.d("리뷰 작성 성공")
                    finish()
                }
            }
        }
    }

    companion object {

        const val REVIEW_MIN_LENGTH = 10

        // 갤러리 권한 요청
        const val PERMISSION_REQUEST_CODE = 1
    }
}