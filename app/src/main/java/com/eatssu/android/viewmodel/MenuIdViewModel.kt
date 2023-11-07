package com.eatssu.android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MenuIdViewModel : ViewModel()  {
    private val menu = MutableLiveData<String>()

    fun setData(dataToSend: String) {
        menu.value = dataToSend
        Log.d("viewmodel", dataToSend)
    }

    fun getData(): LiveData<String> {
        Log.d("viewmodel", menu.toString())
        return menu
    }
}