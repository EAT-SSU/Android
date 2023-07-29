package com.eatssu.android.ui.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private var DateText: MutableLiveData<String> = MutableLiveData()

    fun getData(): MutableLiveData<String> = DateText

    fun updateText(Date: String) {
        DateText.value = Date
    }
}
