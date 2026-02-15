package com.eatssu.android.screenshot.inventory

import java.io.File

object ScreenTargetScanner {
    fun scanAllTargets(): Set<String> {
        return buildSet {
            addAll(scanManifestActivities())
            addAll(scanConcreteFragments())
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

    fun scanConcreteFragments(): Set<String> {
        val kotlinFiles = presentationKotlinFiles()
        val packageRegex = Regex("""(?m)^\s*package\s+([\w.]+)""")
        val classRegex = Regex("""(?m)^\s*(?!abstract\b)class\s+(\w+)\s*:\s*([^\n{]+)""")

        return kotlinFiles
            .flatMap { file ->
                val source = file.readText()
                val packageName = packageRegex.find(source)?.groupValues?.get(1) ?: return@flatMap emptyList()
                classRegex.findAll(source)
                    .mapNotNull { match ->
                        val className = match.groupValues[1]
                        val superTypes = match.groupValues[2]
                        val isFragmentLike =
                            superTypes.contains("Fragment") || superTypes.contains("BottomSheetDialogFragment")
                        if (!isFragmentLike) {
                            null
                        } else {
                            "fragment:$packageName.$className"
                        }
                    }
                    .toList()
            }
            .toSet()
    }

    fun scanComposeRoutes(): Set<String> {
        val routeFiles = listOf(
            "src/main/java/com/eatssu/android/presentation/cafeteria/review/ReviewNav.kt",
            "src/main/java/com/eatssu/android/presentation/mypage/myreview/MyReviewNav.kt",
        )
        val routeObjects = setOf("ReviewNav", "MyReviewNav")
        val objectRegex = Regex(
            pattern = """object\s+(\w+)\s*\{(.*?)\}""",
            options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE),
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

    fun scanComposeScreens(): Set<String> {
        val functionRegex = Regex("""^\s*(?:internal\s+)?fun\s+([A-Za-z0-9_]+)\s*\(""")

        return presentationKotlinFiles()
            .flatMap { file ->
                val names = mutableSetOf<String>()
                var composableAhead = false

                file.forEachLine { line ->
                    val trimmed = line.trim()
                    if (trimmed.contains("@Composable")) {
                        composableAhead = true
                        return@forEachLine
                    }

                    if (!composableAhead) return@forEachLine

                    if (trimmed.startsWith("@")) return@forEachLine
                    if (trimmed.isBlank()) return@forEachLine

                    val name = functionRegex.find(trimmed)?.groupValues?.get(1)
                    if (name != null) {
                        if (name.endsWith("Screen") && !name.startsWith("Preview")) {
                            names.add("screen:$name")
                        }
                    }
                    composableAhead = false
                }

                names
            }
            .toSet()
    }

    private fun presentationKotlinFiles(): List<File> {
        val candidates = listOf(
            File("src/main/java/com/eatssu/android/presentation"),
            File("app/src/main/java/com/eatssu/android/presentation"),
            File("../app/src/main/java/com/eatssu/android/presentation"),
        )
        val root = candidates.firstOrNull { it.exists() }
            ?: error("Cannot find presentation source root for scanner.")

        return root.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()
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
