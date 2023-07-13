package com.eatssu.android.ui.infopage

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.eatssu.android.data.enums.RestaurantType
import com.eatssu.android.data.model.response.InfoResponse
import com.eatssu.android.data.service.InfoService
import com.eatssu.android.databinding.ActivityInfoFoodBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class InfoActivity_Food : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoFoodBinding
    lateinit var retrofit: Retrofit
    private lateinit var infoService: InfoService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoFoodBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        infoService = RetrofitImpl.retrofit.create(InfoService::class.java)

        val mapview = MapView(this);
        val mapViewContainer = viewBinding.btnMap
        mapViewContainer.addView(mapview)

        // 중심점 변경 - 학생회관
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.497103,126.956785), true);
        //mapview.zoomIn(true)

        val marker = MapPOIItem()
        marker.apply {
            itemName = "학생회관"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.497103,126.956785)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)
        getRestaurantInfo(RestaurantType.FOOD_COURT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRestaurantInfo(restaurantType: RestaurantType) {

        infoService.getRestaurantInfo(restaurantType.toString())
            .enqueue(object : Callback<InfoResponse> {
                override fun onResponse(
                    call: Call<InfoResponse>,
                    response: Response<InfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            //putInfoResponse(it.openHours)
                            viewBinding.tvFoodcourtLocation.text = it.location

                            viewBinding.tvFoodcourtWeek.text = it.openHours[0].dayType
                            viewBinding.tvLunchFoodcourt.text = it.openHours[0].timepart
                            viewBinding.tvTimeFoodcourt.text = it.openHours[0].time
                        }
                        Log.d("response", "Response code : " + response.code())
                    } else {
                        Log.d("post", "onResponse 실패" + response.code())
                    }
                }

                override fun onFailure(call: Call<InfoResponse>, t: Throwable) {
                    Log.d("post", "onFailure 에러: ${t.message}")
                }


            })
    }

    private fun getNonFixed() {
    }
}
