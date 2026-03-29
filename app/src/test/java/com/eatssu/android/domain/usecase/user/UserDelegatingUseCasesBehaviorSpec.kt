package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.sampleCollege
import com.eatssu.android.test.sampleDepartment
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UserDelegatingUseCasesBehaviorSpec : AppBehaviorSpec({

    given("GetUserCollegeDepartmentUseCase") {
        val accountDataStore = mockk<AccountDataStore>()
        val useCase = GetUserCollegeDepartmentUseCase(accountDataStore)

        `when`("로컬에 닉네임/단과대/학과가 모두 있으면") {
            every { accountDataStore.name } returns flowOf("eatssu")
            every { accountDataStore.college } returns flowOf(sampleCollege(1, "IT대학"))
            every { accountDataStore.department } returns flowOf(sampleDepartment(2, "컴퓨터학부"))

            then("해당 값을 UserInfo로 반환한다") {
                runTest {
                    val result = useCase()
                    result.nickname shouldBe "eatssu"
                    result.userCollege shouldBe sampleCollege(1, "IT대학")
                    result.userDepartment shouldBe sampleDepartment(2, "컴퓨터학부")
                }
            }
        }

        `when`("단과대/학과가 비어있으면") {
            every { accountDataStore.name } returns flowOf("eatssu")
            every { accountDataStore.college } returns flowOf(null)
            every { accountDataStore.department } returns flowOf(null)

            then("기본 placeholder 값으로 채운다") {
                runTest {
                    val result = useCase()
                    result.userCollege.collegeId shouldBe -1
                    result.userCollege.collegeName shouldBe "단과대"
                    result.userDepartment.departmentId shouldBe -1
                    result.userDepartment.departmentName shouldBe "학과"
                }
            }
        }
    }

    given("GetUserNickNameUseCase") {
        val userRepository = mockk<UserRepository>()
        val accountDataStore = mockk<AccountDataStore>()
        val useCase = GetUserNickNameUseCase(userRepository, accountDataStore)

        `when`("로컬 닉네임이 비어있지 않으면") {
            every { accountDataStore.name } returns flowOf("local-nick")

            then("원격 조회 없이 로컬 닉네임을 반환한다") {
                runTest {
                    useCase() shouldBe "local-nick"
                    coVerify(exactly = 0) { userRepository.getUserNickName() }
                }
            }
        }

        `when`("로컬 닉네임이 비어있으면") {
            every { accountDataStore.name } returns flowOf("")
            coEvery { userRepository.getUserNickName() } returns "remote-nick"
            coJustRun { accountDataStore.setName("remote-nick") }

            then("원격 닉네임을 조회해 저장 후 반환한다") {
                runTest {
                    useCase() shouldBe "remote-nick"
                    coVerifyOrder {
                        userRepository.getUserNickName()
                        accountDataStore.setName("remote-nick")
                    }
                }
            }
        }
    }

    given("SetUserCollegeDepartmentUseCase") {
        val accountDataStore = mockk<AccountDataStore>()
        val useCase = SetUserCollegeDepartmentUseCase(accountDataStore)
        val college = sampleCollege(5, "경영대학")
        val department = sampleDepartment(9, "경영학부")

        coJustRun { accountDataStore.setCollege(college) }
        coJustRun { accountDataStore.setDepartment(department) }

        `when`("단과대/학과를 전달하면") {
            then("둘 다 저장한다") {
                runTest {
                    useCase(college, department)
                    coVerify(exactly = 1) { accountDataStore.setCollege(college) }
                    coVerify(exactly = 1) { accountDataStore.setDepartment(department) }
                }
            }
        }
    }

    given("SetUserEmailUseCase") {
        val accountDataStore = mockk<AccountDataStore>()
        val useCase = SetUserEmailUseCase(accountDataStore)

        coJustRun { accountDataStore.setEmail("a@b.com") }

        `when`("이메일을 전달하면") {
            then("로컬 이메일을 저장한다") {
                runTest {
                    useCase("a@b.com")
                    coVerify(exactly = 1) { accountDataStore.setEmail("a@b.com") }
                }
            }
        }
    }

    given("SetUserNicknameUseCase") {
        val userRepository = mockk<UserRepository>()
        val accountDataStore = mockk<AccountDataStore>()
        val useCase = SetUserNicknameUseCase(userRepository, accountDataStore)
        val nickname = "new-nick"

        coJustRun { accountDataStore.setName(nickname) }

        `when`("원격 닉네임 변경이 성공하면") {
            coEvery { userRepository.updateUserName(nickname) } returns Result.success(Unit)

            then("성공 결과를 반환하고 원격 성공 후 로컬 닉네임을 저장한다") {
                runTest {
                    val result = useCase(nickname)
                    result.isSuccess shouldBe true
                    coVerifyOrder {
                        userRepository.updateUserName(nickname)
                        accountDataStore.setName(nickname)
                    }
                }
            }
        }

        `when`("원격 닉네임 변경이 실패하면") {
            coEvery { userRepository.updateUserName(nickname) } returns Result.failure(
                IllegalStateException("fail")
            )

            then("실패 결과를 반환하고 로컬 닉네임은 변경하지 않는다") {
                runTest {
                    val result = useCase(nickname)
                    result.isFailure shouldBe true
                    coVerify(exactly = 1) { userRepository.updateUserName(nickname) }
                    coVerify(exactly = 0) { accountDataStore.setName(any()) }
                }
            }
        }
    }

    given("ValidateNicknameServerUseCase") {
        val userRepository = mockk<UserRepository>()
        val useCase = ValidateNicknameServerUseCase(userRepository)

        `when`("서버 검증이 성공하면") {
            coEvery { userRepository.checkUserNameValidation("valid-nick") } returns Result.success(Unit)

            then("성공 결과를 그대로 반환한다") {
                runTest {
                    useCase("valid-nick").isSuccess shouldBe true
                }
            }
        }

        `when`("서버 검증이 실패하면") {
            coEvery { userRepository.checkUserNameValidation("bad-nick") } returns Result.failure(IllegalArgumentException("dup"))

            then("실패 결과를 그대로 반환한다") {
                runTest {
                    useCase("bad-nick").isFailure shouldBe true
                }
            }
        }
    }
})
