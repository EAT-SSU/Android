package com.eatssu.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityInfoGisikBinding
import com.eatssu.android.databinding.ActivityInfoSnackBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class InfoActivity_Gisik : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoGisikBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoGisikBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mapview = MapView(this);
        val mapViewContainer = viewBinding.btnMap
        mapViewContainer.addView(mapview)

        // 중심점 변경 - 레지던스홀
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.495488,126.959173), true);
        //mapview.zoomIn(true)

        val marker = MapPOIItem()
        marker.apply {
            itemName = "레지던스홀"   // 마커 이름
            mapPoint = MapPoint.mapPointWithGeoCoord(37.495488,126.959173)   // 좌표
            markerType = MapPOIItem.MarkerType.RedPin
            isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
            setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
        }
        mapview.addPOIItem(marker)
    }
}
