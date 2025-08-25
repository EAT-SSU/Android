package com.eatssu.android.domain.usecase.user

import android.content.Context
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.model.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetUserCollegeDepartmentUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): UserInfo {
        val nickname = MySharedPreferences.getUserName(context)
        val collegeId = MySharedPreferences.getUserCollegeId(context)
        val collegeName = MySharedPreferences.getUserCollegeName(context)
        val departmentId = MySharedPreferences.getUserDepartmentId(context)
        val departmentName = MySharedPreferences.getUserDepartmentName(context)
        return UserInfo(nickname, Department(departmentId, departmentName), College(collegeId, collegeName))
    }
}