package com.eatssu.common.enums

import com.eatssu.common.R
import com.eatssu.common.UiText
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class EnumsBehaviorSpec : BehaviorSpec({

    given("AppLanguage") {
        `when`("코드가 매칭되면") {
            then("해당 언어를 반환한다") {
                AppLanguage.fromCode("ko") shouldBe AppLanguage.KOREAN
            }
        }

        `when`("코드가 매칭되지 않으면") {
            then("KOREAN을 기본값으로 반환한다") {
                AppLanguage.fromCode("en") shouldBe AppLanguage.KOREAN
            }
        }

        `when`("toLocale을 호출하면") {
            then("코드 기반 Locale을 반환한다") {
                AppLanguage.KOREAN.toLocale() shouldBe Locale("ko")
            }
        }
    }

    given("Time") {
        `when`("enum name이 유효하면") {
            then("한국어 식사명을 반환한다") {
                Time.entries.find { it.name == "LUNCH" }?.korean shouldBe "중식"
            }
        }

        `when`("enum name이 유효하지 않으면") {
            then("null을 반환한다") {
                Time.entries.find { it.name == "INVALID" }?.korean shouldBe null
            }
        }
    }

    given("Restaurant") {
        `when`("getVariableRestaurantList를 호출하면") {
            then("menuType이 VARIABLE인 식당만 반환한다") {
                val variableRestaurants = Restaurant.getVariableRestaurantList()
                variableRestaurants.all { it.menuType == MenuType.VARIABLE } shouldBe true
                variableRestaurants shouldBe listOf(
                    Restaurant.HAKSIK,
                    Restaurant.DODAM,
                    Restaurant.DORMITORY,
                    Restaurant.FACULTY,
                )
            }
        }

        `when`("analytics value를 확인하면") {
            then("기숙사 식당은 설계 문서 기준 값으로 노출된다") {
                Restaurant.DORMITORY.value shouldBe "dormitory"
            }
        }
    }

    given("ReportType") {
        `when`("toUiText를 호출하면") {
            then("descriptionResId 기반 StringResource를 반환한다") {
                val uiText = ReportType.COPY.toUiText()
                (uiText as UiText.StringResource).resId shouldBe R.string.report_type_copy
            }
        }
    }
})
