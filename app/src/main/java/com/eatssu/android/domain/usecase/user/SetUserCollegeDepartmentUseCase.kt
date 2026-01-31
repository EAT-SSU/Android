package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import javax.inject.Inject

class SetUserCollegeDepartmentUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore
) {
    suspend operator fun invoke(college: College, department: Department) {
        accountDataStore.setCollege(college)
        accountDataStore.setDepartment(department)
    }
}