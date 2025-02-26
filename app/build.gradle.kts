import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hilt.android)
    id("kotlin-parcelize")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.eatssu.android"
    compileSdk = 35

    // S8: API 28
    // S21: API 33
    defaultConfig {
        applicationId = "com.eatssu.android"
        minSdk = 23
        targetSdk = 35
        versionCode = 24
        versionName = "2.1.4"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
        compose = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    kotlin {
        jvmToolchain(17)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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
    kapt("com.google.dagger:hilt-android-compiler:2.42")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    // compose
    val composePlatform = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composePlatform)
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.0.0-rc01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.0.0-rc01")

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

    // glance for widget
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.appwidget.preview)
//    implementation(libs.androidx.glance.material3)
//    implementation(libs.androidx.glance.material2)

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

    // worker (Kotlin + coroutines)
    implementation(libs.androidx.work.runtime.ktx)

    //data store (with flow)
    implementation(libs.datastore)
}

kapt {
    correctErrorTypes = true
}
