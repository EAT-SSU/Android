package com.eatssu.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.*
import com.eatssu.android.data.model.response.MenuBaseResponse
import com.eatssu.android.data.service.RetrofitInterface
import com.eatssu.android.databinding.FragmentBreakfastBinding
import com.eatssu.android.ui.calendar.CalendarData
import com.eatssu.android.ui.infopage.*
import com.eatssu.android.ui.menuadapter.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BreakfastFragment : Fragment() {
    private var _binding: FragmentBreakfastBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentBreakfastBinding.inflate(inflater, container, false)

        binding.btnHaksikInfo.setOnClickListener{
            val intent = Intent(context, InfoActivity_Haksik::class.java);
            startActivity(intent);
        }
        binding.btnDodamInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Dodam::class.java);
            startActivity(intent);
        }
        binding.btnGisikInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Gisik::class.java);
            startActivity(intent);
        }
        binding.btnKitchenInfo.setOnClickListener{
            val intent = Intent(context, InfoActivity_Kitchen::class.java);
            startActivity(intent);
        }
        binding.btnFoodInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Food::class.java);
            startActivity(intent);
        }
        binding.btnSnackInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity_Snack::class.java);
            startActivity(intent);
        }
        /*        // rv 각 식당과 연결
        val rv_haksik = binding.rvBreakfastHaksik
        val rv_dodam = binding.rvBreakfastDodam
        val rv_gisik = binding.rvBreakfastGisik
        val rv_kitchen = binding.rvBreakfastKitchen
        val rv_food = binding.rvBreakfastFood
        val rv_snack = binding.rvBreakfastSnack

        val itemListHaksik = ArrayList<Haksik>()
        val itemListDodam = ArrayList<Dodam>()
        val itemListGisik = ArrayList<Gisik>()
        val itemListKitchen = ArrayList<Kitchen>()
        val itemListFood = ArrayList<Food>()
        val itemListSnack = ArrayList<Snack>()

        itemListHaksik.add(Haksik("김치찌개", "5000", 5.0))
        //도담은 1코너, 2코너 각각에 여러 메뉴랑 반찬 있는데 코너로 나눌지, 메뉴 하나씩 띄울지 상의 필요..
        itemListDodam.add(Dodam("새우볶음밥", "5000", 5.0))
        itemListDodam.add(Dodam("간장마늘치킨", "5000", 5.0))

        itemListGisik.add(Gisik("까르보나라스파게티", "5000", 4.2))
        itemListGisik.add(Gisik("브로콜리함박스테이크", "5000", 4.2))

        itemListKitchen.add(Kitchen("콥샐러드", "7000", 3.5))
        itemListKitchen.add(Kitchen("탄단지샐러드", "7000", 3.7))

        itemListFood.add(Food("치킨마늘볶음밥", "6500", 3.9))
        itemListFood.add(Food("치즈찜닭", "10000", 3.5))

        itemListSnack.add(Snack("떡볶이", "4500", 4.8))
        itemListSnack.add(Snack("순대", "4000", 4.5))


        val haksikAdapter = HaksikAdapter(itemListHaksik)
        val dodamAdapter = DodamAdapter(itemListDodam)
        val gisikAdapter = GisikAdapter(itemListGisik)
        val kitchenAdapter = KitchenAdapter(itemListKitchen)
        val foodAdapter = FoodAdapter(itemListFood)
        val snackAdapter = SnackAdapter(itemListSnack)

        haksikAdapter.notifyDataSetChanged()
        dodamAdapter.notifyDataSetChanged()
        gisikAdapter.notifyDataSetChanged()
        kitchenAdapter.notifyDataSetChanged()
        foodAdapter.notifyDataSetChanged()
        snackAdapter.notifyDataSetChanged()

        rv_haksik.adapter = haksikAdapter
        rv_haksik.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv_dodam.adapter = dodamAdapter
        rv_dodam.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv_gisik.adapter = gisikAdapter
        rv_gisik.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv_kitchen.adapter = kitchenAdapter
        rv_kitchen.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv_food.adapter = foodAdapter
        rv_food.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv_snack.adapter = snackAdapter
        rv_snack.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        */
        /*
        fun setAdapter(itemListFood: List<MenuBaseResponse.Result>){
            val foodAdapter = FoodAdapter(itemListFood)
            var rv_restaurant_array: Array<String> = resources.getStringArray(R.array.rv_restaurant)
            binding.rvBreakfastFood.adapter=foodAdapter

            val linearLayoutManager = LinearLayoutManager(context)
            binding.rvBreakfastFood.layoutManager=linearLayoutManager

            binding.rvBreakfastFood.setHasFixedSize(true)
            binding.rvBreakfastFood.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

        val retrofitService = RetrofitImpl.getApiClient().create(RetrofitInterface::class.java)
        var restaurant_array: Array<String> = resources.getStringArray(R.array.restaurant)
        for (i in 0..5) {
        retrofitService.getMenuMorning(restaurant_array[i], "20230406").enqueue(object:Callback<MenuBaseResponse> {
            override fun onResponse(
                call: Call<MenuBaseResponse>,
                response: Response<MenuBaseResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        //text_text.text = body.toString response 잘 받아왔는지 확인.
                        setAdapter(it.menuInforesult)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<MenuBaseResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })}*/
        return binding.root
    }
}