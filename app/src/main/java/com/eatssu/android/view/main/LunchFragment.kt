package com.eatssu.android.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.adapter.*
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.view.infopage.*
import com.eatssu.android.viewmodel.MenuViewModel
import retrofit2.*
import androidx.lifecycle.Observer
import com.eatssu.android.repository.MenuRepository
import com.eatssu.android.viewmodel.factory.MenuViewModelFactory
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LunchFragment : Fragment() {
    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MenuViewModel

    private lateinit var retrofit: Retrofit
    private lateinit var menuService: MenuService

    private var menuDate: String = "20230714"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)

        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")


        val repository = MenuRepository(menuService)
        viewModel =
            ViewModelProvider(this, MenuViewModelFactory(repository))[MenuViewModel::class.java]

        //숭실도담
        viewModel.loadTodayMeal(menuDate, Restaurant.DODAM, Time.LUNCH)
        viewModel.todayMealDataDodam.observe(viewLifecycleOwner, Observer { result ->
            val dodamAdapter = TodayMealAdapter(result)
            val recyclerView = binding.rvLunchDodam
            recyclerView.adapter = dodamAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //기숙사식당
        viewModel.loadTodayMeal(menuDate, Restaurant.DOMITORY, Time.LUNCH)
        viewModel.todayMealDataDormitory.observe(viewLifecycleOwner, Observer { result ->
            val dodamAdapter = TodayMealAdapter(result)
            val recyclerView = binding.rvLunchDormitory
            recyclerView.adapter = dodamAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //학생식당
        viewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, Time.LUNCH)
        viewModel.todayMealDataHaksik.observe(viewLifecycleOwner, Observer { result ->
            val dodamAdapter = TodayMealAdapter(result)
            val recyclerView = binding.rvLunchHaksik
            recyclerView.adapter = dodamAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //더키친
        viewModel.loadFixedMenu(Restaurant.THE_KITCHEN)
        viewModel.fixedMenuDataKitchen.observe(viewLifecycleOwner, Observer { result ->
            val kitchenAdapter = FixedMenuAdapter(result)
            val recyclerView = binding.rvLunchKitchen
            recyclerView.adapter = kitchenAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //푸드코트
        viewModel.loadFixedMenu(Restaurant.FOOD_COURT)
        viewModel.fixedMenuDataFood.observe(viewLifecycleOwner, Observer { result ->
            val foodAdapter = FixedMenuAdapter(result)
            val recyclerView = binding.rvLunchFood
            recyclerView.adapter = foodAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })

        //스낵코너
        viewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
        viewModel.fixedMenuDataSnack.observe(viewLifecycleOwner, Observer { result ->
            val foodAdapter = FixedMenuAdapter(result)
            val recyclerView = binding.rvLunchSnack
            recyclerView.adapter = foodAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        })


        setupClickListeners() //info dialog
    }


    private fun setupClickListeners() {
        binding.btnHaksikInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Haksik::class.java))
        }
        binding.btnDodamInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Dodam::class.java))
        }
        binding.btnGisikInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Gisik::class.java))
        }
        binding.btnKitchenInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Kitchen::class.java))
        }
        binding.btnFoodInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Food::class.java))
        }
        binding.btnSnackInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Snack::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
