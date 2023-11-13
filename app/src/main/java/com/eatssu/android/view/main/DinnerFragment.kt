package com.eatssu.android.view.main

//import com.eatssu.android.adapter.DodamAdapter
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
import com.eatssu.android.adapter.TodayMealAdapter
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentDinnerBinding
import com.eatssu.android.repository.MenuRepository
import com.eatssu.android.view.infopage.*
import com.eatssu.android.viewmodel.CalendarViewModel
import com.eatssu.android.viewmodel.MenuViewModel
import com.eatssu.android.viewmodel.factory.MenuViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DinnerFragment : Fragment() {
    private var _binding: FragmentDinnerBinding? = null
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
        _binding = FragmentDinnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuService = retrofit.create(MenuService::class.java)

        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")

        menuRepository = MenuRepository(menuService)
        menuViewModel = ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]

        // ViewModelProvider를 통해 ViewModel 가져오기
        val calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(viewLifecycleOwner, Observer { dataReceived ->
            menuDate =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived
            //숭실도담
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DODAM, Time.DINNER)
            menuViewModel.todayMealDataDodam.observe(viewLifecycleOwner, Observer { result ->
                //if (result.toString() != "[]") {
                    val dodamAdapter = TodayMealAdapter(result)
                    val recyclerView = binding.rvDodam
                    recyclerView.adapter = dodamAdapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
                    Log.d("post", "도담안널" + result)

                //} else {
                //    Log.d("post", "도담널" + result.toString())
//              //      binding.llDodam.visibility = View.GONE
                //}
            })

            //기숙사식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DORMITORY, Time.DINNER)
            menuViewModel.todayMealDataDormitory.observe(viewLifecycleOwner, Observer { result ->
                //if (result.toString() != "[]") {
                    val dodamAdapter = TodayMealAdapter(result)
                    val recyclerView = binding.rvDodam
                    recyclerView.adapter = dodamAdapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
                    Log.d("post", "기숙사 데이터 있음" + result.toString())
                //} else {
//                    binding.llGisik.visibility = View.GONE
                //}
            })
        })

            //학생식당
//            viewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, Time.LUNCH)
//            viewModel.todayMealDataHaksik.observe(viewLifecycleOwner, Observer { result ->
//                if (result.toString() != "[]") {
//                    val dodamAdapter = TodayMealAdapter(result)
//                    val recyclerView = binding.rv
//                    recyclerView.adapter = dodamAdapter
//                    recyclerView.layoutManager = LinearLayoutManager(context)
//                    recyclerView.setHasFixedSize(true)
//                    recyclerView.visibility = View.VISIBLE // 데이터가 있을 때 리사이클러뷰 표시
//                } else {
////                    binding.llHaksik.visibility = View.GONE
//                }
//            })
//        })

//        //더키친
//        viewModel.loadFixedMenu(Restaurant.THE_KITCHEN)
//        viewModel.fixedMenuDataKitchen.observe(viewLifecycleOwner, Observer { result ->
//            val kitchenAdapter = FixedMenuAdapter(result)
//            val recyclerView = binding.rvKitchen
//            recyclerView.adapter = kitchenAdapter
//            recyclerView.layoutManager = LinearLayoutManager(context)
//            recyclerView.setHasFixedSize(true)
//        })
//
//        //푸드코트
//        viewModel.loadFixedMenu(Restaurant.FOOD_COURT)
//        viewModel.fixedMenuDataFood.observe(viewLifecycleOwner, Observer { result ->
//            val foodAdapter = FixedMenuAdapter(result)
//            val recyclerView = binding.rvFood
//            recyclerView.adapter = foodAdapter
//            recyclerView.layoutManager = LinearLayoutManager(context)
//            recyclerView.setHasFixedSize(true)
//        })
//
//        //스낵코너
//        viewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
//        viewModel.fixedMenuDataSnack.observe(viewLifecycleOwner, Observer { result ->
//            val foodAdapter = FixedMenuAdapter(result)
//            val recyclerView = binding.rvSnack
//            recyclerView.adapter = foodAdapter
//            recyclerView.layoutManager = LinearLayoutManager(context)
//            recyclerView.setHasFixedSize(true)
//        })


        setupClickListeners() //info dialog
    }


    private fun setupClickListeners() {
        val intent = Intent(context, InfoActivity::class.java)

        binding.btnGisikInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.DORMITORY)
            context?.startActivity(intent)
        }

        binding.btnDodamInfo.setOnClickListener {
            intent.putExtra("restaurantType", Restaurant.DODAM)
            context?.startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}