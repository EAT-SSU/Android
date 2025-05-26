package com.eatssu.android.di

import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideReportService(retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideMealService(retrofit: Retrofit): MealService {
        return retrofit.create(MealService::class.java)
    }

    @Provides
    @Singleton
    fun provideMenuService(retrofit: Retrofit): MenuService {
        return retrofit.create(MenuService::class.java)
    }
}