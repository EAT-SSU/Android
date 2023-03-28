package com.eatssu.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityInfoDodamBinding
import com.eatssu.android.databinding.ActivityInfoHaksikBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class InfoActivity_Dodam : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoDodamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoDodamBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mapview = MapView(this);
        val mapViewContainer = viewBinding.btnMap
        mapViewContainer.addView(mapview)

        // 중심점 변경 - 신양관
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.496016,126.958154), true);
        //mapview.zoomIn(true)

        val marker = MapPOIItem()
        marker.apply {
            itemName = "신양관"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.496016,126.958154)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)
    }
}
