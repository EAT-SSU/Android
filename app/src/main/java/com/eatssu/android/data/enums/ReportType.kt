package com.eatssu.android.data.enums

import com.eatssu.android.R

enum class ReportType(val description: Int) {
    NO_ASSOCIATE_CONTENT(R.string.report1),
    IMPROPER_CONTENT(R.string.report2),
    IMPROPER_ADVERTISEMENT(R.string.report3),
    COPY(R.string.report4),
    COPYRIGHT(R.string.report5),
    EXTRA(R.string.report6)
}