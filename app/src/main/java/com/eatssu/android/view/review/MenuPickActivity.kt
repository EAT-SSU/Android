package com.eatssu.android.view.review

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.adapter.MenuPickAdapter
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMenuPickBinding
import com.eatssu.android.repository.MenuRepository
import com.eatssu.android.viewmodel.CalendarViewModel
import com.eatssu.android.viewmodel.MenuViewModel
import com.eatssu.android.viewmodel.ReviewViewModel
import com.eatssu.android.viewmodel.factory.MenuViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MenuPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPickBinding


    private lateinit var viewModel: MenuViewModel
    private lateinit var reviewViewModel: ReviewViewModel


    private lateinit var menuService: MenuService
    private lateinit var reviewService: ReviewService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)
        reviewService = RetrofitImpl.retrofit.create(ReviewService::class.java)


        val itemName = intent.getStringArrayListExtra("itemName")
        val itemId = intent.getLongExtra("itemId", 0)

        Log.d("post", "받은" + itemName.toString())


        val menuRepository = MenuRepository(menuService)
        viewModel =
            ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]


        // ViewModelProvider를 통해 ViewModel 가져오기
        val calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(this, Observer { dataReceived ->
            val menuDate =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived
            //숭실도담
            viewModel.loadTodayMeal(menuDate, Restaurant.DODAM, Time.LUNCH)
            viewModel.todayMealDataDodam.observe(this, Observer { result ->
                val menuPickAdapter = MenuPickAdapter(result)
                val recyclerView = binding.rvMenuPicker
                recyclerView.adapter = menuPickAdapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.setHasFixedSize(true)
                menuPickAdapter.setOnCheckBoxClickListener(object :
                    MenuPickAdapter.CheckBoxClickListener {
                    override fun onClickCheckBox(flag: Int, pos: Int) {
                        /* 체크박스를 눌렀을 때 리스너로 상태(눌렸는지, 위치)를 불러옴 */
                        Log.d("__star__", flag.toString() + "" + pos)
                    }
                })
            })


//        /* 리사이클러뷰 어댑터 */
//        val menuPickAdapter = MenuPickAdapter(itemName)
//        val recyclerView = binding.rvMenuPicker
//        recyclerView.adapter = menuPickAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)


        })
    }
}

