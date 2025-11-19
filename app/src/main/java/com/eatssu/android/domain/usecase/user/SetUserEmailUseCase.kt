package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import javax.inject.Inject

class SetUserEmailUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore,
) {
    suspend operator fun invoke(email: String) {
        accountDataStore.setEmail(email)
    }
}