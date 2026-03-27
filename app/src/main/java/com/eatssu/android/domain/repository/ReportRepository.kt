package com.eatssu.android.domain.repository

interface ReportRepository {
    suspend fun reportReview(
        reviewId: Long,
        reportType: String,
        content: String,
    ): Boolean
}
