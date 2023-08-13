package com.eatssu.android.ui.main

import RetrofitImpl
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
import com.eatssu.android.data.enums.RestaurantType
import com.eatssu.android.data.model.response.GetFixedMenuResponse
import com.eatssu.android.data.model.response.GetTodayMealResponse
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.ui.calendar.CalendarViewModel
import com.eatssu.android.ui.calendar.MenuDate
import com.eatssu.android.ui.infopage.*
import retrofit2.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
        loadTodayMeal(RestaurantType.DODAM, time, binding.rvLunchDodam)
        loadTodayMeal(RestaurantType.HAKSIK, time,binding.rvLunchHaksik)
        loadTodayMeal(RestaurantType.DOMITORY, time, binding.rvLunchDormitory)

        loadFixedMenu(RestaurantType.FOOD_COURT, binding.rvLunchFood)
        loadFixedMenu(RestaurantType.SNACK_CORNER, binding.rvLunchSnack)
        loadFixedMenu(RestaurantType.THE_KITCHEN, binding.rvLunchKitchen)
    }


    private fun setAdapter(
        menuList: List<GetFixedMenuResponse.FixMenuInfoList>,
        recyclerView: RecyclerView,
        restaurantType: RestaurantType
    ) {
        val foodAdapter = FoodAdapter(menuList)
        val snackAdapter = SnackAdapter(menuList)
        val kitchenAdapter = KitchenAdapter(menuList)


        val adapter = when (restaurantType) {
            RestaurantType.SNACK_CORNER -> snackAdapter
            RestaurantType.THE_KITCHEN -> kitchenAdapter
            RestaurantType.FOOD_COURT -> foodAdapter
            else -> {
                snackAdapter // 그냥
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }

    private fun setAdapterTodayMeal(
        menuInfoList: GetTodayMealResponse,
        restaurantType: RestaurantType,
        recyclerView: RecyclerView
        ) {
        val dodamAdapter = DodamAdapter(menuInfoList)
        val haksikAdapter = HaksikAdapter(menuInfoList)
        val gisikAdapter = GisikAdapter(menuInfoList)


        val adapter = when (restaurantType) {
            RestaurantType.DODAM -> dodamAdapter
            RestaurantType.HAKSIK -> haksikAdapter
            RestaurantType.DOMITORY -> gisikAdapter
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
        restaurantType: RestaurantType,
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
                .enqueue(object : Callback<GetTodayMealResponse> {
                    override fun onResponse(
                        call: Call<GetTodayMealResponse>,
                        response: Response<GetTodayMealResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            body?.let {
                                setAdapterTodayMeal(it, restaurantType,recyclerView)
                            }
                            Log.d("post", "onResponse 성공" + response.body())

                        } else {
                            Log.d("post", "onResponse 실패 투데이밀" + response.code()+response.message())
                        }
                    }

                    override fun onFailure(call: Call<GetTodayMealResponse>, t: Throwable) {
                        Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}" + "ddd")
                    }
                })
        })
        //date 자리에 localDate나 호출해서 불러온 날짜를 넣으면 됨
        //지금은 날짜를 20230714로 고정해두었음

    }


    private fun loadFixedMenu(restaurantType: RestaurantType, recyclerView: RecyclerView) {

        menuService.getFixMenu(restaurantType.toString())
            .enqueue(object : Callback<GetFixedMenuResponse> {
                override fun onResponse(
                    call: Call<GetFixedMenuResponse>,
                    response: Response<GetFixedMenuResponse>
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

                override fun onFailure(call: Call<GetFixedMenuResponse>, t: Throwable) {
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
