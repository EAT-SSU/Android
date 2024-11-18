package com.eatssu.android.ui.review.write.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityReviewWriteMenuBinding
import com.eatssu.android.ui.review.write.ReviewWriteRateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReviewWriteMenuActivity :
    BaseActivity<ActivityReviewWriteMenuBinding>(ActivityReviewWriteMenuBinding::inflate) {

    private val viewModel: VariableMenuViewModel by viewModels()
    private var mealId: Long = -1

    private lateinit var variableMenuPickAdapter: VariableMenuPickAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정

        getIndex()
        lodeData()
        bindData()
        setClickListener()
    }

    fun getIndex() {
        mealId = intent.getLongExtra("itemId", -1)
    }

    fun lodeData() {
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

            // 다음 아이템을 전달하기 위해 Intent 생성
            val intent = Intent(this, ReviewWriteRateActivity::class.java)
            intent.putExtra("itemName", currentItem.first)
            intent.putExtra("itemId", currentItem.second)

            // BActivity 실행
            startActivity(intent)

            // 만약 마지막 아이템이면 현재 액티비티 종료
            if (i == items.size - 1) {
                finish()
            }
        }
    }
}
