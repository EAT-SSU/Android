package com.eatssu.android.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.adapter.*
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.repository.MenuRepository
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.view.calendar.CalendarViewModel
import com.eatssu.android.view.infopage.*
import com.eatssu.android.viewmodel.MenuViewModel
import com.eatssu.android.viewmodel.factory.MenuViewModelFactory
import retrofit2.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// LunchFragment.kt
//class LunchFragment : Fragment() {
//    private var _binding: FragmentLunchBinding? = null
//    private val binding get() = _binding!!
//
//    lateinit var retrofit: Retrofit
//    lateinit var menuService: MenuService
//    // ... Existing code
//
//    private lateinit var viewModel: MenuViewModel
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initViewModel()
//        lodeData()
//    }
//
//    private fun initViewModel() {
////        val menuService = RetrofitImpl.nonRetrofit.create(MenuService::class.java)
//        val repository = MenuRepository(menuService)
//        viewModel =
//            ViewModelProvider(this, MenuViewModelFactory(repository)).get(MenuViewModel::class.java)
//    }
//
//    private fun lodeData() {
//        viewModel.getTodayMeal(Restaurant.DODAM, "LUNCH")
//            .observe(viewLifecycleOwner, { menuInfoList ->
//                setAdapterTodayMeal(menuInfoList, Restaurant.DODAM, binding.rvLunchDodam)
//            })
//
//        viewModel.getTodayMeal(Restaurant.HAKSIK, "LUNCH")
//            .observe(viewLifecycleOwner, { menuInfoList ->
//                setAdapterTodayMeal(menuInfoList, Restaurant.HAKSIK, binding.rvLunchHaksik)
//            })
//
//        // ... Load other data using viewModel.getTodayMeal and viewModel.getFixedMenu
//    }
//
//    private fun setAdapter(
//        menuList: List<GetFixedMenuResponseDto.FixMenuInfoList>,
//        recyclerView: RecyclerView,
//        restaurantType: Restaurant
//    ) {
//        val foodAdapter = FoodAdapter(menuList)
//        val snackAdapter = SnackAdapter(menuList)
//        val kitchenAdapter = KitchenAdapter(menuList)
//
//
//        val adapter = when (restaurantType) {
//            Restaurant.SNACK_CORNER -> snackAdapter
//            Restaurant.THE_KITCHEN -> kitchenAdapter
//            Restaurant.FOOD_COURT -> foodAdapter
//            else -> {
//                snackAdapter // 그냥
//            }
//        }
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.setHasFixedSize(true)
//    }
//
//    private fun setAdapterTodayMeal(
//        menuInfoList: GetTodayMealResponse,
//        restaurantType: Restaurant,
//        recyclerView: RecyclerView
//    ) {
//        val dodamAdapter = DodamAdapter(menuInfoList)
//        val haksikAdapter = HaksikAdapter(menuInfoList)
//        val gisikAdapter = GisikAdapter(menuInfoList)
//
//
//        val adapter = when (restaurantType) {
//            Restaurant.DODAM -> dodamAdapter
//            Restaurant.HAKSIK -> haksikAdapter
//            Restaurant.DOMITORY -> gisikAdapter
//            else -> {
//                gisikAdapter // 그냥
//            }
//        }
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.setHasFixedSize(true)
//    }
//
//
//}

class LunchFragment : Fragment() {
    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!

    lateinit var retrofit: Retrofit
    lateinit var menuService: MenuService

    private var menuDate : String = "20230714"

    val time:String = "LUNCH"
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

        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")

        init()
        lodeData()
    }

    fun init() {
        menuService = RetrofitImpl.nonRetrofit.create(MenuService::class.java)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun lodeData() {
        loadTodayMeal(Restaurant.DODAM, time, binding.rvLunchDodam)
        loadTodayMeal(Restaurant.HAKSIK, time,binding.rvLunchHaksik)
        loadTodayMeal(Restaurant.DOMITORY, time, binding.rvLunchDormitory)

        loadFixedMenu(Restaurant.FOOD_COURT, binding.rvLunchFood)
        loadFixedMenu(Restaurant.SNACK_CORNER, binding.rvLunchSnack)
        loadFixedMenu(Restaurant.THE_KITCHEN, binding.rvLunchKitchen)
    }


    private fun setAdapter(
        menuList: List<GetFixedMenuResponseDto.FixMenuInfoList>,
        recyclerView: RecyclerView,
        restaurantType: Restaurant
    ) {
        val foodAdapter = FoodAdapter(menuList)
        val snackAdapter = SnackAdapter(menuList)
        val kitchenAdapter = KitchenAdapter(menuList)


        val adapter = when (restaurantType) {
            Restaurant.SNACK_CORNER -> snackAdapter
            Restaurant.THE_KITCHEN -> kitchenAdapter
            Restaurant.FOOD_COURT -> foodAdapter
            else -> {
                snackAdapter // 그냥
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }

    private fun setAdapterTodayMeal(
        menuInfoList: GetTodayMealResponseDto,
        restaurantType: Restaurant,
        recyclerView: RecyclerView
        ) {
        val dodamAdapter = DodamAdapter(menuInfoList)
        val haksikAdapter = HaksikAdapter(menuInfoList)
        val gisikAdapter = GisikAdapter(menuInfoList)


        val adapter = when (restaurantType) {
            Restaurant.DODAM -> dodamAdapter
            Restaurant.HAKSIK -> haksikAdapter
            Restaurant.DOMITORY -> gisikAdapter
            else -> {
                gisikAdapter // 그냥
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTodayMeal(
        restaurantType: Restaurant,
        time: String,
        recyclerView: RecyclerView
    ) {
        val monthFormat =
            DateTimeFormatter.ofPattern("yyyyMMdd").withLocale (Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)

        // ViewModelProvider를 통해 ViewModel 가져오기
        val viewModel = ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)

        // ViewModel에서 데이터 가져오기
        viewModel.getData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { dataReceived ->
            menuDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived
            //menuDate = "202307$dataReceived"
            Log.d("lunchdate", menuDate)
            menuService.getTodayMeal(menuDate, restaurantType.toString(),time)
                .enqueue(object : Callback<GetTodayMealResponseDto> {
                    override fun onResponse(
                        call: Call<GetTodayMealResponseDto>,
                        response: Response<GetTodayMealResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            body?.let {
                                setAdapterTodayMeal(it, restaurantType,recyclerView)
                            }
                            Log.d("post", "onResponse 성공 투데이밀" + response.body())

                        } else {
                            Log.d("post", "onResponse 실패 투데이밀" + response.code()+response.message())
                        }
                    }

                    override fun onFailure(call: Call<GetTodayMealResponseDto>, t: Throwable) {
                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                    }
                })
        })
        //date 자리에 localDate나 호출해서 불러온 날짜를 넣으면 됨
        //지금은 날짜를 20230714로 고정해두었음

    }


    private fun loadFixedMenu(restaurantType: Restaurant, recyclerView: RecyclerView) {

        menuService.getFixMenu(restaurantType.toString())
            .enqueue(object : Callback<GetFixedMenuResponseDto> {
                override fun onResponse(
                    call: Call<GetFixedMenuResponseDto>,
                    response: Response<GetFixedMenuResponseDto>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            setAdapter(it.fixMenuInfoList, recyclerView, restaurantType)
                        }
                        Log.d("post", "onResponse 성공" + response.body())

                    } else {
                        Log.d("post", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<GetFixedMenuResponseDto>, t: Throwable) {
                    Log.d("post", "onFailure 에러: ${t.message}")
                }
            })
    }

    private fun getNonFixed() {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
