package com.eatssu.android.data.enums

import com.eatssu.android.R

enum class ReportType(val type: String, val defaultContent: Int?) {
    CONTENT("CONTENT", R.string.report1),
    BAD_WORD("BAD_WORD", R.string.report2),
    AD("AD", R.string.report3),
    COPY("COPY", R.string.report4),
    COPYRIGHT("COPYRIGHT", R.string.report5),
    ETC("ETC", null)
}