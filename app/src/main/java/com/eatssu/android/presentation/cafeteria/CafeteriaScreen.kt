package com.eatssu.android.presentation.cafeteria

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eatssu.android.R
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.android.presentation.cafeteria.menu.MenuScreen
import com.eatssu.android.presentation.cafeteria.menu.MenuViewModel
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.Time
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray100
import com.eatssu.design_system.theme.Gray600
import com.eatssu.design_system.theme.Primary
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CafeteriaRoute(
    infoViewModel: InfoViewModel,
    onNavigateToReview: (menuType: MenuType, itemId: Long, itemName: String) -> Unit,
) {
    LogScreenView(ScreenId.HOME_MAIN)

    CafeteriaScreenContent(
        onMenuPageContent = { page, time, selectedDate ->
            val menuViewModel: MenuViewModel = hiltViewModel(
                key = "menu_page_$page",
            )

            MenuScreen(
                menuViewModel = menuViewModel,
                infoViewModel = infoViewModel,
                time = time,
                selectedDate = selectedDate,
                onNavigateToReview = onNavigateToReview,
            )
        },
    )
}

@Composable
internal fun CafeteriaScreenContent(
    onMenuPageContent: @Composable (page: Int, time: Time, selectedDate: LocalDate) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var currentWeekStart by remember {
        mutableStateOf(getSundayForDate(LocalDate.now()))
    }

    val defaultTab = remember {
        when (LocalTime.now().hour) {
            in 0..9 -> 0
            in 10..15 -> 1
            else -> 2
        }
    }
    val pagerState = rememberPagerState(
        initialPage = defaultTab,
        pageCount = { 3 },
    )
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        stringResource(R.string.widget_morning),
        stringResource(R.string.widget_lunch),
        stringResource(R.string.widget_dinner),
    )
    val times = listOf(Time.MORNING, Time.LUNCH, Time.DINNER)

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(top = 17.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.img_new_logo_primary),
                contentDescription = "EAT-SSU",
                modifier = Modifier.height(28.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    currentWeekStart = currentWeekStart.minusWeeks(1)
                    selectedDate = selectedDate.minusWeeks(1)
                    EventLogger.selectDay(selectedDate.dayOfWeek.name)
                },
                modifier = Modifier.size(18.dp),
            ) {
                Icon(painterResource(R.drawable.ic_arrow_left), "이전 주")
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = CalendarUtil.monthYearFromDate(selectedDate),
                style = EatssuTheme.typography.subtitle1,
                color = Gray600,
            )
            Spacer(Modifier.width(6.dp))
            IconButton(
                onClick = {
                    currentWeekStart = currentWeekStart.plusWeeks(1)
                    selectedDate = selectedDate.plusWeeks(1)
                    EventLogger.selectDay(selectedDate.dayOfWeek.name)
                },
                modifier = Modifier.size(18.dp),
            ) {
                Icon(painterResource(R.drawable.ic_arrow_right), "다음 주")
            }
        }

        Spacer(Modifier.height(8.dp))
        WeekCalendar(
            weekStart = currentWeekStart,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                currentWeekStart = getSundayForDate(date)
                EventLogger.selectDay(date.dayOfWeek.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                )
                .padding(bottom = 10.dp),
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Primary,
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        EventLogger.selectMealTime(times[index])
                    },
                    text = {
                        Text(title, style = EatssuTheme.typography.subtitle2)
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Gray100),
        ) { page ->
            onMenuPageContent(page, times[page], selectedDate)
        }
    }
}

@ThemePreviews
@Composable
private fun CafeteriaScreenContentPreview() {
    EatssuTheme {
        CafeteriaScreenContent(
            onMenuPageContent = { _, time, _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Gray100),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${time.name} menu preview",
                        style = EatssuTheme.typography.subtitle1,
                        color = Gray600,
                    )
                }
            },
        )
    }
}

private fun getSundayForDate(date: LocalDate): LocalDate {
    var current = date
    val oneWeekAgo = current.minusWeeks(1)
    while (current.isAfter(oneWeekAgo)) {
        if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
        current = current.minusDays(1)
    }
    return date // fallback (should not reach)
}
