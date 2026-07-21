package com.eatssu.android.presentation.cafeteria.review.list.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.design_system.component.Chip
import com.eatssu.design_system.component.RatingBarSmall
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Gray400
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Info
import com.eatssu.design_system.theme.White

@Composable
fun ReviewItem(
    writeName: String,
    writeDate: String,
    content: String,
    rating: Int,
    modifier: Modifier = Modifier,
    menuLikeInfoList: List<Review.MenuLikeInfo>? = null,
    imgUrl: String? = null,
    translatedContent: String? = null,
    isTranslationVisible: Boolean = false,
    isTranslationLoading: Boolean = false,
    isTranslationUnavailable: Boolean = false,
    isParentScrolling: Boolean = false,
    showTranslationAction: Boolean = false,
    onTranslationClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    var showTranslationTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(isParentScrolling) {
        if (isParentScrolling) showTranslationTooltip = false
    }

    Column(modifier = modifier.padding(vertical = 10.dp)) {
        ReviewHeader(
            writeName = writeName,
            writeDate = writeDate,
            rating = rating,
            onMoreClick = onMoreClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!menuLikeInfoList.isNullOrEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                menuLikeInfoList.forEach { menuLikeInfo ->
                    Chip(
                        menuName = menuLikeInfo.name,
                        modifier = Modifier.height(26.dp),
                        isLike = menuLikeInfo.isLike,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = if (isTranslationVisible && translatedContent != null) {
                translatedContent
            } else {
                content
            },
            style = EatssuTheme.typography.body3,
        )

        if (showTranslationAction && content.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))

            when {
                isTranslationUnavailable -> TranslationStatusRow(
                    label = stringResource(R.string.review_translation_unavailable),
                    tooltipText = stringResource(R.string.review_translation_unavailable_notice),
                    showTooltip = showTranslationTooltip,
                    onTooltipClick = { showTranslationTooltip = true },
                    onTooltipDismiss = { showTranslationTooltip = false },
                )

                isTranslationVisible && translatedContent != null -> TranslationStatusRow(
                    label = stringResource(R.string.review_translated_by_ai),
                    actionLabel = stringResource(R.string.review_show_original),
                    tooltipText = stringResource(R.string.review_translation_accuracy_notice),
                    showTooltip = showTranslationTooltip,
                    onActionClick = onTranslationClick,
                    onTooltipClick = { showTranslationTooltip = true },
                    onTooltipDismiss = { showTranslationTooltip = false },
                )

                else -> TranslationActionText(
                    text = when {
                        isTranslationLoading -> stringResource(R.string.review_translating)
                        translatedContent != null -> stringResource(R.string.review_show_translation)
                        else -> stringResource(R.string.review_translate)
                    },
                    enabled = !isTranslationLoading,
                    onClick = onTranslationClick,
                )
            }
        }

        if (!imgUrl.isNullOrBlank() && imgUrl != "null") {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = imgUrl,
                contentDescription = stringResource(R.string.review_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun ReviewHeader(
    writeName: String,
    writeDate: String,
    rating: Int,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(com.eatssu.design_system.R.drawable.ic_profile_24),
            contentDescription = stringResource(R.string.review_profile_image),
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = writeName,
                style = EatssuTheme.typography.caption2,
            )
            Spacer(modifier = Modifier.height(2.dp))
            RatingBarSmall(rating = rating)
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(
                        onClick = onMoreClick,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ),
                contentAlignment = Alignment.TopEnd,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu_12),
                    contentDescription = stringResource(R.string.review_more),
                    modifier = Modifier.size(12.dp),
                    tint = Color.Unspecified,
                )
            }
            Text(
                text = writeDate,
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
        }
    }
}

@Composable
private fun TranslationActionText(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = EatssuTheme.typography.caption2,
        color = Info,
        modifier = Modifier.clickable(
            enabled = enabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
        ),
    )
}

@Composable
private fun TranslationStatusRow(
    label: String,
    tooltipText: String,
    showTooltip: Boolean,
    onTooltipClick: () -> Unit,
    onTooltipDismiss: () -> Unit,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = EatssuTheme.typography.caption2,
            color = Gray400,
        )

        if (actionLabel != null) {
            Text(
                text = " · ",
                style = EatssuTheme.typography.caption2,
                color = Gray400,
            )
            TranslationActionText(
                text = actionLabel,
                enabled = true,
                onClick = onActionClick,
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Box {
            Icon(
                painter = painterResource(R.drawable.ic_info_12),
                contentDescription = stringResource(R.string.review_translation_information),
                tint = Gray400,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onTooltipClick,
                    )
                    .padding(2.dp),
            )

            DropdownMenu(
                expanded = showTooltip,
                onDismissRequest = onTooltipDismiss,
                offset = DpOffset(x = (-220).dp, y = (-72).dp),
                shape = RoundedCornerShape(2.dp),
                containerColor = White,
                border = BorderStroke(1.dp, Gray300),
                shadowElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp),
            ) {
                Text(
                    text = tooltipText,
                    style = EatssuTheme.typography.caption2,
                    color = Gray600,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
        }
    }
}

private val previewMenus = listOf(
    Review.MenuLikeInfo(menuId = 1L, name = "chips", isLike = true),
    Review.MenuLikeInfo(menuId = 2L, name = "chips", isLike = false),
)

@Preview(showBackground = true)
@Composable
private fun ReviewTranslationStatesPreview() {
    EatssuTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ReviewItem(
                writeName = "nickname",
                writeDate = "2023.03.03",
                content = "리뷰 내용 리뷰 내용 리뷰 내용",
                rating = 4,
                menuLikeInfoList = previewMenus,
                showTranslationAction = true,
            )
            ReviewItem(
                writeName = "nickname",
                writeDate = "2023.03.03",
                content = "리뷰 내용 리뷰 내용 리뷰 내용",
                translatedContent = "Translated review content",
                isTranslationVisible = true,
                rating = 4,
                menuLikeInfoList = previewMenus,
                showTranslationAction = true,
            )
            ReviewItem(
                writeName = "nickname",
                writeDate = "2023.03.03",
                content = "리뷰 내용 리뷰 내용 리뷰 내용",
                isTranslationUnavailable = true,
                rating = 4,
                menuLikeInfoList = previewMenus,
                showTranslationAction = true,
            )
        }
    }
}
