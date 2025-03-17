import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)

    id("kotlin-parcelize")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.eatssu.android"
    compileSdk = 34

    // S8: API 28
    // S21: API 33
    defaultConfig {
        applicationId = "com.eatssu.android"
        minSdk = 23
        targetSdk = 34
        versionCode = 25
        versionName = "2.1.5"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val baseUrl: String = p.getProperty("PROD_BASE_URL")
            buildConfigField("String", "BASE_URL", baseUrl)

            val kakaoKey: String = p.getProperty("KAKAO_NATIVE_APP_KEY")
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey

            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            var shrinkResources = false
            var minifyEnabled = false
        }

        debug {
//            applicationIdSuffix = ".debug"
//            isDebuggable = false

            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val baseUrl: String = p.getProperty("DEV_BASE_URL")
            buildConfigField("String", "BASE_URL", baseUrl)

            val kakaoKey: String = p.getProperty("KAKAO_NATIVE_APP_KEY")
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.threetenabp)
    implementation(libs.material.calendarview)
    implementation(libs.recyclerview)
    implementation(libs.transport.runtime)
    implementation(libs.activity)
    implementation(libs.fragment)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.compiler:compiler:1.5.4")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //retrofit2: 서버통신
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Gson for JSON parsing
    implementation(libs.gson)

    //OkHttp: 통신 로그 확인하기 위함
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    //glide: 사진 업로드
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    //compressor: 이미지 압축
    implementation(libs.compressor)

    // Coroutines for concurrency
    implementation(libs.coroutines)
    implementation(libs.coroutines.core)
    implementation(libs.lifecycle.runtime)

    // Kakao login SDK
    implementation(libs.kakao.login)

    // Hilt for Dependency Injection
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // ViewModel and LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    implementation(libs.play.services.base)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Timber for logging
    implementation(libs.timber)

    // OSS
    implementation(libs.oss.licenses)

}

kapt {
    correctErrorTypes = true
}
