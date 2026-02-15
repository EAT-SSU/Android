package com.eatssu.android.screenshot.inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenCoverageGuardTest {
    @Test
    fun `manifest fragment route targets are fully covered or excluded`() {
        val scannedTargets = ScreenTargetScanner.scanAllTargets()
        val coveredTargets = ScreenCoverageRegistry.coveredTargetIds
            .filterNot { it.startsWith("screen:") }
            .toSet()
        val excludedTargets = ScreenCoverageRegistry.excludedTargets.keys

        val missingTargets = scannedTargets - coveredTargets - excludedTargets
        val staleTargets = coveredTargets - scannedTargets

        assertTrue(
            buildString {
                appendLine("Missing coverage targets: $missingTargets")
                appendLine("Covered targets: $coveredTargets")
                appendLine("Scanned targets: $scannedTargets")
                appendLine("Excluded targets: ${ScreenCoverageRegistry.excludedTargets}")
            },
            missingTargets.isEmpty(),
        )

        assertTrue(
            "Stale coverage targets: $staleTargets",
            staleTargets.isEmpty(),
        )
    }

    @Test
    fun `compose screens are curated and fully covered`() {
        val scannedComposeScreens = ScreenTargetScanner.scanComposeScreens()
        val curatedComposeScreens = ScreenCoverageRegistry.curatedComposeScreenTargets
        val coveredComposeScreens = ScreenCoverageRegistry.coveredComposeScreenTargetIds
        val excludedComposeScreens = ScreenCoverageRegistry.excludedComposeScreenTargets

        val missingComposeCoverage = curatedComposeScreens - coveredComposeScreens
        val staleComposeCoverage = coveredComposeScreens - curatedComposeScreens
        val uncategorizedComposeScreens =
            scannedComposeScreens - curatedComposeScreens - excludedComposeScreens
        val staleCuratedComposeScreens = curatedComposeScreens - scannedComposeScreens

        assertTrue(
            "Missing compose screen coverage: $missingComposeCoverage",
            missingComposeCoverage.isEmpty(),
        )
        assertTrue(
            "Stale compose screen coverage: $staleComposeCoverage",
            staleComposeCoverage.isEmpty(),
        )
        assertTrue(
            "Uncategorized compose screens: $uncategorizedComposeScreens",
            uncategorizedComposeScreens.isEmpty(),
        )
        assertTrue(
            "Curated compose screens not found in source: $staleCuratedComposeScreens",
            staleCuratedComposeScreens.isEmpty(),
        )
    }

    @Test
    fun `all coverage items declare at least one state`() {
        val invalidItems = ScreenCoverageRegistry.coverageItems
            .filter { it.states.isEmpty() }
            .map { it.targetId }

        assertTrue("Coverage items without states: $invalidItems", invalidItems.isEmpty())
    }
}
