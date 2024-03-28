package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
) {
    suspend operator fun invoke() {

        MySharedPreferences.clearUser(App.appContext)

    }
}