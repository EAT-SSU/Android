// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    dependencies {
        classpath(libs.oss.licenses.plugin)
    }
}

tasks.register<Exec>("generateLocalizedStrings") {
    group = "localization"
    description =
        "language.csv 파일을 통해 res/values-* 위치에 양식에 맞게 strings.xml 파일을 생성합니다. csv에 기존 strings.xml에 매칭되는 값이 없는 경우 한글 값이 들어갑니다."
    commandLine("python3", "scripts/generate_android_strings.py")
}
