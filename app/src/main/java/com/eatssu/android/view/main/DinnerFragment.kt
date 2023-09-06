package com.eatssu.android.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import com.eatssu.android.adapter.DodamAdapter
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentDinnerBinding
import com.eatssu.android.view.infopage.*
import retrofit2.Retrofit

class DinnerFragment : Fragment() {
    private var _binding: FragmentDinnerBinding? = null
    private val binding get() = _binding!!

    lateinit var retrofit: Retrofit
    lateinit var menuService: MenuService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDinnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        lodeData()
    }

    fun init() {
        menuService = retrofit.create(MenuService::class.java)
        setupClickListeners()

    }

    private fun setupClickListeners() {
//        binding.btnHaksikInfo.setOnClickListener {
//            startActivity(Intent(context, InfoActivity_Haksik::class.java))
//        }
        binding.btnDodamInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Dodam::class.java))
        }
        binding.btnGisikInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Gisik::class.java))
        }
//        binding.btnKitchenInfo.setOnClickListener {
//            startActivity(Intent(context, InfoActivity_Kitchen::class.java))
//        }
//        binding.btnFoodInfo.setOnClickListener {
//            startActivity(Intent(context, InfoActivity_Food::class.java))
//        }
//        binding.btnSnackInfo.setOnClickListener {
//            startActivity(Intent(context, InfoActivity_Snack::class.java))
//        }
    }

    private fun lodeData() {
        getNonFixed()
    }

//    private fun setAdapter(
//        menuList: List<GetMenuInfoListResponse.MenuInfo>,
//        recyclerView: RecyclerView,
//        restaurantType: RestaurantType
//    ) {
////        val foodAdapter =  FoodAdapter(menuList)
////        val snackAdapter = SnackAdapter(menuList)
////        val kitchenAdapter = KitchenAdapter(menuList)
//        val dodamAdapter = DodamAdapter(menuList)
//        val gisikAdapter = GisikAdapter(menuList)
//
//        val adapter = when (restaurantType) {
////            RestaurantType.SNACK_CORNER -> snackAdapter
////            RestaurantType.THE_KITCHEN -> kitchenAdapter
////            RestaurantType.FOOD_COURT -> foodAdapter
//            RestaurantType.DODAM -> dodamAdapter
//            RestaurantType.DOMITORY -> gisikAdapter
//
//            else -> {
//                dodamAdapter // 그냥
//            }
//        }
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.setHasFixedSize(true)
//    }
//
//    private fun getFixedMenu(restaurantType: RestaurantType, recyclerView: RecyclerView) {
//
//        menuService.getFixedMenu(restaurantType.toString())
//            .enqueue(object : Callback<GetMenuInfoListResponse> {
//                override fun onResponse(
//                    call: Call<GetMenuInfoListResponse>,
//                    response: Response<GetMenuInfoListResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val body = response.body()
//                        body?.let {
////                            setAdapter(it.menuInfoList, recyclerView, restaurantType)
//                        }
//                    } else {
//                        Log.d("post", "onResponse 실패")
//                    }
//                }
//
//                override fun onFailure(call: Call<GetMenuInfoListResponse>, t: Throwable) {
//                    Log.d("post", "onFailure 에러: ${t.message}")
//                }
//            })
//    }

    private fun getNonFixed() {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}