package com.eatssu.android.presentation.cafeteria.review.translation

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe

class ReviewTranslationLanguageBehaviorSpec : AppBehaviorSpec({

    given("영어 번역 버튼 노출 조건") {
        `when`("리뷰에 번역 가능한 비ASCII 문자가 있으면") {
            then("번역 버튼을 보여준다") {
                shouldShowReviewTranslationAction("EN", true, "맛있어요") shouldBe true
                shouldShowReviewTranslationAction("EN", true, "おいしいです") shouldBe true
                shouldShowReviewTranslationAction("EN", true, "ngon quá") shouldBe true
            }
        }

        `when`("리뷰가 ASCII 문자로만 구성되면") {
            then("번역 버튼을 숨긴다") {
                shouldShowReviewTranslationAction("EN", true, "yoyoyoyoy") shouldBe false
                shouldShowReviewTranslationAction("EN", true, "Already in English") shouldBe false
                shouldShowReviewTranslationAction("EN", true, "123 !!!") shouldBe false
            }
        }

        `when`("지원하지 않는 번역 언어이면") {
            then("번역 버튼을 숨긴다") {
                shouldShowReviewTranslationAction(null, true, "맛있어요") shouldBe false
            }
        }

        `when`("로그인하지 않은 사용자이면") {
            then("번역 버튼을 숨긴다") {
                shouldShowReviewTranslationAction("EN", false, "맛있어요") shouldBe false
            }
        }

        `when`("앱 언어로 번역 대상 언어를 결정하면") {
            then("영어 앱에서만 영어 번역을 지원한다") {
                reviewTranslationTargetLanguage("en") shouldBe "EN"
                reviewTranslationTargetLanguage("ko") shouldBe null
                reviewTranslationTargetLanguage("ja") shouldBe null
                reviewTranslationTargetLanguage("vi") shouldBe null
            }
        }
    }
})
