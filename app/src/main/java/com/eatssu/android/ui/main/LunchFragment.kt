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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.adapter.*
import com.eatssu.android.data.enums.RestaurantType
import com.eatssu.android.data.model.response.GetFixedMenuResponse
import com.eatssu.android.data.model.response.GetTodayMealResponse
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.ui.infopage.*
import retrofit2.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LunchFragment : Fragment() {
    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!

    lateinit var retrofit: Retrofit
    lateinit var menuService: MenuService

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
        init()
        lodeData()
    }

    fun init() {
        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)
        setupClickListeners()

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
        getTodayMeal(RestaurantType.DODAM,binding.rvLunchDodam)
        getTodayMeal(RestaurantType.HAKSIK,binding.rvLunchHaksik)

        getFixedMenu(RestaurantType.FOOD_COURT, binding.rvLunchFood)
        getFixedMenu(RestaurantType.SNACK_CORNER, binding.rvLunchSnack)
        getFixedMenu(RestaurantType.THE_KITCHEN, binding.rvLunchKitchen)
    }



    private fun setAdapter(
        menuList: GetFixedMenuResponse,
        recyclerView: RecyclerView,
        restaurantType: RestaurantType
    ) {
//        val foodAdapter = FoodAdapter(menuList)
//        val snackAdapter = SnackAdapter(menuList)
//        val kitchenAdapter = KitchenAdapter(menuList)
//
//
//        val adapter = when (restaurantType) {
//            RestaurantType.SNACK_CORNER -> snackAdapter
//            RestaurantType.THE_KITCHEN -> kitchenAdapter
//            RestaurantType.FOOD_COURT -> foodAdapter
//            else -> {
//                snackAdapter // 그냥
//            }
//        }

//        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }
    private fun setAdapterLunch(
        menuInfoList: GetTodayMealResponse,
        recyclerView: RecyclerView,
        restaurantType: RestaurantType
    ) {
//        val dodamAdapter = DodamAdapter(menuInfoList)
//        val haksikAdapter = HaksikAdapter(menuInfoList)

//
//        val adapter = when (restaurantType) {
//            RestaurantType.DODAM -> dodamAdapter
//            RestaurantType.HAKSIK -> haksikAdapter
//            else -> {
//                dodamAdapter // 그냥
//            }
//        }
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.setHasFixedSize(true)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayMeal(restaurantType: RestaurantType, recyclerView: RecyclerView) {
//        val date = LocalDate()


        val monthFormat =
            DateTimeFormatter.ofPattern("yyyyMMdd").withLocale(Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)


        menuService.getTodayMeal("20230714","DOMITORY","LUNCH")
            .enqueue(object : Callback<GetTodayMealResponse> {
                override fun onResponse(
                    call: Call<GetTodayMealResponse>,
                    response: Response<GetTodayMealResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
//                        body?.let {
//                            setAdapterLunch(it, recyclerView, restaurantType)
//                        }
                        Log.d("post", "onResponse 성공"+response.body())

                    } else {
                        Log.d("post", "onResponse 실패 tlqkf")
                    }
                }

                override fun onFailure(call: Call<GetTodayMealResponse>, t: Throwable) {
                    Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}"+"ddd")
                }
            })
    }




    private fun getFixedMenu(restaurantType: RestaurantType, recyclerView: RecyclerView) {

        menuService.getFixMenu("FOOD_COURT")
            .enqueue(object : Callback<GetFixedMenuResponse> {
                override fun onResponse(
                    call: Call<GetFixedMenuResponse>,
                    response: Response<GetFixedMenuResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
//                        body?.let {
//                            setAdapter(it, recyclerView, restaurantType)
//                        }
                        Log.d("post", "onResponse 성공"+response.body())

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