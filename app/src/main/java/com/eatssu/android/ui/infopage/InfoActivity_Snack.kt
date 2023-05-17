package com.eatssu.android.ui.infopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eatssu.android.databinding.ActivityInfoSnackBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class InfoActivity_Snack : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInfoSnackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoSnackBinding.inflate(layoutInflater)
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
    }
}