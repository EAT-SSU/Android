import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.eatssu.android"
    compileSdk = 35

    /**
     * 현재 팀 내 안드로이드 OS 버전
     * 진 S8: 9 (sdk 28)
     * 진 S21: 14 (sdk 33)
     * 윤소: 9
     * 유리: 10
     * 제훈: 14
     */

    defaultConfig {
        applicationId = "com.eatssu.android"
        minSdk = 28
        targetSdk = 35
        versionCode = 51
        versionName = "3.2.1"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
        compose = true
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val baseUrl: String = p.getProperty("PROD_BASE_URL")
            buildConfigField("String", "BASE_URL", baseUrl)

            val kakaoKey: String = p.getProperty("KAKAO_NATIVE_APP_KEY")
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey

            val naverMapsClientID: String = p.getProperty("NAVER_MAPS_CLIENT_ID")
            buildConfigField("String", "NAVER_MAPS_CLIENT_ID", "\"$naverMapsClientID\"")
            manifestPlaceholders["NAVER_MAPS_CLIENT_ID"] = naverMapsClientID

            val postHogApiKey: String = p.getProperty("POSTHOG_API_KEY")
            buildConfigField("String", "POSTHOG_API_KEY", "\"$postHogApiKey\"")

            val postHogHost: String = p.getProperty("POSTHOG_HOST")
            buildConfigField("String", "POSTHOG_HOST", "\"$postHogHost\"")

            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
//            isDebuggable = false

            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val baseUrl: String = p.getProperty("DEV_BASE_URL")
            buildConfigField("String", "BASE_URL", baseUrl)

            val kakaoKey: String = p.getProperty("KAKAO_NATIVE_APP_KEY")
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey

            val naverMapsClientID: String = p.getProperty("NAVER_MAPS_CLIENT_ID")
            buildConfigField("String", "NAVER_MAPS_CLIENT_ID", "\"$naverMapsClientID\"")
            manifestPlaceholders["NAVER_MAPS_CLIENT_ID"] = naverMapsClientID

            val postHogApiKey: String = p.getProperty("POSTHOG_API_KEY")
            buildConfigField("String", "POSTHOG_API_KEY", "\"$postHogApiKey\"")

            val postHogHost: String = p.getProperty("POSTHOG_HOST")
            buildConfigField("String", "POSTHOG_HOST", "\"$postHogHost\"")

            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    splits {
        abi {
            isEnable = true
            reset()
            isUniversalApk = true
        }
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.threetenabp)
    implementation(libs.material.calendarview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.transport.runtime)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.lifecycle.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.theme.adapter)
    implementation(libs.accompanist.appcompat.theme)
    androidTestImplementation(libs.androidx.compose.bom)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.compose)

    //glance
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    debugImplementation(libs.androidx.glance.appwidget.preview) // 프리뷰 지원

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //retrofit2: 서버통신
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Gson for JSON parsing
    implementation(libs.gson)

    //OkHttp: 통신 로그 확인하기 위함
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    //glide: 사진 업로드
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    //coil: 이미지 로딩
    implementation(libs.coil.compose)

    //compressor: 이미지 압축
    implementation(libs.compressor)

    // Coroutines for concurrency
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Kakao SDK
    implementation(libs.kakao.login)
    implementation(libs.kakao.talk)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    implementation(libs.hilt.navigation.compose)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.play.services.base)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // Timber for logging
    implementation(libs.timber)

    // OSS
    implementation(libs.oss.licenses)

    // worker (Kotlin + coroutines)
    implementation(libs.androidx.work.runtime.ktx)

    //data store (with flow)
    implementation(libs.androidx.datastore.preferences)

    // EncryptedSharedPreferences
    implementation(libs.androidx.security.crypto)

    // naver maps
    implementation (libs.map.sdk)

    // naver maps to compose
    implementation(libs.naver.map.compose)
    implementation(libs.naver.map.location)

    // 현재 위치 정보
    implementation(libs.play.services.location)

    // PostHog
    implementation(libs.posthog.android)

}