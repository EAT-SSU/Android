package com.eatssu.android.test

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherListener(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestListener {
    override suspend fun beforeTest(testCase: TestCase) {
        Dispatchers.setMain(dispatcher)
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        unmockkAll()
        Dispatchers.resetMain()
    }
}
