package com.eatssu.android.di

import com.eatssu.android.data.remote.service.HealthCheckService
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.data.remote.service.MenuService
import com.eatssu.android.data.remote.service.OauthService
import com.eatssu.android.data.remote.service.PartnershipService
import com.eatssu.android.data.remote.service.ReportService
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.data.remote.service.UserService
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
    fun provideOauthService(@NoToken noTokenRetrofit: Retrofit): OauthService {
        return noTokenRetrofit.create(OauthService::class.java)
    }

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

    @Provides
    @Singleton
    fun providePartnershipService(retrofit: Retrofit): PartnershipService {
        return retrofit.create(PartnershipService::class.java)
    }

    @Provides
    @Singleton
    fun provideHealthCheckService(@NoToken noTokenRetrofit: Retrofit): HealthCheckService {
        return noTokenRetrofit.create(HealthCheckService::class.java)
    }

    // 착한가격업소 서비스 제공 (비로그인 사용자도 호출 가능하도록 @NoToken Retrofit 사용)
    @Provides
    @Singleton
    fun provideGoodPriceStoreService(@NoToken noTokenRetrofit: Retrofit): com.eatssu.android.data.remote.service.GoodPriceStoreService {
        return noTokenRetrofit.create(com.eatssu.android.data.remote.service.GoodPriceStoreService::class.java)
    }
}
