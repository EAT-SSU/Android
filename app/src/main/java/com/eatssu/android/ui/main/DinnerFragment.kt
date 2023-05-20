package com.eatssu.android

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
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentBreakfastBinding
import com.eatssu.android.databinding.FragmentLunchBinding
import com.eatssu.android.ui.infopage.*
import com.eatssu.android.ui.main.MenuAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DinnerFragment : Fragment() {
    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLunchBinding.inflate(inflater, container, false)

        binding.btnHaksikInfo.setOnClickListener {
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
        binding.btnKitchenInfo.setOnClickListener {
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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lodeData()
    }


    private fun setAdapter(menuList: List<GetMenuInfoListResponse.MenuInfo>) {
        val menuAdapter = MenuAdapter(menuList)
        binding.rvLunchFood.adapter = menuAdapter

        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvLunchFood.layoutManager = linearLayoutManager

        binding.rvLunchFood.setHasFixedSize(true)
        binding.rvLunchFood.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
    }

    private fun lodeData() {

        val restaurantType: RestaurantType = RestaurantType.FOOD_COURT


        val menuService = RetrofitImpl.getApiClient().create(MenuService::class.java)
        menuService.getFixedMenu(restaurantType.toString()).enqueue(object :
            Callback<GetMenuInfoListResponse> {
            override fun onResponse(
                call: Call<GetMenuInfoListResponse>,
                response: Response<GetMenuInfoListResponse>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString());
                    //Toast.makeText(this@ProfileActivity, "비밀번호 찾기 성공!", Toast.LENGTH_SHORT).show()

                    val body = response.body()
                    body?.let {
                        setAdapter(it.menuInfoList)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<GetMenuInfoListResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}