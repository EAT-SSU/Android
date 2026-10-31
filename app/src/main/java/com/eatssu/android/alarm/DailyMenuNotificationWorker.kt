package com.eatssu.android.alarm

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.usecase.alarm.GetDailyFavoriteMealsUseCase
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.IOException
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class DailyMenuNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val settings: SettingDataStore,
    private val accountDataStore: AccountDataStore,
    private val getMatches: GetDailyFavoriteMealsUseCase,
    private val getAccessToken: GetAccessTokenUseCase,
    private val factory: MenuNotificationFactory,
    private val publisher: NotificationPublisher,
    private val clock: Clock,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val date =
            inputData.getString(KEY_DATE)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?: return Result.failure()
        if (date != LocalDate.now(clock.withZone(ZoneId.systemDefault())) || date.dayOfWeek in listOf(
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            )
        ) return Result.success()
        return try {
            if (!settings.dailyNotificationStatus.first()) return Result.success()
            val account = getAccessToken()
            val email = accountDataStore.email.first()
            val matches = try {
                if (account.isBlank()) emptyList() else getMatches(date)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Timber.w(error, "찜 식단 조회 실패, 기본 알림으로 대체")
                emptyList()
            }
            if (date != LocalDate.now(clock.withZone(ZoneId.systemDefault())) || !settings.dailyNotificationStatus.first() || (account.isBlank() != getAccessToken().isBlank()) || email != accountDataStore.email.first()) return Result.success()
            factory.create(date, matches, settings.appLanguage.first())
                .forEach { publisher.publish(it) }
            Result.success()
        } catch (error: IOException) {
            Timber.w(error, "알림 저장 실패")
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }

    companion object {
        private const val KEY_DATE = "notification_date"
        fun enqueue(context: Context, date: LocalDate) {
            val request = OneTimeWorkRequestBuilder<DailyMenuNotificationWorker>()
                .setInputData(workDataOf(KEY_DATE to date.toString())).build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("daily_menu_$date", ExistingWorkPolicy.KEEP, request)
        }
    }
}
