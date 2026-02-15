package com.eatssu.android.screenshot.fixtures

import android.net.Uri
import androidx.paging.PagingData
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.presentation.cafeteria.review.list.ReviewListState
import com.eatssu.android.presentation.cafeteria.review.modify.ModifyState
import com.eatssu.android.presentation.cafeteria.review.write.WriteReviewState
import com.eatssu.android.presentation.map.MapState
import com.eatssu.android.presentation.map.component.FilterType
import com.eatssu.android.presentation.mypage.myreview.MyReviewState
import com.eatssu.common.UiState

data class WriteReviewSnapshotModel(
    val menuList: List<MenuMini>,
    val rating: Int,
    val content: String,
    val likedMenuIds: Set<Long>,
    val selectedImageUri: Uri?,
    val isPosting: Boolean,
)

data class ModifyReviewSnapshotModel(
    val rating: Int,
    val content: String,
    val menuLikeInfos: List<Review.MenuLikeInfo>,
    val isSubmitting: Boolean,
    val canSubmit: Boolean,
)

object FakeUiStates {
    fun reviewPagingData(state: String): PagingData<Review> {
        return when (state) {
            "success" -> PagingData.from(FakeScreenFixtures.reviewList(count = 5, writer = false))
            "empty" -> PagingData.empty()
            else -> PagingData.empty()
        }
    }

    fun reviewListUiState(state: String): UiState<ReviewListState> {
        return when (state) {
            "loading" -> UiState.Loading
            "empty" -> UiState.Success(ReviewListState(FakeScreenFixtures.reviewInfo(0)))
            "success" -> UiState.Success(ReviewListState(FakeScreenFixtures.reviewInfo(5)))
            "error" -> UiState.Error
            else -> UiState.Init
        }
    }

    fun myReviewUiState(state: String): UiState<MyReviewState> {
        return when (state) {
            "loading" -> UiState.Loading
            "empty" -> UiState.Success(MyReviewState.NoReview)
            "success" -> UiState.Success(MyReviewState.ReviewExists(FakeScreenFixtures.reviewList()))
            "error" -> UiState.Error
            else -> UiState.Init
        }
    }

    fun writeReviewModel(state: String): WriteReviewSnapshotModel {
        return when (state) {
            "loading" -> WriteReviewSnapshotModel(
                menuList = FakeScreenFixtures.menuList(),
                rating = 0,
                content = "",
                likedMenuIds = emptySet(),
                selectedImageUri = null,
                isPosting = true
            )
            "empty" -> WriteReviewSnapshotModel(
                menuList = FakeScreenFixtures.menuList(),
                rating = 0,
                content = "",
                likedMenuIds = emptySet(),
                selectedImageUri = null,
                isPosting = false
            )
            "success" -> WriteReviewSnapshotModel(
                menuList = FakeScreenFixtures.menuList(),
                rating = 4,
                content = "맛있어요",
                likedMenuIds = setOf(1L, 3L),
                selectedImageUri = null,
                isPosting = false
            )
            else -> WriteReviewSnapshotModel(
                menuList = FakeScreenFixtures.menuList(),
                rating = 2,
                content = "에러 상태 표시",
                likedMenuIds = emptySet(),
                selectedImageUri = null,
                isPosting = false
            )
        }
    }

    fun modifyReviewModel(state: String): ModifyReviewSnapshotModel {
        return when (state) {
            "loading" -> ModifyReviewSnapshotModel(
                rating = 3,
                content = "수정 중",
                menuLikeInfos = FakeScreenFixtures.menuLikeInfoList(),
                isSubmitting = true,
                canSubmit = false
            )
            "empty" -> ModifyReviewSnapshotModel(
                rating = 0,
                content = "",
                menuLikeInfos = FakeScreenFixtures.menuLikeInfoList(),
                isSubmitting = false,
                canSubmit = false
            )
            "success" -> ModifyReviewSnapshotModel(
                rating = 4,
                content = "수정 완료",
                menuLikeInfos = FakeScreenFixtures.menuLikeInfoList(),
                isSubmitting = false,
                canSubmit = true
            )
            else -> ModifyReviewSnapshotModel(
                rating = 1,
                content = "오류 상태",
                menuLikeInfos = FakeScreenFixtures.menuLikeInfoList(),
                isSubmitting = false,
                canSubmit = false
            )
        }
    }

    fun mapState(state: String): MapState {
        val filter = if (state == "empty") FilterType.All else FilterType.Mine
        return MapState(
            partnerships = if (state == "empty") emptyList() else FakeScreenFixtures.partnershipList(),
            restaurantPartnershipInfo = if (state == "success") FakeScreenFixtures.partnershipRestaurant() else null,
            selectedFilter = filter,
            filterChangeResult = when (state) {
                "error" -> MapState.FilterChangeResult.RequiresDepartment
                else -> null
            }
        )
    }

    fun writeReviewState(state: String): UiState<WriteReviewState> {
        val model = writeReviewModel(state)
        return if (model.isPosting) {
            UiState.Success(
                WriteReviewState.Posting(
                    menuList = model.menuList,
                    rating = model.rating,
                    content = model.content,
                    likedMenuIds = model.likedMenuIds,
                    selectedImageUri = model.selectedImageUri
                )
            )
        } else {
            UiState.Success(
                WriteReviewState.Editing(
                    menuList = model.menuList,
                    rating = model.rating,
                    content = model.content,
                    likedMenuIds = model.likedMenuIds,
                    selectedImageUri = model.selectedImageUri
                )
            )
        }
    }

    fun modifyReviewState(state: String): UiState<ModifyState> {
        val model = modifyReviewModel(state)
        val baseline = ModifyState.Baseline(
            rating = 3,
            content = "기존 내용",
            menuLikeInfos = FakeScreenFixtures.menuLikeInfoList(),
        )

        return if (model.isSubmitting) {
            UiState.Success(
                ModifyState.Modifying(
                    rating = model.rating,
                    content = model.content,
                    menuLikeInfos = model.menuLikeInfos,
                    baseline = baseline
                )
            )
        } else {
            UiState.Success(
                ModifyState.Editing(
                    rating = model.rating,
                    content = model.content,
                    menuLikeInfos = model.menuLikeInfos,
                    baseline = baseline
                )
            )
        }
    }
}
