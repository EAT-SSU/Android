package com.eatssu.android.presentation.cafeteria.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest

class MenuSectionBuilderBehaviorSpec : AppBehaviorSpec({

    given("식당별 메뉴 섹션 구성") {
        `when`("메뉴가 비어 있는 식당이 포함되면") {
            val menu = Menu(id = 1, name = "오늘의 메뉴", price = 5_000, rate = 4.0)
            val menuMap = linkedMapOf(
                Restaurant.DODAM to emptyList(),
                Restaurant.HAKSIK to listOf(menu),
            )

            then("빈 식당을 제거하지 않고 식당 순서대로 구성한다") {
                runTest {
                    val sections = buildMenuSections(menuMap) { restaurant ->
                        "${restaurant.name} 위치"
                    }

                    sections.map { it.cafeteria } shouldBe listOf(
                        Restaurant.HAKSIK,
                        Restaurant.DODAM,
                    )
                    sections.first { it.cafeteria == Restaurant.HAKSIK }.menuList shouldBe listOf(menu)
                    sections.first { it.cafeteria == Restaurant.DODAM }.menuList shouldBe emptyList()
                    sections.first { it.cafeteria == Restaurant.DODAM }.cafeteriaLocation shouldBe
                        "DODAM 위치"
                }
            }
        }
    }
})
