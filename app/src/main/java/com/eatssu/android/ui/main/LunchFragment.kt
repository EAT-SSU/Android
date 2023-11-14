package com.eatssu.android.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.adapter.*
import com.eatssu.android.util.RetrofitImpl.retrofit
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.data.repository.MenuRepository
import com.eatssu.android.ui.info.*
import com.eatssu.android.ui.main.menu.FixedMenuAdapter
import com.eatssu.android.ui.main.menu.MenuViewModel
import com.eatssu.android.ui.main.menu.TodayMealAdapter
import com.eatssu.android.ui.main.calendar.CalendarViewModel
import com.eatssu.android.data.viewmodelFactory.MenuViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


@AndroidEntryPoint
class LunchFragment : Fragment() {
    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var menuService: MenuService
    private lateinit var menuRepository: MenuRepository

    private lateinit var menuDate: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuService = retrofit.create(MenuService::class.java)
        menuRepository = MenuRepository(menuService)
        menuViewModel = ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]


        // ViewModelProvider를 통해 ViewModel 가져오기
        val calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(viewLifecycleOwner, Observer { dataReceived ->
            Log.d("lunchdate", dataReceived)
            val dateFormat = DateTimeFormatter.ofPattern("dd")
            val monthFormat = DateTimeFormatter.ofPattern("yyyyMM")
            val preSunday: LocalDateTime = LocalDateTime.now().with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.SUNDAY
                )
            )

            if (Integer.parseInt(preSunday.format(dateFormat)) > 24) {
                if (Integer.parseInt(dataReceived) > 24)
                    menuDate =
                        preSunday.format(monthFormat) + dataReceived
                else if (Integer.parseInt(dataReceived) < 7)
                    menuDate =
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived
            }
            else
                menuDate =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived

            //숭실도담
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DODAM, Time.LUNCH)
            menuViewModel.todayMealDataDodam.observe(viewLifecycleOwner, Observer { result ->
                //if (result.toString() != "[]") {
                val dodamAdapter = TodayMealAdapter(result)
                val recyclerView = binding.rvDodam
                recyclerView.adapter = dodamAdapter
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.setHasFixedSize(true)
                recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
                Log.d("post","도담안널"+result)

                //}
                //else{
                //    Log.d("post","도담널"+result.toString())
                //    binding.llDodam.visibility = View.GONE
                //}
            })

            //기숙사식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DORMITORY, Time.LUNCH)
            menuViewModel.todayMealDataDormitory.observe(viewLifecycleOwner, Observer { result ->
                //if (result.toString()!= "[]") {
                val dodamAdapter = TodayMealAdapter(result)
                val recyclerView = binding.rvDormitory
                recyclerView.adapter = dodamAdapter
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.setHasFixedSize(true)
                recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
                Log.d("post","기숙사 데이터 있음"+result.toString())
                //} else {
                //   binding.llGisik.visibility = View.GONE
                //}
            })

            //학생식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, Time.LUNCH)
            menuViewModel.todayMealDataHaksik.observe(viewLifecycleOwner, Observer { result ->
                //    if (result.toString() != "[]") {
                val dodamAdapter = TodayMealAdapter(result)
                val recyclerView = binding.rvHaksik
                recyclerView.adapter = dodamAdapter
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.setHasFixedSize(true)
                recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
                //    } else {
                //        binding.llHaksik.visibility = View.GONE
                //    }
            })
        })

//        //더키친
//        menuViewModel.loadFixedMenu(Restaurant.THE_KITCHEN)
//        menuViewModel.fixedMenuDataKitchen.observe(viewLifecycleOwner, Observer { result ->
//            val kitchenAdapter = FixedMenuAdapter(result)
//            val recyclerView = binding.rvKitchen
//            recyclerView.adapter = kitchenAdapter
//            recyclerView.layoutManager = LinearLayoutManager(context)
//            recyclerView.setHasFixedSize(true)
//        })

        //푸드코트
        menuViewModel.loadFixedMenu(Restaurant.FOOD_COURT)
        menuViewModel.fixedMenuDataFood.observe(viewLifecycleOwner, Observer { result ->
            val foodAdapter = FixedMenuAdapter(result)
            val recyclerView = binding.rvFood
            recyclerView.adapter = foodAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //스낵코너
        menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
        menuViewModel.fixedMenuDataSnack.observe(viewLifecycleOwner, Observer { result ->
            val foodAdapter = FixedMenuAdapter(result)
            val recyclerView = binding.rvSnack
            recyclerView.adapter = foodAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })


        setupClickListeners() //info dialog
    }


    private fun setupClickListeners() {
        val intent = Intent(context, InfoActivity::class.java)

        binding.btnHaksikInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.HAKSIK)
            context?.startActivity(intent)
        }
        binding.btnDodamInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.DODAM)
            context?.startActivity(intent)
        }
        binding.btnGisikInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.DORMITORY)
            context?.startActivity(intent)
        }
        binding.btnFoodInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.FOOD_COURT)
            context?.startActivity(intent)
        }
        binding.btnSnackInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.SNACK_CORNER)
            context?.startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
