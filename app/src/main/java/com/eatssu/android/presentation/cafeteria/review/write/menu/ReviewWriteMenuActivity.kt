package com.eatssu.android.presentation.cafeteria.review.write.menu

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.databinding.ActivityReviewWriteMenuBinding
import com.eatssu.android.presentation.base.ActivityCompanionWithArgs
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteRateActivity
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@AndroidEntryPoint
class ReviewWriteMenuActivity :
    BaseActivity<ActivityReviewWriteMenuBinding>(
        ActivityReviewWriteMenuBinding::inflate,
        ScreenId.REVIEW_V1_WRITE
    ) {

    @Parcelize
    data class Args(
        val itemId: Long,
        val menuType: String? = null
    ) : Parcelable

    companion object : ActivityCompanionWithArgs<Args>(ReviewWriteMenuActivity::class, Args::class)

    private val viewModel: VariableMenuViewModel by viewModels()
    private val mealId by lazy { intentOptions?.itemId ?: -1 }

    private lateinit var variableMenuPickAdapter: VariableMenuPickAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정

        loadData()
        bindData()
        setClickListener()
    }

    fun loadData() {
        viewModel.findMenuItemByMealId(mealId)
    }

    private fun bindData() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    Timber.d("받은" + it.menuOfMeal.toString())

                    variableMenuPickAdapter = VariableMenuPickAdapter(it.menuOfMeal!!)
                    binding.rvMenuPicker.apply {
                        adapter = variableMenuPickAdapter
                        layoutManager = LinearLayoutManager(this@ReviewWriteMenuActivity)
                        setHasFixedSize(true)
                    }
                    // 데이터 바인딩이 완료된 후 클릭 리스너 설정
//                    setClickListener()
                }
            }
        }
    }

    private fun setClickListener() {
        binding.btnNextReview.setOnClickListener {
            sendNextItem(variableMenuPickAdapter.sendCheckedItem())
        }
    }

    private fun sendNextItem(items: ArrayList<Pair<String, Long>>) {
        for (i in 0 until items.size) {
            Timber.d("sendNextItem: " + items.size.toString())
            // 현재 아이템을 가져옴

            val currentItem = items[i]

            // 다음 아이템을 전달하기 위해 새로운 companion 패턴 사용
            ReviewWriteRateActivity.start(
                this,
                ReviewWriteRateActivity.Args(
                    itemName = currentItem.first,
                    itemId = currentItem.second,
                    itemCount = items.size.toLong()
                )
            )

            // 만약 마지막 아이템이면 현재 액티비티 종료
            if (i == items.size - 1) {
                finish()
            }
        }
    }
}
