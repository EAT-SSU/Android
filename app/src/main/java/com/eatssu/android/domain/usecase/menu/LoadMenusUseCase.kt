package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.model.MenuLoadResult
import com.eatssu.android.domain.usecase.holiday.GetPublicHolidayOfDateUseCase
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LoadMenusUseCase @Inject constructor(
    private val getMenuListUseCase: GetMenuListUseCase,
    private val getPublicHolidayOfDateUseCase: GetPublicHolidayOfDateUseCase,
) {
    private val menuDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    suspend operator fun invoke(date: LocalDate, time: Time): MenuLoadResult = coroutineScope {
        val holiday = runCatching { getPublicHolidayOfDateUseCase(date) }.getOrNull()
        val isPublicHoliday = holiday != null

        val restaurantsToLoad = buildList {
            addAll(Restaurant.getVariableRestaurantList())

            if (shouldIncludeFixedRestaurants(date = date, time = time, isPublicHoliday = isPublicHoliday)) {
                add(Restaurant.FOOD_COURT)
                add(Restaurant.SNACK_CORNER)
            }
        }

        val menuDate = date.format(menuDateFormatter)

        val deferredMenus = restaurantsToLoad.map { restaurant ->
            async {
                restaurant to getMenuListUseCase(restaurant, menuDate, time)
            }
        }

        val menuMap = deferredMenus.awaitAll().toMap()

        MenuLoadResult(
            menuMap = menuMap,
            publicHolidayName = holiday?.name,
        )
    }

    private fun shouldIncludeFixedRestaurants(
        date: LocalDate,
        time: Time,
        isPublicHoliday: Boolean,
    ): Boolean {
        if (time != Time.LUNCH) return false

        val dayOfWeek = date.dayOfWeek
        val isWeekday = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
        if (!isWeekday) return false

        // Core: weekday + lunch + not a public holiday
        return !isPublicHoliday
    }
}
