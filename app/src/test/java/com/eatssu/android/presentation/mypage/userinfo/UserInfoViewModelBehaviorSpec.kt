package com.eatssu.android.presentation.mypage.userinfo

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.NicknameValidationResult
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserNicknameUseCase
import com.eatssu.android.domain.usecase.user.ValidateNicknameLocalUseCase
import com.eatssu.android.domain.usecase.user.ValidateNicknameServerUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.asStringResIdOrNull
import com.eatssu.android.test.expectToast
import com.eatssu.android.test.sampleUserInfo
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class UserInfoViewModelBehaviorSpec : AppBehaviorSpec({

    given("유저 정보 수정 화면") {
        val baseCollege = College(collegeId = 1, collegeName = "IT")
        val baseDepartment = Department(departmentId = 11, departmentName = "컴퓨터학부")
        val otherCollege = College(collegeId = 2, collegeName = "경영")
        val otherDepartment = Department(departmentId = 21, departmentName = "경영학과")

        `when`("초기화되면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege, otherCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("닉네임/단과대/학과와 목록을 로드한 Success 상태가 된다") {
                runTest {
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.nickname shouldBe "oldNick"
                        state.data.selectedCollege shouldBe baseCollege
                        state.data.selectedDepartment shouldBe baseDepartment
                        state.data.collegeList.size shouldBe 2
                        state.data.departmentList shouldBe listOf(baseDepartment)
                    }
                }
            }
        }

        `when`("닉네임을 변경하고 로컬 검증에 실패하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every {
                validateNicknameLocalUseCase("x", UserInfoViewModel.MIN_NICKNAME_LENGTH, UserInfoViewModel.MAX_NICKNAME_LENGTH)
            } returns NicknameValidationResult.Invalid(UiText.StringResource(R.string.nickname_error_length))

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("검증 에러를 표시하고 중복확인 상태를 초기화한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("x")
                    val state = viewModel.uiState.value as UiState.Success

                    state.data.nickname shouldBe "x"
                    state.data.isNicknameChanged shouldBe true
                    state.data.isDuplicationChecked shouldBe false
                    state.data.nicknameValidationError.asStringResIdOrNull() shouldBe R.string.nickname_error_length
                }
            }
        }

        `when`("닉네임 중복확인 서버 호출이 실패하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { validateNicknameServerUseCase("newNick") } returns Result.failure(IllegalArgumentException("dup"))

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("서버 에러 메시지를 validationError에 반영한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.checkNicknameDuplication()

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        (state.data.nicknameValidationError as UiText.DynamicString).value shouldBe "dup"
                        state.data.isDuplicationChecked shouldBe false
                    }
                }
            }
        }

        `when`("변경사항 없이 저장하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("no changes 토스트를 보내고 완료 플래그를 true로 만든다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.uiEvent.test {
                        viewModel.saveUserInfo()
                        expectToast(R.string.toast_no_changes, ToastType.INFO)

                        eventually(2.seconds) {
                            val state = viewModel.uiState.value as UiState.Success
                            state.data.isDone shouldBe true
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("닉네임 변경 저장이 실패하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { setUserNicknameUseCase("newNick") } returns Result.failure(IllegalStateException("fail"))

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("실패 토스트를 보내고 Error 상태가 된다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.uiEvent.test {
                        viewModel.saveUserInfo()
                        expectToast(R.string.toast_nickname_change_failed, ToastType.ERROR)
                        eventually(2.seconds) {
                            viewModel.uiState.value shouldBe UiState.Error
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("닉네임과 학과를 모두 바꿔 저장하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege, otherCollege)
            coEvery { userRepository.getTotalDepartments(any()) } answers {
                when (firstArg<Int>()) {
                    baseCollege.collegeId -> listOf(baseDepartment)
                    otherCollege.collegeId -> listOf(otherDepartment)
                    else -> emptyList()
                }
            }
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { validateNicknameServerUseCase("newNick") } returns Result.success(Unit)
            coEvery { setUserNicknameUseCase("newNick") } returns Result.success(Unit)
            coEvery { userRepository.setUserDepartment(otherDepartment.departmentId) } returns true
            coEvery { setUserCollegeDepartmentUseCase(otherCollege, otherDepartment) } returns Unit

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("통합 수정 성공 토스트를 보내고 완료 플래그를 true로 만든다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.checkNicknameDuplication()
                    viewModel.selectCollege(otherCollege)
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.departmentList shouldBe listOf(otherDepartment)
                    }
                    viewModel.selectDepartment(otherDepartment)

                    viewModel.uiEvent.test {
                        viewModel.saveUserInfo()
                        expectToast(R.string.toast_info_updated, ToastType.INFO)

                        eventually(2.seconds) {
                            val state = viewModel.uiState.value as UiState.Success
                            state.data.isDone shouldBe true
                        }
                        coVerify { setUserCollegeDepartmentUseCase(otherCollege, otherDepartment) }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("닉네임 중복확인이 성공하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { validateNicknameServerUseCase("newNick") } returns Result.success(Unit)

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("중복확인 완료 상태가 true가 된다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.checkNicknameDuplication()

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.isDuplicationChecked shouldBe true
                        state.data.nicknameValidationError shouldBe null
                    }
                }
            }
        }

        `when`("닉네임 중복확인 실패 메시지가 null이면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { validateNicknameServerUseCase("newNick") } returns Result.failure(IllegalStateException())

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("기본 닉네임 오류 리소스를 사용한다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.checkNicknameDuplication()

                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.nicknameValidationError.asStringResIdOrNull() shouldBe R.string.nickname_error_invalid
                    }
                }
            }
        }

        `when`("닉네임만 바꿔 저장하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { validateNicknameServerUseCase("newNick") } returns Result.success(Unit)
            coEvery { setUserNicknameUseCase("newNick") } returns Result.success(Unit)

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("닉네임 수정 토스트를 보낸다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.onNicknameChanged("newNick")
                    viewModel.checkNicknameDuplication()

                    viewModel.uiEvent.test {
                        viewModel.saveUserInfo()
                        expectToast(R.string.toast_nickname_changed, ToastType.INFO)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("학과만 바꿔 저장하면") {
            val setUserNicknameUseCase = mockk<SetUserNicknameUseCase>()
            val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
            val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
            val validateNicknameServerUseCase = mockk<ValidateNicknameServerUseCase>()
            val validateNicknameLocalUseCase = mockk<ValidateNicknameLocalUseCase>()
            val userRepository = mockk<UserRepository>()

            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "oldNick",
                college = baseCollege,
                department = baseDepartment,
            )
            coEvery { userRepository.getTotalColleges() } returns listOf(baseCollege, otherCollege)
            coEvery { userRepository.getTotalDepartments(otherCollege.collegeId) } returns listOf(otherDepartment)
            coEvery { userRepository.getTotalDepartments(baseCollege.collegeId) } returns listOf(baseDepartment)
            every { validateNicknameLocalUseCase(any(), any(), any()) } returns NicknameValidationResult.Valid
            coEvery { userRepository.setUserDepartment(otherDepartment.departmentId) } returns true
            coEvery { setUserCollegeDepartmentUseCase(otherCollege, otherDepartment) } returns Unit

            val viewModel = UserInfoViewModel(
                setUserNicknameUseCase = setUserNicknameUseCase,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                validateNicknameServerUseCase = validateNicknameServerUseCase,
                validateNicknameLocalUseCase = validateNicknameLocalUseCase,
                userRepository = userRepository,
            )

            then("학과 수정 토스트를 보낸다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value is UiState.Success) shouldBe true
                    }

                    viewModel.selectCollege(otherCollege)
                    eventually(2.seconds) {
                        val state = viewModel.uiState.value as UiState.Success
                        state.data.departmentList shouldBe listOf(otherDepartment)
                    }
                    viewModel.selectDepartment(otherDepartment)

                    viewModel.uiEvent.test {
                        viewModel.saveUserInfo()
                        expectToast(R.string.toast_department_updated, ToastType.INFO)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }

    given("UserInfoData 버튼 활성화 규칙") {
        val baseCollege = College(collegeId = 1, collegeName = "IT")
        val baseDepartment = Department(departmentId = 11, departmentName = "컴퓨터학부")

        `when`("닉네임이 변경됐고 로컬 검증 오류가 없으며 중복확인을 안 했으면") {
            then("중복확인 버튼이 활성화된다") {
                UserInfoData(
                    nickname = "new",
                    originalNickname = "old",
                    isNicknameChanged = true,
                    nicknameValidationError = null,
                    isDuplicationChecked = false,
                ).canCheckDuplication shouldBe true
            }
        }

        `when`("닉네임 변경 + 중복확인 완료 + 검증 오류 없음이면") {
            then("저장 버튼이 활성화된다") {
                UserInfoData(
                    nickname = "new",
                    originalNickname = "old",
                    isNicknameChanged = true,
                    nicknameValidationError = null,
                    isDuplicationChecked = true,
                ).canSave shouldBe true
            }
        }

        `when`("닉네임 변경이 있지만 중복확인을 하지 않았으면") {
            then("저장 버튼이 비활성화된다") {
                UserInfoData(
                    nickname = "new",
                    originalNickname = "old",
                    isNicknameChanged = true,
                    nicknameValidationError = null,
                    isDuplicationChecked = false,
                ).canSave shouldBe false
            }
        }

        `when`("학과/단과대만 변경됐고 학과가 선택되어 있으면") {
            then("저장 버튼이 활성화된다") {
                UserInfoData(
                    selectedCollege = baseCollege,
                    originalCollege = baseCollege,
                    isCollegeChanged = true,
                    selectedDepartment = baseDepartment,
                    originalDepartment = baseDepartment,
                    isDepartmentChanged = false,
                ).canSave shouldBe true
            }
        }

        `when`("학과/단과대만 변경됐지만 학과가 비어있으면") {
            then("저장 버튼이 비활성화된다") {
                UserInfoData(
                    selectedCollege = baseCollege,
                    originalCollege = baseCollege,
                    isCollegeChanged = true,
                    selectedDepartment = null,
                    originalDepartment = baseDepartment,
                ).canSave shouldBe false
            }
        }

        `when`("변경사항이 전혀 없으면") {
            then("중복확인/저장 버튼 모두 비활성화된다") {
                val data = UserInfoData(
                    nickname = "same",
                    originalNickname = "same",
                    isNicknameChanged = false,
                    nicknameValidationError = null,
                    isDuplicationChecked = false,
                )

                data.canCheckDuplication shouldBe false
                data.canSave shouldBe false
            }
        }
    }
})
