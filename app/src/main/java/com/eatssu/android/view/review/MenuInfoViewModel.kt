package com.eatssu.android.view.review



import androidx.lifecycle.ViewModel

class MenuInfoViewModel : ViewModel() {

    var value1: String = ""
    var value2: String = ""

    companion object {
        private var instance: MenuInfoViewModel? = null

        fun getInstance(): MenuInfoViewModel {
            return instance ?: synchronized(this) {
                instance ?: MenuInfoViewModel().also { instance = it }
            }
        }
    }
}
