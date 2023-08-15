package com.eatssu.android.view.infopage

import RetrofitImpl
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.response.InfoResponseDto
import com.eatssu.android.data.service.InfoService
import com.eatssu.android.databinding.ActivityInfoGisikBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class InfoActivity_Gisik : AppCompatActivity(){
    private lateinit var viewBinding: ActivityInfoGisikBinding
    lateinit var retrofit: Retrofit
    private lateinit var infoService: InfoService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoGisikBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        infoService = RetrofitImpl.retrofit.create(InfoService::class.java)

        val mapview = MapView(this);
        val mapViewContainer = viewBinding.btnMap
        mapViewContainer.addView(mapview)

        // 중심점 변경 - 레지던스홀
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.495488,126.959173), true);

        val marker = MapPOIItem()
        marker.apply {
            itemName = "레지던스홀"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.495488,126.959173)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)

        getRestaurantInfo(Restaurant.DOMITORY)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRestaurantInfo(restaurantType: Restaurant) {

        infoService.getRestaurantInfo(restaurantType.toString())
            .enqueue(object : Callback<InfoResponseDto> {
                override fun onResponse(
                    call: Call<InfoResponseDto>,
                    response: Response<InfoResponseDto>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            viewBinding.tvGisikLoca.text = it.location

                            viewBinding.weekTvDodam.text = it.openHours[0].dayType
                            viewBinding.lunchTv.text = it.openHours[0].timepart
                            viewBinding.lunchTv2.text = it.openHours[0].time
                            viewBinding.tvGisikTime.text = it.openHours[1].timepart
                            viewBinding.tvGisikTime2.text = it.openHours[1].time
                            viewBinding.tvSpace.text = it.openHours[2].timepart
                            viewBinding.tvSpace2.text = it.openHours[2].time
                            viewBinding.weekendTvGisik.text = it.openHours[3].dayType
                            viewBinding.tvWeekendGisik.text = it.openHours[3].timepart
                            viewBinding.tvWeekendGisik2.text = it.openHours[3].time
                            viewBinding.tvSpaceGisik.text = it.openHours[4].timepart
                            viewBinding.tvSpace2Gisik.text = it.openHours[4].time
                        }
                        Log.d("response", "Response code : " + response.code())
                    } else {
                        Log.d("post", "onResponse 실패" + response.code())
                    }
                }

                override fun onFailure(call: Call<InfoResponseDto>, t: Throwable) {
                    Log.d("post", "onFailure 에러: ${t.message}")
                }

            })
    }

    private fun getNonFixed() {
    }

    override fun finish() {
        super.finish()
    }


}
