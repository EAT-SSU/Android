# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# https://developers.kakao.com/docs/latest/en/getting-started/sdk-android#configure-for-shrinking-and-obfuscation-(optional)
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# Retrofit2 인터페이스와 데이터 모델 클래스 보존
-keep interface com.eatssu.android.data.service.** { *; }
-keep class com.eatssu.android.data.dto.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-keepattributes Signature
-keepattributes Exceptions

# OkHttp 보존
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Hilt 의존성 주입 관련 클래스 보존
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.EntryPoint
-keep class * extends dagger.hilt.InstallIn
-keep class * extends dagger.hilt.components.SingletonComponent
#-keep class * extends dagger.hilt.components.ActivityComponent
-dontwarn dagger.hilt.**

# Firebase Analytics 관련 클래스 보존
-keep class com.google.firebase.analytics.** { *; }
#-keepclassmembers class * {
#    @com.google.firebase.analytics.FirebaseEvent *;
#}
-dontwarn com.google.firebase.analytics.**

# Gson 보존 (필드명 직렬화/역직렬화 보호)
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**

# Coroutines 관련 클래스 보존
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# AndroidX 관련 보존
-keep class androidx.** { *; }
-dontwarn androidx.**

# Retrofit의 어노테이션 보존
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# 일반적인 자바 어노테이션 보존
-keepattributes *Annotation*

# 모든 직렬화된 클래스 보존
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
