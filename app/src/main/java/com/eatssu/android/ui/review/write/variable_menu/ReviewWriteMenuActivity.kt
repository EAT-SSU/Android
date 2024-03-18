package com.eatssu.android.ui.review.write.variable_menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.MealService
import com.eatssu.android.databinding.ActivityReviewWriteMenuBinding
import com.eatssu.android.ui.review.write.ReviewWriteRateActivity
import com.eatssu.android.util.RetrofitImpl.retrofit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReviewWriteMenuActivity :
    BaseActivity<ActivityReviewWriteMenuBinding>(ActivityReviewWriteMenuBinding::inflate) {

    private lateinit var variableMenuPickAdapter: VariableMenuPickAdapter
    private lateinit var viewModel: VariableMenuViewModel
    private lateinit var mealService: MealService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정

        val mealId = intent.getLongExtra("itemId", -1)

        initViewModel()
        bindData(mealId)
        setClickListener()
    }


    private fun initViewModel() {
        mealService = retrofit.create(MealService::class.java)
        viewModel = ViewModelProvider(
            this,
            VariableMenuModelFactory(mealService)
        )[VariableMenuViewModel::class.java]
    }

    private fun bindData(mealId: Long) {
        viewModel.findMenuItemByMealId(mealId)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    Log.d("ReviewWriteMenuActivity", "!!!받은" + it.menuOfMeal.toString())

                    variableMenuPickAdapter = VariableMenuPickAdapter(it.menuOfMeal!!)
                    binding.rvMenuPicker.apply {
                        adapter = variableMenuPickAdapter
                        layoutManager = LinearLayoutManager(this@ReviewWriteMenuActivity)
                        setHasFixedSize(true)
                    }
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
            Log.d("sendNextItem", items.size.toString())
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
