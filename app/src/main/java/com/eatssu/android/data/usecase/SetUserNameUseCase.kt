package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetUserNameUseCase @Inject constructor(
) {
    suspend operator fun invoke(name: String) {
        MySharedPreferences.setUserName(App.appContext, name)

        Log.d("SetUserNameUseCase", name)

    }
}