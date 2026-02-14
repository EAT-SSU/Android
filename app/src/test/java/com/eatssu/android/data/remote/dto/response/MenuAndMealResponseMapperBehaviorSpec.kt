package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class MenuAndMealResponseMapperBehaviorSpec : AppBehaviorSpec({

    given("GetFixedMenuResponse.mapFixedMenuResponseToMenu") {
        `when`("카테고리/메뉴 응답이 주어지면") {
            val response = GetFixedMenuResponse(
                categoryMenuListCollection = arrayListOf(
                    CategoryMenuListCollection(
                        category = "A",
                        menus = arrayListOf(
                            MenuInformationList(menuId = 1L, name = "돈까스", price = 5500, rating = 4.3),
                            MenuInformationList(menuId = null, name = null, price = null, rating = null),
                        ),
                    ),
                    CategoryMenuListCollection(
                        category = "B",
                        menus = arrayListOf(
                            MenuInformationList(menuId = 2L, name = "비빔밥", price = 6000, rating = 4.0),
                        ),
                    ),
                )
            )

            then("카테고리를 펼쳐 Menu 리스트로 매핑하고 null은 기본값으로 채운다") {
                val result = response.mapFixedMenuResponseToMenu()
                result shouldHaveSize 3
                result[0].id shouldBe 1L
                result[0].name shouldBe "돈까스"
                result[1].id shouldBe 0L
                result[1].name shouldBe ""
                result[1].price shouldBe 0
                result[1].rate shouldBe 0.0
                result[2].id shouldBe 2L
                result[2].name shouldBe "비빔밥"
            }
        }
    }

    given("List<GetMealResponse>.mapTodayMenuResponseToMenu") {
        `when`("식단 응답에 메뉴명이 일부 null로 섞여 있으면") {
            val response = listOf(
                GetMealResponse(
                    mealId = 5L,
                    price = 5000,
                    rating = 4.2,
                    briefMenus = listOf(
                        MenusInformationList(menuId = 1L, name = "제육"),
                        MenusInformationList(menuId = 2L, name = null),
                        MenusInformationList(menuId = 3L, name = "계란찜"),
                    ),
                ),
                GetMealResponse(
                    mealId = null,
                    price = null,
                    rating = null,
                    briefMenus = listOf(
                        MenusInformationList(menuId = 4L, name = null),
                    ),
                ),
            )

            then("null 이름은 제외해 문자열로 결합하고 null 필드는 기본값으로 변환한다") {
                val result = response.mapTodayMenuResponseToMenu()
                result shouldHaveSize 2
                result[0].id shouldBe 5L
                result[0].name shouldBe "제육, 계란찜"
                result[1].id shouldBe -1L
                result[1].name shouldBe ""
                result[1].price shouldBe 0
                result[1].rate shouldBe 0.0
            }
        }
    }

    given("List<GetMealResponse>.toDomain") {
        `when`("식단 응답을 도메인 메뉴명 리스트로 변환하면") {
            val response = listOf(
                GetMealResponse(
                    briefMenus = listOf(
                        MenusInformationList(name = "짜장면"),
                        MenusInformationList(name = null),
                    ),
                ),
                GetMealResponse(
                    briefMenus = listOf(
                        MenusInformationList(name = "우동"),
                    ),
                ),
            )

            then("meal 단위로 null이 제거된 문자열 리스트를 반환한다") {
                response.toDomain() shouldBe listOf(
                    listOf("짜장면"),
                    listOf("우동"),
                )
            }
        }
    }

    given("MenuOfMealResponse.toDomain") {
        `when`("menuList를 변환하면") {
            val response = MenuOfMealResponse(
                menuList = arrayListOf(
                    MenuList(menuId = 1L, name = "덮밥"),
                    MenuList(menuId = null, name = null),
                )
            )

            then("MenuMini 리스트로 매핑하고 null은 기본값으로 채운다") {
                val result = response.toDomain()
                result shouldHaveSize 2
                result[0].id shouldBe 1L
                result[0].name shouldBe "덮밥"
                result[1].id shouldBe -1L
                result[1].name shouldBe ""
            }
        }
    }
})
