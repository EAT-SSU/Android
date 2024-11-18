package com.eatssu.android.ui.review.write.menu


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.response.toMenuMiniList
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.usecase.menu.GetMenuNameListOfMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
            getMenuNameListUseCase(
                mealId
            ).onStart {
                Timber.d("findMenuItemByMealId: onStart")

                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                Timber.d("findMenuItemByMealId: onCompletion")

                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                Timber.d("findMenuItemByMealId: catch $e")

                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                    )
                }
            }.collectLatest { result ->
                Timber.d("findMenuItemByMealId: ${result.toString()}")
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                        menuOfMeal = result.result?.toMenuMiniList()
                    )
                }
            }
        }
    }
}

data class MenuState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var menuOfMeal: List<MenuMini>? = null,
)