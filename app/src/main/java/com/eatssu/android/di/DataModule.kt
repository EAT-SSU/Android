package com.eatssu.android.di


import com.eatssu.android.data.repository.OauthRepository
import com.eatssu.android.data.repository.OauthRepositoryImpl
import com.eatssu.android.data.repository.ReportRepository
import com.eatssu.android.data.repository.ReportRepositoryImpl
import com.eatssu.android.data.repository.UserRepository
import com.eatssu.android.data.repository.UserRepositoryImpl
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

}