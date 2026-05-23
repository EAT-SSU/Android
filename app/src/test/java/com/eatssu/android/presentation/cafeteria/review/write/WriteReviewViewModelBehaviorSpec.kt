package com.eatssu.android.presentation.cafeteria.review.write

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.usecase.menu.GetValidMenusOfMealUseCase
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.expectNavigateBack
import com.eatssu.android.test.expectToast
import com.eatssu.android.test.successDataAs
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ReviewAnalyticsEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ToastType
import id.zelory.compressor.Compressor
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.io.path.createTempDirectory
import java.io.ByteArrayInputStream
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class WriteReviewViewModelBehaviorSpec : AppBehaviorSpec({

    given("리뷰 작성 화면") {
        val writeReviewUseCase = mockk<WriteReviewUseCase>()
        val getImageUrlUseCase = mockk<GetImageUrlUseCase>()
        val getValidMenusOfMealUseCase = mockk<GetValidMenusOfMealUseCase>()
        val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

        `when`("고정 메뉴 타입을 로드하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)

            then("단일 메뉴 Editing 상태를 만든다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(
                        WriteReviewState.Editing(
                            menuList = listOf(MenuMini(1L, "돈가스")),
                            rating = 0,
                            content = "",
                            likedMenuIds = emptySet(),
                            selectedImageUri = null,
                        )
                    )
                }
            }
        }

        `when`("가변 메뉴 타입을 로드하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            val menus = listOf(MenuMini(10L, "A"), MenuMini(11L, "B"))
            coEvery { getValidMenusOfMealUseCase(999L) } returns menus

            then("usecase 결과로 Editing 상태를 만든다") {
                runTest {
                    viewModel.loadMenuList(MenuType.VARIABLE, 999L, "")
                    advanceUntilIdle()

                    (viewModel.uiState.value as UiState.Success).data shouldBe WriteReviewState.Editing(
                        menuList = menus,
                        rating = 0,
                        content = "",
                        likedMenuIds = emptySet(),
                        selectedImageUri = null,
                    )
                }
            }
        }

        `when`("rating이 0인 상태에서 submit하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)

            then("요청하지 않는다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()

                    viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, mockk(relaxed = true))
                    advanceUntilIdle()

                    coVerify(exactly = 0) {
                        writeReviewUseCase(any(), any(), any(), any(), any(), any())
                    }
                }
            }
        }

        `when`("Editing 상태가 아닐 때 submit하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)

            then("아무 동작도 수행하지 않는다") {
                runTest {
                    viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, mockk(relaxed = true))
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Init
                    coVerify(exactly = 0) {
                        writeReviewUseCase(any(), any(), any(), any(), any(), any())
                    }
                }
            }
        }

        `when`("좋아요 메뉴를 같은 id로 두 번 토글하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)

            then("likedMenuIds가 추가됐다가 다시 제거된다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()

                    viewModel.toggleLike(101L)
                    viewModel.uiState.value.successDataAs<WriteReviewState.Editing>().likedMenuIds shouldBe setOf(101L)

                    viewModel.toggleLike(101L)
                    viewModel.uiState.value.successDataAs<WriteReviewState.Editing>().likedMenuIds shouldBe emptySet()
                }
            }
        }

        `when`("이미지 없이 리뷰 작성이 성공하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            coEvery {
                writeReviewUseCase(
                    menuType = MenuType.FIXED,
                    itemId = 1L,
                    rating = 5,
                    content = "good",
                    imageUrl = null,
                    likeMenuIdList = any(),
                )
            } returns true

            then("성공 토스트와 NavigateBack 이벤트를 보낸다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(5)
                    viewModel.onContentChanged("good")

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, mockk(relaxed = true))
                        advanceUntilIdle()

                        expectToast(R.string.toast_review_write_success, ToastType.SUCCESS)
                        expectNavigateBack()
                        verify {
                            analyticsTracker.track(
                                ReviewAnalyticsEvent.Completed(
                                    rating = 5,
                                    likes = 0,
                                    photoAttached = false,
                                    restaurant = Restaurant.HAKSIK,
                                ),
                            )
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("이미지 업로드 성공 후 리뷰 작성이 성공하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            val context = mockk<Context>()
            val resolver = mockk<ContentResolver>()
            val uri = mockk<Uri>()
            val cacheDir = createTempDirectory(prefix = "write-review").toFile()
            val compressed = File(cacheDir, "compressed.jpg").apply { writeBytes(byteArrayOf(1, 2, 3)) }

            every { context.contentResolver } returns resolver
            every { context.cacheDir } returns cacheDir
            every { resolver.openInputStream(uri) } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))

            mockkObject(Compressor)
            coEvery { Compressor.compress(context, any()) } returns compressed
            coEvery { getImageUrlUseCase(compressed) } returns "https://img"
            coEvery {
                writeReviewUseCase(MenuType.FIXED, 1L, 4, "", "https://img", any())
            } returns true

            then("이미지 업로드 성공 토스트 후 리뷰 성공 토스트와 뒤로가기를 보낸다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(4)
                    viewModel.setSelectedImage(uri)
                    clearMocks(writeReviewUseCase, answers = false, recordedCalls = true)

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, context)
                        advanceUntilIdle()

                        expectToast(R.string.toast_image_upload_success, ToastType.SUCCESS)
                        expectToast(R.string.toast_review_write_success, ToastType.SUCCESS)
                        expectNavigateBack()
                        verify {
                            analyticsTracker.track(
                                ReviewAnalyticsEvent.Completed(
                                    rating = 4,
                                    likes = 0,
                                    photoAttached = true,
                                    restaurant = Restaurant.HAKSIK,
                                ),
                            )
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("이미지 업로드 URL이 null이면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            val context = mockk<Context>()
            val resolver = mockk<ContentResolver>()
            val uri = mockk<Uri>()
            val cacheDir = createTempDirectory(prefix = "write-review-null-url").toFile()
            val compressed = File(cacheDir, "compressed.jpg").apply { writeBytes(byteArrayOf(1, 2, 3)) }

            every { context.contentResolver } returns resolver
            every { context.cacheDir } returns cacheDir
            every { resolver.openInputStream(uri) } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))

            mockkObject(Compressor)
            coEvery { Compressor.compress(context, any()) } returns compressed
            coEvery { getImageUrlUseCase(compressed) } returns null

            then("업로드 실패 토스트를 보내고 Editing으로 롤백한다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(4)
                    viewModel.setSelectedImage(uri)

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, context)
                        advanceUntilIdle()

                        expectToast(R.string.toast_image_upload_failed, ToastType.ERROR)
                        viewModel.uiState.value.successDataAs<WriteReviewState.Editing>()
                        coVerify(exactly = 0) { writeReviewUseCase(any(), any(), any(), any(), any(), any()) }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("이미지 압축이 실패하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            val context = mockk<Context>()
            val resolver = mockk<ContentResolver>()
            val uri = mockk<Uri>()
            val cacheDir = createTempDirectory(prefix = "write-review-fail").toFile()

            every { context.contentResolver } returns resolver
            every { context.cacheDir } returns cacheDir
            every { resolver.openInputStream(uri) } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))

            mockkObject(Compressor)
            coEvery { Compressor.compress(context, any()) } throws IllegalStateException("compress")

            then("기존 Editing 상태로 롤백하고 압축 실패 토스트를 보낸다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(4)
                    viewModel.setSelectedImage(uri)

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, context)
                        advanceUntilIdle()

                        expectToast(R.string.toast_image_compress_failed, ToastType.ERROR)
                        viewModel.uiState.value.successDataAs<WriteReviewState.Editing>()
                        coVerify(exactly = 0) { writeReviewUseCase(any(), any(), any(), any(), any(), any()) }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("이미지 업로드 과정에서 예외가 발생하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            val context = mockk<Context>()
            val resolver = mockk<ContentResolver>()
            val uri = mockk<Uri>()

            every { context.contentResolver } returns resolver
            every { resolver.openInputStream(uri) } returns null

            then("업로드 실패 토스트를 보낸다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(4)
                    viewModel.setSelectedImage(uri)

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, context)
                        advanceUntilIdle()

                        expectToast(R.string.toast_image_upload_failed, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("리뷰 작성 API가 실패하면") {
            val viewModel = WriteReviewViewModel(writeReviewUseCase, getImageUrlUseCase, getValidMenusOfMealUseCase, analyticsTracker)
            coEvery { writeReviewUseCase(MenuType.FIXED, 1L, 3, "", null, any()) } returns false

            then("Editing으로 롤백하고 실패 토스트를 보낸다") {
                runTest {
                    viewModel.loadMenuList(MenuType.FIXED, 1L, "돈가스")
                    advanceUntilIdle()
                    viewModel.onRatingChanged(3)

                    viewModel.uiEvent.test {
                        viewModel.postReview(MenuType.FIXED, Restaurant.HAKSIK, 1L, mockk(relaxed = true))
                        advanceUntilIdle()

                        viewModel.uiState.value.successDataAs<WriteReviewState.Editing>()
                        expectToast(R.string.toast_review_write_failed, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})
