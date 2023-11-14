package com.eatssu.android.ui.review.write

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.util.RetrofitImpl.retrofit
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMenuPickBinding
import com.eatssu.android.data.repository.MenuRepository
import com.eatssu.android.ui.main.menu.MenuViewModel
import com.eatssu.android.data.viewmodelFactory.MenuViewModelFactory


class MenuPickActivity : BaseActivity<ActivityMenuPickBinding>(ActivityMenuPickBinding::inflate) {

    private lateinit var menuPickAdapter: MenuPickAdapter

    private lateinit var viewModel: MenuViewModel
    private lateinit var repository: MenuRepository

    private lateinit var menuService: MenuService
    private lateinit var reviewService: ReviewService

    private lateinit var items: ArrayList<Pair<String, Long>>

    private var currentItemIndex = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "리뷰 남기기" // 툴바 제목 설정


        menuService = retrofit.create(MenuService::class.java)
        reviewService = retrofit.create(ReviewService::class.java)
        repository = MenuRepository(menuService)

        val mealId = intent.getLongExtra("mealId",-1)
        viewModel = ViewModelProvider(this, MenuViewModelFactory(repository))[MenuViewModel::class.java]

        Log.d("post", "!!!받은" + viewModel.findMenuItemByMealId(mealId).toString())
        Log.d("post", "!!!받은2" + viewModel.menuBymealId.toString())

        viewModel.menuBymealId.observe(this) { menuList ->
            menuPickAdapter = MenuPickAdapter(menuList)
            val recyclerView = binding.rvMenuPicker
            recyclerView.adapter = menuPickAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            //구분선 주석처리
//            recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        }



        // "다음" 버튼 클릭 시, 다음 항목의 리뷰화면을 시작합니다.
        binding.btnNextReview.setOnClickListener {

            items = menuPickAdapter.sendItem()

            if (currentItemIndex < items.size) {
                val item = items[currentItemIndex]
                val intent = Intent(this, WriteReviewActivity::class.java)
                intent.putExtra("itemName", item.first)
                intent.putExtra("itemId", item.second)

                Log.d("post", "넘길게$item")
                startActivityForResult(intent, 1)
            } else {
                // 모든 항목을 처리한 경우, A 화면으로 돌아갑니다.
                currentItemIndex = 0
                finish()
            }
        }
    }

    // B 화면에서 돌아올 때 호출되는 콜백 메서드
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // B 화면에서 돌아온 경우, 다음 항목을 처리합니다.
            currentItemIndex++
            // "Go to B Screen" 버튼을 클릭하여 B 화면을 다시 열 수 있도록 합니다.
            binding.btnNextReview.performClick()
        }
    }

}

