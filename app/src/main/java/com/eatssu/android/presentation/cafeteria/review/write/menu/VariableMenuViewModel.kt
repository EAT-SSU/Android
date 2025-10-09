package com.eatssu.android.presentation.cafeteria.review.write.menu


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.toMenuMini
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.usecase.menu.GetMenuNameListOfMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class VariableMenuViewModel @Inject constructor(
    private val getMenuNameListUseCase: GetMenuNameListOfMealUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())
    val uiState: StateFlow<MenuState> = _uiState.asStateFlow()

    fun findMenuItemByMealId(mealId: Long) {
        Timber.d("findMenuItemByMealId: $mealId")
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val menuNameList = getMenuNameListUseCase(mealId)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    menuOfMeal = menuNameList.map { it.toMenuMini() })
            }

            Timber.d("findMenuItemByMealId: $menuNameList")
        }
    }
}

data class MenuState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var menuOfMeal: List<MenuMini>? = null,
)