package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.remote.service.PublicHolidayService
import com.eatssu.android.domain.model.PublicHoliday
import com.eatssu.android.domain.repository.PublicHolidayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLEncoder
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Named
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicHolidayRepositoryImpl @Inject constructor(
    private val publicHolidayService: PublicHolidayService,
    @Named(PUBLIC_HOLIDAY_SERVICE_KEY_NAME) private val serviceKey: String,
) : PublicHolidayRepository {

    companion object {
        const val PUBLIC_HOLIDAY_SERVICE_KEY_NAME: String = "PublicHolidayServiceKey"
    }

    /**
     * 외부 공휴일 API를 호출해 해당 [YearMonth]의 공휴일 목록을 조회한다.
     *
     *
     * - `HOLIDAY_API_KEY`가 비어있으면 네트워크 호출 없이 빈 리스트를 반환한다.
     * - 네트워크/파싱 실패 또는 비정상 resultCode인 경우 빈 리스트를 반환한다.
     * - `isHoliday == "Y"`만 필터링하고, 날짜 기준으로 중복 제거 후 오름차순 정렬한다.
     */
    override suspend fun getHolidays(yearMonth: YearMonth): List<PublicHoliday> {
        if (serviceKey.isBlank()) {
            Timber.w("HOLIDAY_API_KEY is blank; skipping public holiday fetch")
            return emptyList()
        }

        val normalizedKey = normalizeServiceKey(serviceKey)

        val responseResult = withContext(Dispatchers.IO) {
            runCatching {
                publicHolidayService.getRestDeInfo(
                    serviceKey = normalizedKey,
                    solYear = yearMonth.year.toString(),
                    solMonth = yearMonth.monthValue.toString().padStart(2, '0'),
                )
            }
        }

        val response = responseResult.getOrNull() ?: run {
            Timber.w(responseResult.exceptionOrNull(), "Failed to fetch public holidays")
            return emptyList()
        }

        val resultCode = response.response?.header?.resultCode
        if (resultCode != null && resultCode != "00") {
            Timber.w(
                "PublicHoliday API returned non-normal resultCode=%s msg=%s",
                resultCode,
                response.response?.header?.resultMsg,
            )
            return emptyList()
        }

        val holidays = response.response
            ?.body
            ?.items
            ?.item
            .orEmpty()
            .asSequence()
            .filter { it.isHoliday.equals("Y", ignoreCase = true) }
            .mapNotNull { item ->
                val date = item.locdate?.let(::parseLocdate)
                val name = item.dateName?.trim().orEmpty()

                if (date == null || name.isBlank()) return@mapNotNull null
                PublicHoliday(date = date, name = name)
            }
            .distinctBy { it.date }
            .sortedBy { it.date }
            .toList()

        return holidays
    }

    private fun parseLocdate(locdate: Long): LocalDate? {
        val s = locdate.toString()
        if (s.length != 8) return null

        return runCatching {
            val year = s.substring(0, 4).toInt()
            val month = s.substring(4, 6).toInt()
            val day = s.substring(6, 8).toInt()
            LocalDate.of(year, month, day)
        }.getOrNull()
    }

    private fun normalizeServiceKey(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""

        return if ('%' in trimmed) trimmed else URLEncoder.encode(trimmed, Charsets.UTF_8.name())
    }
}
