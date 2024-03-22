package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetUserEmailUseCase @Inject constructor(
) {
    suspend operator fun invoke(email: String) {
        MySharedPreferences.setUserEmail(App.appContext, email)

        Log.d("SetUserEmailUseCase", "email")

    }
}