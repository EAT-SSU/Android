package com.eatssu.android.presentation.cafeteria

import androidx.lifecycle.ViewModel
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

data class CafeteriaScrollTarget(
    val id: Long,
    val date: LocalDate,
    val time: Time,
    val restaurant: Restaurant
)

@HiltViewModel
class CafeteriaScrollViewModel @Inject constructor() : ViewModel() {
    private var requestId = 0L
    private val _target = MutableStateFlow<CafeteriaScrollTarget?>(null)
    val target = _target.asStateFlow()

    fun request(date: LocalDate, time: Time, restaurant: Restaurant?) {
        _target.value = restaurant?.let { CafeteriaScrollTarget(++requestId, date, time, it) }
    }

    fun consume(id: Long) {
        if (_target.value?.id == id) _target.value = null
    }

    fun clear() {
        _target.value = null
    }
}
