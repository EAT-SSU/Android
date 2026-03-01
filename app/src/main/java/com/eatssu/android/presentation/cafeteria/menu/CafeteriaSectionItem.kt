package com.eatssu.android.presentation.cafeteria.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import com.eatssu.android.R
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.model.Section
import com.eatssu.common.enums.Restaurant
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray200

@Composable
fun CafeteriaSectionItem(
    section: Section,
    onInfoClick: () -> Unit,
    onMenuClick: (Menu) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(section.cafeteria.displayNameResId),
                style = EatssuTheme.typography.subtitle1,
                color = Color.Black,
                modifier = Modifier
                    .weight(5f)
                    .padding(start = 5.dp),
            )
            Row(
                modifier = Modifier.clickable { onInfoClick() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = section.cafeteriaLocation,
                    style = EatssuTheme.typography.caption3,
                    color = Color.Black,
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_info_12),
                    contentDescription = "Info",
                    modifier = Modifier.size(12.dp),
                    tint = Color.Unspecified,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                )
                .border(1.dp, Gray200, RoundedCornerShape(20.dp)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 18.dp, end = 14.dp, bottom = 11.dp),
            ) {
                Text(
                    text = stringResource(R.string.today_menu),
                    style = EatssuTheme.typography.body2,
                    color = Color.Black,
                    modifier = Modifier.weight(7f),
                )
                Text(
                    text = stringResource(R.string.price),
                    style = EatssuTheme.typography.body2,
                    color = Color.Black,
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.rate),
                    style = EatssuTheme.typography.body2,
                    color = Color.Black,
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.Center,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 10.dp),
                thickness = 1.dp,
                color = Gray200,
            )
            Column(
                modifier = Modifier.padding(top = 11.dp, bottom = 18.dp),
            ) {
                section.menuList?.forEach { menu ->
                    MenuItemRow(
                        menu = menu,
                        onClick = { onMenuClick(menu) },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun CafeteriaSectionItemPreview() {
    EatssuTheme {
        CafeteriaSectionItem(
            section = Section(
                menuType = Restaurant.HAKSIK.menuType,
                cafeteria = Restaurant.HAKSIK,
                menuList = listOf(
                    Menu(id = 1L, name = "치킨마요덮밥", price = 5000, rate = 4.1),
                    Menu(id = 2L, name = "된장찌개", price = 4500, rate = 3.8),
                ),
                cafeteriaLocation = "학생회관 1층",
            ),
            onInfoClick = {},
            onMenuClick = {},
        )
    }
}
