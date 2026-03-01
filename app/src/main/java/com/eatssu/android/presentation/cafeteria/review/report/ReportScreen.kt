package com.eatssu.android.presentation.cafeteria.review.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eatssu.android.R
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.common.enums.ReportType
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.EatSsuButton
import com.eatssu.design_system.component.EatSsuRadioButton
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.component.EatssuTextField
import com.eatssu.design_system.component.EatssuToastHost
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray600

@Composable
fun ReportRoute(
    viewModel: ReportViewModel = hiltViewModel(),
    reviewId: Long,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    var selectedReportType by remember { mutableStateOf<ReportType?>(null) }
    var inputText by remember { mutableStateOf("") }

    LogScreenView(ScreenId.REVIEW_REPORT)

    LaunchedEffect(uiState) {
        val state = uiState
        if (state.toastMessage != com.eatssu.common.UiText.Empty) {
            toastType = if (state.isDone) ToastType.SUCCESS else ToastType.ERROR
            snackbarHostState.showSnackbar(state.toastMessage.asString(context))
        }
        if (state.isDone) {
            onBack()
        }
    }

    ReportContent(
        selectedReportType = selectedReportType,
        inputText = inputText,
        onReportTypeSelected = { selectedReportType = it },
        onInputTextChanged = { inputText = it },
        onSubmit = {
            val type = selectedReportType ?: return@ReportContent
            val content = if (type == ReportType.EXTRA) {
                inputText
            } else {
                context.getString(type.descriptionResId)
            }
            viewModel.postData(reviewId, type.toString(), content)
        },
        isSubmitEnabled = selectedReportType != null,
        snackbarHostState = snackbarHostState,
        toastType = toastType,
        onBack = onBack,
    )
}

@Composable
private fun ReportContent(
    selectedReportType: ReportType?,
    inputText: String,
    onReportTypeSelected: (ReportType) -> Unit,
    onInputTextChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    isSubmitEnabled: Boolean,
    snackbarHostState: SnackbarHostState,
    toastType: ToastType,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            EatSsuTopBar(
                title = stringResource(R.string.button_report),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.report_title),
                style = EatssuTheme.typography.subtitle1,
                color = Color.Black,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.report_sub),
                style = EatssuTheme.typography.caption2,
                color = Gray600,
            )
            Spacer(Modifier.height(20.dp))

            ReportType.entries.forEach { reportType ->
                EatSsuRadioButton(
                    text = stringResource(reportType.descriptionResId),
                    isSelected = selectedReportType == reportType,
                    onSelect = { onReportTypeSelected(reportType) },
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

            EatssuTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                hint = stringResource(R.string.report_write_hint),
                maxLength = 150,
                maxLines = 10,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
            )

            Text(
                text = stringResource(R.string.max_150),
                style = EatssuTheme.typography.caption2,
                color = Gray600,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )

            Spacer(Modifier.height(28.dp))

            EatSsuButton(
                text = stringResource(R.string.button_report),
                onClick = onSubmit,
                enabled = isSubmitEnabled,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
private fun ReportContentPreview() {
    EatssuTheme {
        ReportContent(
            selectedReportType = ReportType.IMPROPER_CONTENT,
            inputText = "",
            onReportTypeSelected = {},
            onInputTextChanged = {},
            onSubmit = {},
            isSubmitEnabled = true,
            snackbarHostState = remember { SnackbarHostState() },
            toastType = ToastType.INFO,
            onBack = {},
        )
    }
}
