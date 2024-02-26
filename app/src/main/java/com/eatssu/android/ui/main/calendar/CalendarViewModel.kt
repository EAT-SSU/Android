package com.eatssu.android.ui.main.calendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val data = MutableLiveData<LocalDate>()

    fun setData(dataToSend: LocalDate) {
        if (dataToSend != data.value) {
            data.value = dataToSend
        }

        Log.d("setdata", dataToSend.toString())
    }

    fun getData(): LiveData<LocalDate> {
        return data
    }
}