package com.eatssu.android.domain.usecase.user

import android.content.Context
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SetUserCollegeDepartmentUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(college: College, department: Department) {
        MySharedPreferences.setUserCollege(context,college)
        MySharedPreferences.setUserDepartment(context, department)
    }
}