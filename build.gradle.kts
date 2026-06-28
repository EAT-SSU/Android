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

tasks.register("generateLocalizedStrings") {
    group = "localization"
    description =
        "app/language.csv 파일을 통해 지정된 모듈들의 res/values-* 위치에 strings.xml 파일을 생성합니다."

    doLast {
        val targets = listOf(
            Triple(
                project.file("app/src/main/res/values/strings.xml").absolutePath,
                project.file("app/src/main/res").absolutePath,
                project.file("language.csv").absolutePath
            )
            /* Core/Common localization is disabled for now until etc.csv is ready.
            Triple(
                project.file("core/common/src/main/res/values/strings.xml").absolutePath,
                project.file("core/common/src/main/res").absolutePath,
                project.file("etc.csv").absolutePath
            )
            */
        )

        targets.forEach { (source, resDir, csvPath) ->
            val csvFile = project.file(csvPath)
            if (!csvFile.exists()) {
                println("Warning: CSV file not found at ${csvFile.absolutePath}. Skipping localization for $source.")
                return@forEach
            }

            println("Generating localized strings for: $source -> $resDir using CSV: $csvPath")
            exec {
                commandLine(
                    "python3",
                    "scripts/generate_android_strings.py",
                    csvPath,
                    "--source",
                    source,
                    "--res-dir",
                    resDir
                )
            }
        }
    }
}


