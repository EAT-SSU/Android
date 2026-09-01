package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.CategoryMenuListCollection
import com.eatssu.android.data.remote.dto.response.GetFixedMenuResponse
import com.eatssu.android.data.remote.dto.response.MenuInformationList
import com.eatssu.android.data.remote.service.MenuService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MenuRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("MenuRepositoryImpl") {
        val menuService = mockk<MenuService>()
        val repository = MenuRepositoryImpl(menuService)

        `when`("고정 메뉴 API가 성공하면") {
            val response = GetFixedMenuResponse(
                categoryMenuListCollection = arrayListOf(
                    CategoryMenuListCollection(
                        category = "A",
                        menus = arrayListOf(
                            MenuInformationList(
                                menuId = 1L,
                                name = "돈까스",
                                price = 5000,
                                rating = 4.5,
                            )
                        ),
                    )
                )
            )
            coEvery { menuService.getFixMenu(Restaurant.SNACK_CORNER.toString()) } returns ApiResult.Success(response)

            then("도메인 Menu 리스트로 매핑한다") {
                runTest {
                    val result = repository.getFixedMenuList(Restaurant.SNACK_CORNER)
                    result.size shouldBe 1
                    result.first().name shouldBe "돈까스"
                }
            }
        }

        `when`("고정 메뉴 API가 실패하면") {
            coEvery {
                menuService.getFixMenu(Restaurant.SNACK_CORNER.toString())
            } returns ApiResult.Failure(500, "err")

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getFixedMenuList(Restaurant.SNACK_CORNER) shouldBe emptyList()
                }
            }
        }
    }
})
