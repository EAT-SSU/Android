package com.eatssu.android.screenshot.core

enum class RoborazziMode {
    RECORD,
    VERIFY,
    COMPARE,
    NONE;

    companion object {
        fun fromSystemProperties(): RoborazziMode {
            val record = System.getProperty("roborazzi.test.record") == "true"
            val verify = System.getProperty("roborazzi.test.verify") == "true"
            val compare = System.getProperty("roborazzi.test.compare") == "true"
            return when {
                record -> RECORD
                verify -> VERIFY
                compare -> COMPARE
                else -> NONE
            }
        }
    }
}
