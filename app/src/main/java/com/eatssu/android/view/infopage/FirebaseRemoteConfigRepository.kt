package com.eatssu.android.view.infopage

import android.util.Log
import com.eatssu.android.BuildConfig
import com.eatssu.android.data.entity.FirebaseInfoItem
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONArray

class FirebaseRemoteConfigRepository {
    private val instance = FirebaseRemoteConfig.getInstance()

    fun init() {
        // Set the default values locally
        instance.setDefaultsAsync(defaultValues)
        instance.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkForUpdate()

            } else {
                // Handle error
            }
        }
    }
    private fun checkForUpdate() {
        val forceUpdateRequired = getForceUpdate()
        val latestAppVersion = getAppVersion()

        val currentAppVersion = BuildConfig.VERSION_NAME

        if (forceUpdateRequired) {
            Log.d("remoteconfig", forceUpdateRequired.toString())
            Log.d("remoteconfig", latestAppVersion)
            Log.d("remoteconfig", currentAppVersion)
            // 강제 업데이트 다이얼로그를 띄우거나 업데이트 화면으로 이동
//            showForceUpdateDialog()
        }
    }

    fun getForceUpdate(): Boolean {
        return instance.getBoolean("force_update")
    }

    fun getAppVersion(): String {
        return instance.getString("app_version")
    }

    fun getCafeteriaInfo(): ArrayList<FirebaseInfoItem> {
        return parsingJson(instance.getString("cafeteria_info"))
    }

    companion object {
        // 기본값 설정 (강제 업데이트 여부와 버전 정보)
        val defaultValues: Map<String, Any> = mapOf(
            "force_update_required" to false,
            "latest_app_version" to "1.0.0",
            "cafeteria_info" to "[\n" +
                    "  {\n" +
                    "    \"name\": \"숭실도담\",\n" +
                    "    \"location\": \"신양관 2층\",\n" +
                    "    \"time\": \"11:20~14:00\\n17:00~18:30\",\n" +
                    "    \"etc\": \"대면 배식+웰빙코너\\n토요일 11:30~13:30\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"학생식당\",\n" +
                    "    \"location\": \"학생회관 3층\",\n" +
                    "    \"time\": \"11:20~16:00\\n(식사 14:00 마감)\",\n" +
                    "    \"etc\": \"3개 코너 운영\\n뚝배기찌개, 덮밥, 양식\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"스낵코너\",\n" +
                    "    \"location\": \"학생회관 3층\",\n" +
                    "    \"time\": \"11:00~16:00\",\n" +
                    "    \"etc\": \"분식류, 옛날도시락, 컵밥 등\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"FOOD COURT\",\n" +
                    "    \"location\": \"학생회관 2층\",\n" +
                    "    \"time\": \"11:30~16:00\",\n" +
                    "    \"etc\": \"아시안푸드, 돈까스, 샐러드, 국밥 등 + 카페\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"기숙사 식당\",\n" +
                    "    \"location\": \"레지던스홀 지하 1층\",\n" +
                    "    \"time\": \"08:00분~09:30\\n11:00~14:00\\n17:00~18:30\",\n" +
                    "    \"etc\": \"주말 조식은 운영되지 않습니다.\"\n" +
                    "  }\n" +
                    "]"
        )
    }

    fun parsingJson(json: String): ArrayList<FirebaseInfoItem> {
        val jsonArray = JSONArray(json)
        val list = ArrayList<FirebaseInfoItem>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)

            val name = jsonObject.optString("name", "")
            val location = jsonObject.optString("location", "")
            val time = jsonObject.optString("time", "")
            val etc = jsonObject.optString("etc", "")

            val firebaseInfoItem = FirebaseInfoItem(name, location, time, etc)
            Log.d("MainActivity", firebaseInfoItem.toString())
            list.add(firebaseInfoItem)
        }


        return list
    }
}