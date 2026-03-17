package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserEmailUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore,
) {
    suspend operator fun invoke(): String = accountDataStore.email.first()
}
