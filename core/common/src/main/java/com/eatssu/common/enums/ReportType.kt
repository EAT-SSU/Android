package com.eatssu.common.enums

import androidx.annotation.StringRes
import com.eatssu.common.R
import com.eatssu.common.UiText

enum class ReportType(
    @StringRes val descriptionResId: Int
) {
    NO_ASSOCIATE_CONTENT(R.string.report_type_no_associate_content),
    IMPROPER_CONTENT(R.string.report_type_improper_content),
    IMPROPER_ADVERTISEMENT(R.string.report_type_improper_advertisement),
    COPY(R.string.report_type_copy),
    COPYRIGHT(R.string.report_type_copyright),
    EXTRA(R.string.report_type_extra);

    /** ViewModel에서 Context 없이 사용하기 위한 UiText 변환 */
    fun toUiText(): UiText = UiText.StringResource(descriptionResId)
}