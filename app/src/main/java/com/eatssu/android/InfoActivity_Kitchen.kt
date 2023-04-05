package com.eatssu.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityInfoFoodBinding
import com.eatssu.android.databinding.ActivityInfoKitchenBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class InfoActivity_Kitchen : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoKitchenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoKitchenBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mapview = MapView(this);
        val mapViewContainer = viewBinding.btnMap
        mapViewContainer.addView(mapview)

        // 중심점 변경 - 전산관
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.495488,126.959173), true);
        //mapview.zoomIn(true)

        val marker = MapPOIItem()
        marker.apply {
            itemName = "전산관"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.495488,126.959173)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)
    }
}