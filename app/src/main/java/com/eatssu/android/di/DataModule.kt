package com.eatssu.android.di


import com.eatssu.android.data.remote.repository.FirebaseRemoteConfigRepositoryImpl
import com.eatssu.android.data.remote.repository.HealthCheckRepositoryImpl
import com.eatssu.android.data.remote.repository.MealRepositoryImpl
import com.eatssu.android.data.remote.repository.MenuRepositoryImpl
import com.eatssu.android.data.remote.repository.OauthRepositoryImpl
import com.eatssu.android.data.remote.repository.PartnershipRepositoryImpl
import com.eatssu.android.data.remote.repository.PublicHolidayRepositoryImpl
import com.eatssu.android.data.remote.repository.ReportRepositoryImpl
import com.eatssu.android.data.remote.repository.ReviewRepositoryImpl
import com.eatssu.android.data.remote.repository.UserRepositoryImpl
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.repository.HealthCheckRepository
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.domain.repository.MenuRepository
import com.eatssu.android.domain.repository.OauthRepository
import com.eatssu.android.domain.repository.PartnershipRepository
import com.eatssu.android.domain.repository.PublicHolidayRepository
import com.eatssu.android.domain.repository.ReportRepository
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.android.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsOauthRepository(
        oauthRepositoryImpl: OauthRepositoryImpl,
    ): OauthRepository

    @Binds
    internal abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl,
    ): UserRepository

    @Binds
    internal abstract fun bindsReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl,
    ): ReportRepository

    @Binds
    internal abstract fun bindsReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl,
    ): ReviewRepository

    @Binds
    internal abstract fun bindsMealRepository(
        mealRepositoryImpl: MealRepositoryImpl,
    ): MealRepository

    @Binds
    internal abstract fun bindsPartnershipRepository(
        partnershipRepositoryImpl: PartnershipRepositoryImpl,
    ): PartnershipRepository

    @Binds
    internal abstract fun bindsHealthCheckRepository(
        healthCheckRepositoryImpl: HealthCheckRepositoryImpl,
    ): HealthCheckRepository

    @Binds
    internal abstract fun bindsMenuRepository(
        menuRepositoryImpl: MenuRepositoryImpl,
    ): MenuRepository

    @Binds
    internal abstract fun bindsFirebaseRemoteConfigRepository(
        firebaseRemoteConfigRepositoryImpl: FirebaseRemoteConfigRepositoryImpl,
    ): FirebaseRemoteConfigRepository

    @Binds
    internal abstract fun bindsPublicHolidayRepository(
        publicHolidayRepositoryImpl: PublicHolidayRepositoryImpl,
    ): PublicHolidayRepository
}
