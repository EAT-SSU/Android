package com.eatssu.android.screenshot.inventory

import java.io.File

object ScreenTargetScanner {
    fun scanAllTargets(): Set<String> {
        return buildSet {
            addAll(scanManifestActivities())
            addAll(scanNavigationFragments())
            addAll(scanViewPagerFragments())
            addAll(scanComposeRoutes())
        }
    }

    fun scanManifestActivities(): Set<String> {
        val manifest = readProjectFile("src/main/AndroidManifest.xml")
        val regex = Regex("""android:name="([^"]+Activity)"""")
        return regex.findAll(manifest)
            .map { "activity:${it.groupValues[1]}" }
            .toSet()
    }

    fun scanNavigationFragments(): Set<String> {
        val navigation = readProjectFile("src/main/res/navigation/eatssu_navigation.xml")
        val regex = Regex("""android:name="([^"]+Fragment)"""")
        return regex.findAll(navigation)
            .map { "fragment:${it.groupValues[1]}" }
            .toSet()
    }

    fun scanComposeRoutes(): Set<String> {
        val routeFiles = listOf(
            "src/main/java/com/eatssu/android/presentation/cafeteria/review/ReviewNav.kt",
            "src/main/java/com/eatssu/android/presentation/mypage/myreview/MyReviewNav.kt"
        )
        val routeObjects = setOf("ReviewNav", "MyReviewNav")
        val objectRegex = Regex(
            pattern = """object\s+(\w+)\s*\{(.*?)\}""",
            options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
        )
        val constRegex = Regex("""const\s+val\s+(\w+)\s*=\s*"[^"]+"""")

        return buildSet {
            routeFiles.forEach { relativePath ->
                val text = readProjectFile(relativePath)
                objectRegex.findAll(text).forEach { objectMatch ->
                    val objectName = objectMatch.groupValues[1]
                    if (objectName !in routeObjects) return@forEach
                    val objectBody = objectMatch.groupValues[2]
                    constRegex.findAll(objectBody).forEach { constMatch ->
                        add("route:$objectName.${constMatch.groupValues[1]}")
                    }
                }
            }
        }
    }

    fun scanViewPagerFragments(): Set<String> {
        val adapterPath =
            "src/main/java/com/eatssu/android/presentation/cafeteria/CafeteriaViewPagerAdapter.kt"
        val adapterSource = readProjectFile(adapterPath)
        if (!adapterSource.contains("MenuFragment")) return emptySet()

        // MenuFragment is rendered in Cafeteria ViewPager tabs and not declared in navigation XML.
        return setOf("fragment:com.eatssu.android.presentation.cafeteria.menu.MenuFragment")
    }

    private fun readProjectFile(moduleRelativePath: String): String {
        val candidates = listOf(
            File(moduleRelativePath),
            File("app/$moduleRelativePath"),
            File("../app/$moduleRelativePath"),
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("Cannot find source file for scanner: $moduleRelativePath")
        return file.readText()
    }
}
