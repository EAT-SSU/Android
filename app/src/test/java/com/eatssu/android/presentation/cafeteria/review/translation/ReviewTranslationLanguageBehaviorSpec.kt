package com.eatssu.android.presentation.cafeteria.review.translation

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe

class ReviewTranslationLanguageBehaviorSpec : AppBehaviorSpec({

    given("다국어 번역 버튼 노출 조건") {
        `when`("리뷰에 한글이 포함되어 있으면") {
            then("지원하는 모든 언어에서 번역 버튼을 보여준다") {
                listOf("EN", "JA", "VI").forEach { targetLanguage ->
                    shouldShowReviewTranslationAction(targetLanguage, true, "맛있어요") shouldBe true
                    shouldShowReviewTranslationAction(targetLanguage, true, "good ㅋㅋ") shouldBe true
                }
            }
        }

        `when`("리뷰에 한글이 없으면") {
            then("번역 버튼을 숨긴다") {
                listOf("EN", "JA", "VI").forEach { targetLanguage ->
                    shouldShowReviewTranslationAction(targetLanguage, true, "yoyoyoyoy") shouldBe false
                    shouldShowReviewTranslationAction(targetLanguage, true, "Already in English") shouldBe false
                    shouldShowReviewTranslationAction(targetLanguage, true, "おいしいです") shouldBe false
                    shouldShowReviewTranslationAction(targetLanguage, true, "ngon quá") shouldBe false
                    shouldShowReviewTranslationAction(targetLanguage, true, "123 !!!") shouldBe false
                }
            }
        }

        `when`("지원하지 않는 번역 언어이면") {
            then("번역 버튼을 숨긴다") {
                shouldShowReviewTranslationAction(null, true, "맛있어요") shouldBe false
                shouldShowReviewTranslationAction("KO", true, "맛있어요") shouldBe false
            }
        }

        `when`("로그인하지 않은 사용자이면") {
            then("번역 버튼을 숨긴다") {
                shouldShowReviewTranslationAction("EN", false, "맛있어요") shouldBe false
            }
        }

        `when`("앱 언어로 번역 대상 언어를 결정하면") {
            then("영어, 일본어, 베트남어 앱에서 해당 언어로 번역한다") {
                reviewTranslationTargetLanguage("en") shouldBe "EN"
                reviewTranslationTargetLanguage("ja") shouldBe "JA"
                reviewTranslationTargetLanguage("vi") shouldBe "VI"
                reviewTranslationTargetLanguage("ko") shouldBe null
                reviewTranslationTargetLanguage("fr") shouldBe null
            }
        }
    }
})
