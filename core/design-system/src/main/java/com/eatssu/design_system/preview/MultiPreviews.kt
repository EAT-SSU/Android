package com.eatssu.design_system.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Custom MultiPreview annotations for EAT-SSU design system.
 *
 * Usage:
 * - @ThemePreviews: Light + Dark theme variants
 * - @DevicePreviews: Phone, Foldable, Tablet
 * - @FontScalePreviews: Normal, Large, Extra-large text
 * - @CompletePreviews: All of the above combined
 */

// ──────────────────────────────────────────────
// Theme Previews (Light / Dark)
// ──────────────────────────────────────────────

@Preview(
    name = "Light",
    showBackground = true,
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    backgroundColor = 0xFF1C1B1F,
)
annotation class ThemePreviews

// ──────────────────────────────────────────────
// Device Previews (Phone / Foldable / Tablet)
// ──────────────────────────────────────────────

@Preview(
    name = "Phone",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
)
@Preview(
    name = "Foldable",
    showBackground = true,
    device = "spec:width=673dp,height=841dp,dpi=420",
)
@Preview(
    name = "Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
annotation class DevicePreviews

// ──────────────────────────────────────────────
// Font Scale Previews (Normal / Large / Extra-large)
// ──────────────────────────────────────────────

@Preview(
    name = "Font 1x",
    showBackground = true,
    fontScale = 1.0f,
)
@Preview(
    name = "Font 1.5x",
    showBackground = true,
    fontScale = 1.5f,
)
@Preview(
    name = "Font 2x",
    showBackground = true,
    fontScale = 2.0f,
)
annotation class FontScalePreviews

// ──────────────────────────────────────────────
// Complete Previews (Theme + Device + Font Scale)
// ──────────────────────────────────────────────

@ThemePreviews
@DevicePreviews
@FontScalePreviews
annotation class CompletePreviews
