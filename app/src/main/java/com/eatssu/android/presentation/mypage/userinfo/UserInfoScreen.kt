package com.eatssu.android.presentation.mypage.userinfo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.component.EatssuDropdownField
import com.eatssu.design_system.component.EatssuTextField
import com.eatssu.design_system.component.EatssuToastHost
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600

@Composable
fun UserInfoRoute(
    viewModel: UserInfoViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    LogScreenView(ScreenId.MYPAGE_USERINFO)

    ObserveUiEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> {
                toastType = event.type
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.my_info),
                onBack = onBack,
            )
        },
        snackbarHost = {
            EatssuToastHost(
                hostState = snackbarHostState,
                toastType = toastType,
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is UserInfoData -> {
                LaunchedEffect(state.isDone) {
                    if (state.isDone) onBack()
                }

                UserInfoContent(
                    data = state,
                    onNicknameChanged = viewModel::onNicknameChanged,
                    onCheckDuplication = viewModel::checkNicknameDuplication,
                    onCollegeSelected = { index ->
                        val college = state.collegeList[index]
                        viewModel.selectCollege(college)
                    },
                    onDepartmentSelected = { index ->
                        val department = state.departmentList[index]
                        viewModel.selectDepartment(department)
                    },
                    onSave = viewModel::saveUserInfo,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun UserInfoContent(
    data: UserInfoData,
    onNicknameChanged: (String) -> Unit,
    onCheckDuplication: () -> Unit,
    onCollegeSelected: (Int) -> Unit,
    onDepartmentSelected: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.userinfo_nickname_setting),
                style = EatssuTheme.typography.body2,
                color = Color.Black,
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EatssuTextField(
                    value = data.nickname,
                    onValueChange = onNicknameChanged,
                    hint = stringResource(R.string.set_nickname),
                    isError = data.nicknameValidationError != null,
                    maxLength = UserInfoViewModel.MAX_NICKNAME_LENGTH,
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                )
                Spacer(Modifier.width(5.dp))
                EatSsuButton(
                    text = stringResource(R.string.button_check_duplicate),
                    onClick = onCheckDuplication,
                    enabled = data.canCheckDuplication,
                    fillMaxWidth = false,
                    modifier = Modifier.height(52.dp),
                )
            }

            Spacer(Modifier.height(8.dp))

            val nicknameStatusText: String
            val nicknameStatusColor: Color
            when {
                data.nicknameValidationError != null -> {
                    nicknameStatusText = data.nicknameValidationError.asString(context)
                    nicknameStatusColor = Color(0xFFE53935) // error red
                }

                data.isDuplicationChecked -> {
                    nicknameStatusText = stringResource(R.string.set_nickname_able)
                    nicknameStatusColor = Gray600
                }

                else -> {
                    nicknameStatusText = stringResource(
                        R.string.set_nickname_length,
                        UserInfoViewModel.MIN_NICKNAME_LENGTH,
                        UserInfoViewModel.MAX_NICKNAME_LENGTH,
                    )
                    nicknameStatusColor = Gray600
                }
            }
            Text(
                text = nicknameStatusText,
                style = EatssuTheme.typography.caption2,
                color = nicknameStatusColor,
            )

            Spacer(Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.userinfo_affiliation_setting),
                style = EatssuTheme.typography.body2,
                color = Color.Black,
            )
            Spacer(Modifier.height(8.dp))

            EatssuDropdownField(
                selectedText = data.selectedCollege?.collegeName ?: "",
                options = data.collegeList.map { it.collegeName },
                onOptionSelected = { index, _ -> onCollegeSelected(index) },
                placeholder = "단과대",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))

            EatssuDropdownField(
                selectedText = data.selectedDepartment?.departmentName ?: "",
                options = data.departmentList.map { it.departmentName },
                onOptionSelected = { index, _ -> onDepartmentSelected(index) },
                placeholder = "학과",
                enabled = data.selectedCollege != null,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.connect_account),
                    style = EatssuTheme.typography.body2,
                    color = Color.Black,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.kakao),
                    style = EatssuTheme.typography.body3,
                    color = Color.Black,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_kakao_login),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        EatSsuButton(
            text = stringResource(R.string.button_save),
            onClick = onSave,
            enabled = data.canSave,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
        )
    }
}

@Preview
@Composable
private fun UserInfoContentPreview() {
    val college = College(collegeId = 1, collegeName = "IT대학")
    val department = Department(departmentId = 1, departmentName = "컴퓨터학부")

    EatssuTheme {
        UserInfoContent(
            data = UserInfoData(
                nickname = "잇쑤",
                originalNickname = "잇쑤",
                isDuplicationChecked = true,
                selectedCollege = college,
                originalCollege = college,
                selectedDepartment = department,
                originalDepartment = department,
                collegeList = listOf(college),
                departmentList = listOf(department),
            ),
            onNicknameChanged = {},
            onCheckDuplication = {},
            onCollegeSelected = {},
            onDepartmentSelected = {},
            onSave = {},
        )
    }
}
