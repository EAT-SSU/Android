package com.eatssu.android.presentation.cafeteria

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.unit.dp
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary
import java.time.LocalDate

@Composable
fun WeekCalendar(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weekdayNames = listOf("월", "화", "수", "목", "금", "토", "일")
    val days = (0 until 7).map { weekStart.plusDays(it.toLong()) }
    val today = LocalDate.now()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        days.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == today

            Column(
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 2.dp)
                    .clickable { onDateSelected(date) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = weekdayNames[date.dayOfWeek.value - 1],
                    style = EatssuTheme.typography.caption3,
                    color = when {
                        isSelected -> Color.Black
                        isToday -> Primary
                        else -> Color.Black
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .then(
                            if (isSelected) Modifier.background(
                                color = Primary,
                                shape = CircleShape,
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = EatssuTheme.typography.body2,
                        color = when {
                            isSelected -> Color.White
                            isToday -> Primary
                            else -> Color.Black
                        },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun WeekCalendarPreview() {
    EatssuTheme {
        val today = LocalDate.now()
        WeekCalendar(
            weekStart = today.minusDays(today.dayOfWeek.value.toLong() % 7),
            selectedDate = today,
            onDateSelected = {},
        )
    }
}
