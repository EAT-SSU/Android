@file:OptIn(ExperimentalMaterial3Api::class)

package com.eatssu.android.presentation.cafeteria.review.list


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.design_system.theme.EatssuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewBottomSheet(
    onDismissRequest: () -> Unit,
    onModify: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp), // root padding
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "리뷰 설정",
                style = MaterialTheme.typography.titleMedium, // XML: Subtitle2 대응
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 22.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button, onClick = onModify)
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pencil),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "수정하기",
                    style = MaterialTheme.typography.bodyLarge, // XML: Body1 대응
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button, onClick = onDelete)
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "삭제하기",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewActionsSheetPreview() {
    EatssuTheme {
        Surface {
            MyReviewBottomSheet(onDismissRequest = {}, onModify = {}, onDelete = {})
        }
    }
}

@Preview(
    showBackground = true,
    name = "Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ReviewActionsSheetDarkPreview() {
    EatssuTheme {
        Surface {
            MyReviewBottomSheet(onDismissRequest = {}, onModify = {}, onDelete = {})
        }
    }
}
