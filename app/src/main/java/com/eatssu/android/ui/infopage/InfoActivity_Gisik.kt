package com.eatssu.android.ui.infopage

import RetrofitImpl
import android.R
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.data.enums.RestaurantType
import com.eatssu.android.data.model.response.InfoResponse
import com.eatssu.android.data.service.InfoService
import com.eatssu.android.databinding.ActivityInfoGisikBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class InfoActivity_Gisik : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityInfoGisikBinding
    lateinit var retrofit: Retrofit
    private lateinit var infoService: InfoService
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoGisikBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        this.mapView = viewBinding.btnMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@InfoActivity_Gisik)

        infoService = RetrofitImpl.retrofit.create(InfoService::class.java)

        getRestaurantInfo(RestaurantType.DOMITORY)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // 중심점 변경 - 레지던스홀
        currentMarker = setupMarker(LatLngEntity(37.495488,126.959173))
        currentMarker?.showInfoWindow()

        /*val marker = MapPOIItem()
        marker.apply {
            itemName = "레지던스홀"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.495488,126.959173)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)*/
    }

    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title("위치")
            snippet("레지던스홀 위치")
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // 지도 유형 설정
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))  // 줌의 정도 - 1 일 경우 세계지도 수준, 숫자가 커질 수록 상세지도가 표시됨
        return googleMap.addMarker(markerOption)

    }

    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )

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

                override fun onFailure(call: Call<InfoResponse>, t: Throwable) {
                    Log.d("post", "onFailure 에러: ${t.message}")
                }

            })
    }

    private fun getNonFixed() {
    }

    override fun onDestroy() {
        googleMap.clear()
        super.onDestroy()
    }


}
