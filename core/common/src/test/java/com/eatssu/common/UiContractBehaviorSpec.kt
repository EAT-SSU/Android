package com.eatssu.common

import com.eatssu.common.enums.ToastType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class UiContractBehaviorSpec : BehaviorSpec({

    given("UiState") {
        `when`("각 상태를 생성하면") {
            then("타입과 payload가 의도대로 유지된다") {
                UiState.Init shouldBe UiState.Init
                UiState.Loading shouldBe UiState.Loading
                UiState.Error shouldBe UiState.Error
                UiState.Success("data") shouldBe UiState.Success("data")
            }
        }
    }

    given("UiEvent") {
        `when`("ShowToast를 생성하면") {
            then("메시지와 토스트 타입을 보존한다") {
                val event = UiEvent.ShowToast(
                    message = UiText.StringResource(resId = 1),
                    type = ToastType.SUCCESS,
                )

                (event.message as UiText.StringResource).resId shouldBe 1
                event.type shouldBe ToastType.SUCCESS
            }
        }

        `when`("NavigateBack을 사용하면") {
            then("싱글톤 이벤트를 유지한다") {
                UiEvent.NavigateBack shouldBe UiEvent.NavigateBack
            }
        }
    }
})
