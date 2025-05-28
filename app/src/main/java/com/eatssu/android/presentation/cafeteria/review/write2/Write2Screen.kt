package com.eatssu.android.presentation.cafeteria.review.write2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eatssu.android.R
import com.eatssu.android.presentation.cafeteria.review.write.ReviewWriteViewModel

@Composable
fun Write2Screen(
    modifier: Modifier = Modifier,
    viewModel: ReviewWriteViewModel = hiltViewModel(),
) {

    var menu by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf(TextFieldValue()) }
    var mainRating by remember { mutableStateOf(0f) }
    var tasteRating by remember { mutableStateOf(0f) }
    var amountRating by remember { mutableStateOf(0f) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("'", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(menu, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    stringResource(R.string.recomented),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            RatingBar(rating = mainRating, onRatingChanged = { mainRating = it })

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "해당 메뉴에 대한 상세한 평가를 남겨주세요",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 40.dp)) {
                RatingRow(
                    label = stringResource(R.string.taste),
                    rating = tasteRating,
                    onRatingChanged = { tasteRating = it })
                RatingRow(
                    label = stringResource(R.string.amount),
                    rating = amountRating,
                    onRatingChanged = { amountRating = it })
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                placeholder = { Text(text = stringResource(R.string.write_text_review)) },
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.max_300),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Top) {
                IconButton(onClick = { /* 이미지 추가 */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_pic),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
                imageUri?.let {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 10.dp)
                    )
                    IconButton(onClick = { imageUri = null }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.pic_not_patch),
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(30.dp))
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = { /* 리뷰 제출 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.review_done))
            }
        }
    }
}

@Composable
fun RatingRow(label: String, rating: Float, onRatingChanged: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        RatingBar(rating = rating, onRatingChanged = onRatingChanged)
    }
}

@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Slider(
        value = rating,
        onValueChange = onRatingChanged,
        valueRange = 0f..5f,
        steps = 4,
        modifier = Modifier.width(150.dp)
    )
}


@Composable
@Preview(name = "Write2")
private fun Write2ScreenPreview() {
    Write2Screen(
//        state = Write2State(),
//        actions = Write2Actions()
    )
}

