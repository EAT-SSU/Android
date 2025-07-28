package com.eatssu.android.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Gray400
import com.eatssu.android.presentation.compose.ui.theme.Primary
import com.eatssu.android.presentation.compose.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentBottomSheet(
    onDismiss: () -> Unit = {},
    onInputClick: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(30.dp)
                    .height(2.dp)
                    .background(
                        color = Gray400,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.Input_string_description),
                style = EatssuTheme.typography.h2,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 28.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onInputClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, bottom = 40.dp)
                    .height(52.dp)

            ) {
                Text(stringResource(R.string.inpur_department), color = White, style = EatssuTheme.typography.button1)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DepartmentBottomSheetPreview() {
    EatssuTheme {
        Surface {
            DepartmentBottomSheet(
                onDismiss = {},
                onInputClick = {}
            )
        }
    }
}

