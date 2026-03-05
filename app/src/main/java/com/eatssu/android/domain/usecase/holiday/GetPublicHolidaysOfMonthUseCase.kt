package com.eatssu.android.domain.usecase.holiday

import com.eatssu.android.domain.model.PublicHoliday
import com.eatssu.android.domain.repository.PublicHolidayRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 지정한 [YearMonth]의 공휴일 목록을 조회한다.
 *
 * 캐싱 정책(월 단위 메모리 캐시)은 usecase가 소유하고,
 * repository는 데이터 접근(원격/로컬)에만 집중한다.
 */
@Singleton
class GetPublicHolidaysOfMonthUseCase @Inject constructor(
    private val publicHolidayRepository: PublicHolidayRepository,
) {
    private val mutex = Mutex()
    private val cache: MutableMap<YearMonth, List<PublicHoliday>> = linkedMapOf()

    suspend operator fun invoke(yearMonth: YearMonth): List<PublicHoliday> {
        mutex.withLock {
            cache[yearMonth]?.let { return it }
        }

        val result = publicHolidayRepository.getHolidays(yearMonth)

        mutex.withLock {
            cache[yearMonth] = result
        }

        return result
    }
}
