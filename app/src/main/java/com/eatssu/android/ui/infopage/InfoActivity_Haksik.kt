package com.eatssu.android.ui.infopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.response.InfoResponse
import com.eatssu.android.data.model.response.MenuBaseResponse
import com.eatssu.android.data.service.InfoService
import com.eatssu.android.data.service.RetrofitInterface
import com.eatssu.android.databinding.ActivityInfoHaksikBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoActivity_Haksik : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoHaksikBinding

    var location: String = ""
    var openHours: String = ""
    var pw: String = ""
    var pw2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoHaksikBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

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
        /*
        val infoService = RetrofitImpl.getApiClientWithOutToken().create(InfoService::class.java)
            infoService.getRestaurantInfo("Haksik").enqueue(object:
                Callback<InfoResponse> {
                override fun onResponse(
                    call: Call<InfoResponse>,
                    response: Response<InfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            //text_text.text = body.toString response 잘 받아왔는지 확인.
                            location = viewBinding.tvRestaurantLocation.toString()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<InfoResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })*/
    }
}
