package com.eatssu.android.analytics

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsIdentityManager @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
) {

    fun identifyUser(
        email: String,
        nickname: String? = null,
        college: College? = null,
        department: Department? = null,
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) return
        val selectedCollege =
            college?.takeUnless { it.collegeId == -1 || it.collegeName.isBlank() || it.collegeName == "단과대" }
        val selectedDepartment =
            department?.takeUnless {
                it.departmentId == -1 || it.departmentName.isBlank() || it.departmentName == "학과"
            }

        analyticsTracker.identify(
            AnalyticsIdentity(
                distinctId = trimmedEmail,
                email = trimmedEmail,
                nickname = nickname?.trim()?.takeIf(String::isNotBlank),
                collegeId = selectedCollege?.collegeId,
                collegeName = selectedCollege?.collegeName,
                departmentId = selectedDepartment?.departmentId,
                departmentName = selectedDepartment?.departmentName,
            ),
        )
    }

    fun resetIdentity() {
        analyticsTracker.resetIdentity()
    }
}
