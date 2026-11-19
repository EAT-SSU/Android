package com.eatssu.android.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.remote.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepository) :
    ViewModel() {
    val notifications = repository.notifications
    val loading = repository.loading
    val error = repository.error
    val actionFailed = MutableStateFlow(false)

    init {
        refresh()
    }

    fun refresh() { viewModelScope.launch { repository.refresh() } }

    fun markAllRead() {
        viewModelScope.launch { actionFailed.value = !repository.markAllRead() }
    }

    suspend fun markRead(id: String): Boolean = repository.markRead(id)
}
