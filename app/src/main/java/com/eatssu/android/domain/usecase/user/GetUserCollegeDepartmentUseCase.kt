package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.domain.model.UserInfo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserCollegeDepartmentUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore
) {
    suspend operator fun invoke(): UserInfo {
        val nickname = accountDataStore.name.first()
        val college = accountDataStore.college.first()
        val department = accountDataStore.department.first()
        return UserInfo(nickname, department, college)
    }
}