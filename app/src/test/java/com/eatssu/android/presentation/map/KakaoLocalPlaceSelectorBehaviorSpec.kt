package com.eatssu.android.presentation.map

import com.eatssu.android.data.remote.dto.response.KakaoLocalSearchResponse
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class KakaoLocalPlaceSelectorBehaviorSpec : AppBehaviorSpec({

    given("Kakao place candidates near a partnership") {
        `when`("the server place ID exists in the search results") {
            val serverPlace = document(id = "server", name = "에이블 PC방", distance = 65)
            val nearestPlace = document(id = "nearest", name = "에이블 PC CAFE 숭실대점", distance = 8)

            then("the server-provided place is selected first") {
                selectBestKakaoPlace(
                    documents = listOf(serverPlace, nearestPlace),
                    preferredPlaceId = "server",
                ) shouldBe serverPlace
            }
        }

        `when`("the server place ID is absent from the search results") {
            val fartherPlace = document(id = "farther", name = "놀숲 서울대점", distance = 120)
            val nearestPlace = document(id = "nearest", name = "놀숲 숭실대점", distance = 15)

            then("the nearest result is selected") {
                selectBestKakaoPlace(
                    documents = listOf(fartherPlace, nearestPlace),
                    preferredPlaceId = "missing",
                ) shouldBe nearestPlace
            }
        }

        `when`("the selected result is outside the allowed radius") {
            then("the resolver falls back to coordinates") {
                selectBestKakaoPlace(
                    documents = listOf(document(id = "1", name = "놀숲 숭실대점", distance = 301)),
                    preferredPlaceId = "1",
                ).shouldBeNull()
            }
        }
    }
})

private fun document(
    id: String,
    name: String,
    distance: Int,
) = KakaoLocalSearchResponse.Document(
    id = id,
    placeName = name,
    distance = distance.toString(),
)
