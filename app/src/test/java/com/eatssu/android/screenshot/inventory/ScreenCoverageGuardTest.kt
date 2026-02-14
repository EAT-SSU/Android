package com.eatssu.android.screenshot.inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenCoverageGuardTest {
    @Test
    fun `manifest, navigation, route targets are fully covered or excluded`() {
        val scannedTargets = ScreenTargetScanner.scanAllTargets()
        val coveredTargets = ScreenCoverageRegistry.coveredTargetIds
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
            missingTargets.isEmpty()
        )

        assertTrue(
            "Registry has stale targets not found in scanner: $staleTargets",
            staleTargets.isEmpty()
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
