package com.eatssu.android.presentation.cafeteria.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.usecase.holiday.GetPublicHolidayOfDateUseCase
import com.eatssu.android.domain.usecase.menu.GetMenuListUseCase
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getMenuListUseCase: GetMenuListUseCase,
    private val getPublicHolidayOfDateUseCase: GetPublicHolidayOfDateUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MenuState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MenuState>> = _uiState.asStateFlow()

    private val menuDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    fun loadMenus(date: LocalDate, time: Time) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
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
            _uiState.value = UiState.Success(
                MenuState(
                    menuMap = menuMap,
                    publicHolidayName = holiday?.name,
                )
            )
        }
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

        // 핵심: 주중 + 점심 + 공휴일 아님
        return !isPublicHoliday
    }
}

data class MenuState(
    val menuMap: Map<Restaurant, List<Menu>> = emptyMap(),
    val publicHolidayName: String? = null,
)
