package com.eatssu.android.data.remote.repository

import com.eatssu.android.R
import com.eatssu.common.enums.Restaurant
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseRemoteConfigRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("FirebaseRemoteConfigRepositoryImpl") {
        val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)
        mockkStatic(FirebaseRemoteConfig::class)
        every { FirebaseRemoteConfig.getInstance() } returns remoteConfig
        every { remoteConfig.setConfigSettingsAsync(any()) } returns Tasks.forResult(null)
        every { remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config) } returns Tasks.forResult(null)

        val repository = FirebaseRemoteConfigRepositoryImpl(
            Json { ignoreUnknownKeys = true }
        )

        `when`("minimum version fetch가 성공하면") {
            every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
            every { remoteConfig.getLong("android_version_code") } returns 321L

            then("최소 버전 코드를 반환한다") {
                runTest {
                    repository.getMinimumVersionCode() shouldBe 321L
                    verify(exactly = 1) { remoteConfig.fetchAndActivate() }
                    verify(exactly = 1) { remoteConfig.getLong("android_version_code") }
                }
            }
        }

        `when`("minimum version fetch가 실패해도") {
            every { remoteConfig.fetchAndActivate() } returns Tasks.forException(IllegalStateException("fetch fail"))
            every { remoteConfig.getLong("android_version_code") } returns 100L

            then("예외를 삼키고 캐시 값 반환을 시도한다") {
                runTest {
                    repository.getMinimumVersionCode() shouldBe 100L
                }
            }
        }

        `when`("식당 정보 JSON이 유효하고 대상 enum이 존재하면") {
            every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
            every { remoteConfig.getString("cafeteria_information") } returns """
                [
                  {"enum":"HAKSIK","name":"학식당","location":"B1","image":"a.png","time":"11:00-14:00","etc":"-"},
                  {"enum":"DODAM","name":"도담","location":"1F","image":"b.png","time":"11:00-14:00","etc":"-"}
                ]
            """.trimIndent()

            then("요청한 식당의 정보를 반환한다") {
                runTest {
                    val result = repository.getRestaurantInfo(Restaurant.HAKSIK)
                    result?.enum shouldBe Restaurant.HAKSIK
                    result?.name shouldBe "학식당"
                    result?.location shouldBe "B1"
                }
            }
        }

        `when`("식당 정보 JSON은 유효하지만 대상 enum이 없으면") {
            every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
            every { remoteConfig.getString("cafeteria_information") } returns """
                [{"enum":"HAKSIK","name":"학식당","location":"B1","image":"a.png","time":"11:00-14:00","etc":"-"}]
            """.trimIndent()

            then("null을 반환한다") {
                runTest {
                    repository.getRestaurantInfo(Restaurant.DODAM).shouldBeNull()
                }
            }
        }

        `when`("식당 정보 JSON 파싱에 실패하면") {
            every { remoteConfig.fetchAndActivate() } returns Tasks.forResult(true)
            every { remoteConfig.getString("cafeteria_information") } returns "{invalid-json}"

            then("빈 리스트로 처리되어 null을 반환한다") {
                runTest {
                    repository.getRestaurantInfo(Restaurant.HAKSIK).shouldBeNull()
                }
            }
        }
    }
})
