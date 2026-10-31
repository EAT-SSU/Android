package com.eatssu.android.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.local.NotificationStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val store: NotificationStore) :
    ViewModel() {
    val notifications = store.notifications

    init {
        viewModelScope.launch { store.prune() }
    }

    fun markAllRead() {
        viewModelScope.launch { store.markAllRead() }
    }

    suspend fun markRead(id: String) = store.markRead(id)
}
