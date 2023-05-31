package com.eatssu.android.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.adapter.DodamAdapter
import com.eatssu.android.adapter.GisikAdapter
import com.eatssu.android.data.enums.RestaurantType
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentDinnerBinding
import com.eatssu.android.ui.infopage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
        binding.btnDodamInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Dodam::class.java))
        }
        binding.btnGisikInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity_Gisik::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun lodeData() {
        getNonFixed(RestaurantType.DOMITORY, binding.rvDinnerGisik)
        getNonFixed(RestaurantType.DODAM, binding.rvDinnerDodam)
    }

    private fun setAdapter(
        menuList: List<GetMenuInfoListResponse>,
        recyclerView: RecyclerView,
        restaurantType: RestaurantType
    ) {
        val gisikAdapter = GisikAdapter(menuList)
        val dodamAdapter = DodamAdapter(menuList)

        val adapter = when (restaurantType) {

            RestaurantType.DOMITORY -> gisikAdapter
            RestaurantType.DODAM -> dodamAdapter

            else -> {
                gisikAdapter    //그냥
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNonFixed(restaurantType: RestaurantType, recyclerView: RecyclerView) {

        val monthFormat =
            DateTimeFormatter.ofPattern("yyyyMMdd").withLocale(Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)

        menuService.getDinnerMenu(localDate, restaurantType.toString())
            .enqueue(object : Callback<GetMenuInfoListResponse> {
                override fun onResponse(
                    call: Call<GetMenuInfoListResponse>,
                    response: Response<GetMenuInfoListResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            setAdapter(listOf(it), recyclerView, restaurantType)
                        }
                    } else {
                        Log.d("post", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<GetMenuInfoListResponse>, t: Throwable) {
                    Log.d("post", "onFailure 에러: 나다${t.message}+ ${call}")
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}