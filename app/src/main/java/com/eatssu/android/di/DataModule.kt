package com.eatssu.android.di


import com.eatssu.android.data.repository.OauthRepository
import com.eatssu.android.data.repository.OauthRepositoryImpl
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
    internal abstract fun bindsQuestRepository(
        oauthRepositoryImpl: OauthRepositoryImpl,
    ): OauthRepository

    @Binds
    internal abstract fun bindsUserRepository(
        userRepository: UserRepositoryImpl,
    ): UserRepository

}