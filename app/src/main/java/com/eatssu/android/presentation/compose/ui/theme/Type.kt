package com.aurora.carevision.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.eatssu.android.R

// 폰트 패밀리 설정
val pretendardBold = FontFamily(Font(R.font.pretendard_bold, FontWeight.Bold))
val pretendardMedium = FontFamily(Font(R.font.pretendard_medium, FontWeight.Medium))
val pretendardSemiBold = FontFamily(Font(R.font.pretendard_semibold, FontWeight.SemiBold))
val pretendardRegular = FontFamily(Font(R.font.pretendard_regular, FontWeight.Normal))

@Stable
class EatssuTypography internal constructor(
    val button1: TextStyle,
    val button2: TextStyle,
    val headingPrimary: TextStyle,
    val headingSecondary: TextStyle,
    val subtitle1: TextStyle,
    val subtitle2: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle,
) {
    fun copy(): EatssuTypography = EatssuTypography(
        button1, button2, headingPrimary, headingSecondary, subtitle1, subtitle2, body1, body2, body3, caption1, caption2
    )
}

fun EatssuTextStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle = TextStyle(
    fontFamily = fontFamily,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

@Composable
fun careVisionTypography(): EatssuTypography {
    return EatssuTypography(
        button1 = EatssuTextStyle(
            fontFamily = pretendardBold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        button2 = EatssuTextStyle(
            fontFamily = pretendardBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        headingPrimary = EatssuTextStyle(
            fontFamily = pretendardBold,
            fontSize = 20.sp,
            lineHeight = 30.sp
        ),
        headingSecondary = EatssuTextStyle(
            fontFamily = pretendardBold,
            fontSize = 18.sp,
            lineHeight = 28.sp
        ),
        subtitle1 = EatssuTextStyle(
            fontFamily = pretendardSemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        subtitle2 = EatssuTextStyle(
            fontFamily = pretendardSemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        body1 = EatssuTextStyle(
            fontFamily = pretendardRegular,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        body2 = EatssuTextStyle(
            fontFamily = pretendardRegular,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        body3 = EatssuTextStyle(
            fontFamily = pretendardRegular,
            fontSize = 14.sp,
            lineHeight = 18.sp
        ),
        caption1 = EatssuTextStyle(
            fontFamily = pretendardBold,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        caption2 = EatssuTextStyle(
            fontFamily = pretendardMedium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    )
}
