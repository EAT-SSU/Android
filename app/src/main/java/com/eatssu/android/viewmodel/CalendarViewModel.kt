package com.eatssu.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val data = MutableLiveData<String>()

    fun setData(dataToSend: String) {
        data.value = dataToSend
    }

    fun getData(): LiveData<String> {
        return data
    }
}
