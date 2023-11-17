package com.eatssu.android.ui.review.write

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.repository.MenuRepository
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.ActivityReviewWriteMenuBinding
import com.eatssu.android.ui.main.menu.MenuViewModel
import com.eatssu.android.ui.main.menu.MenuViewModelFactory
import com.eatssu.android.util.RetrofitImpl.retrofit

class ReviewWriteMenuActivity : BaseActivity<ActivityReviewWriteMenuBinding>(ActivityReviewWriteMenuBinding::inflate) {

    private lateinit var menuPickAdapter: MenuPickAdapter
    private lateinit var viewModel: MenuViewModel
    private lateinit var repository: MenuRepository
    private lateinit var menuService: MenuService
    private lateinit var items: ArrayList<Pair<String, Long>>
    private var currentItemIndex = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = title

        initServices()

        val mealId = intent.getLongExtra("mealId", -1)
        initViewModel(mealId)

        observeMenuByMealId()

        initNextButtonClickListener()
    }

    private fun initServices() {
        menuService = retrofit.create(MenuService::class.java)
        repository = MenuRepository(menuService)
    }

    private fun initViewModel(mealId: Long) {
        viewModel = ViewModelProvider(this, MenuViewModelFactory(repository))[MenuViewModel::class.java]
        Log.d("MenuPickActivity", "!!!받은" + viewModel.findMenuItemByMealId(mealId).toString())
        Log.d("MenuPickActivity", "!!!받은2" + viewModel.menuBymealId.toString())
    }

    private fun observeMenuByMealId() {
        viewModel.menuBymealId.observe(this) { menuList ->
            menuPickAdapter = MenuPickAdapter(menuList)
            binding.rvMenuPicker.apply {
                adapter = menuPickAdapter
                layoutManager = LinearLayoutManager(this@ReviewWriteMenuActivity)
                setHasFixedSize(true)
            }
        }
    }

    private fun initNextButtonClickListener() {
        binding.btnNextReview.setOnClickListener {
            items = menuPickAdapter.sendItem()
            if (currentItemIndex < items.size) {
                val item = items[currentItemIndex]
                startWriteReviewActivity(item)
            } else {
                currentItemIndex = 0
                finish()
            }
        }
    }

    private fun startWriteReviewActivity(item: Pair<String, Long>) {
        val intent = Intent(this, ReviewWriteRateActivity::class.java)
        intent.putExtra("itemName", item.first)
        intent.putExtra("itemId", item.second)
        Log.d("MenuPickActivity", "넘길게$item")
        startActivityForResult(intent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            currentItemIndex++
            binding.btnNextReview.performClick()
        }
    }
}
