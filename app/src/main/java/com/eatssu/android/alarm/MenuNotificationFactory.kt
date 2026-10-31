package com.eatssu.android.alarm

import android.content.Context
import android.content.res.Configuration
import com.eatssu.android.R
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.android.domain.usecase.alarm.FavoriteMealMatch
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

class MenuNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clock: Clock,
) {
    fun create(
        date: LocalDate,
        matches: List<FavoriteMealMatch>,
        language: AppLanguage
    ): List<MenuNotification> {
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language.code))
        }
        val strings = context.createConfigurationContext(configuration)
        val activeMatches = matches.filter { it.menus.isNotEmpty() }
        if (activeMatches.isEmpty()) return listOf(
            MenuNotification(
                id = "local_${date}_daily",
                title = strings.getString(R.string.notification_context_title),
                body = strings.getString(R.string.notification_context_text),
                receivedAt = clock.millis(),
                source = NotificationSource.LOCAL,
            )
        )
        return activeMatches.map { match ->
            val menus = match.menus
            val first = menus.first()
            val meal =
                strings.getString(if (match.time == Time.DINNER) R.string.widget_dinner else R.string.widget_lunch)
            val title = strings.getString(
                when (menus.size) {
                    1 -> R.string.notification_favorite_one_title
                    2 -> R.string.notification_favorite_two_title
                    else -> R.string.notification_favorite_many_title
                }
            )
            val body = when (menus.size) {
                1 -> strings.getString(
                    R.string.notification_favorite_one_body,
                    meal,
                    restaurantName(strings, first.restaurant),
                    first.menuName
                )

                2 -> strings.getString(
                    R.string.notification_favorite_two_body,
                    meal,
                    restaurantName(strings, first.restaurant),
                    first.menuName,
                    restaurantName(strings, menus[1].restaurant),
                    menus[1].menuName
                )

                else -> strings.getString(
                    R.string.notification_favorite_many_body,
                    meal,
                    restaurantName(strings, first.restaurant),
                    first.menuName,
                    menus.size - 1
                )
            }
            MenuNotification(
                id = "local_${date}_${match.time.value}",
                title = title, body = body, receivedAt = clock.millis(),
                mealDate = date.toString(), mealTime = match.time,
                targetRestaurant = match.targetRestaurant, source = NotificationSource.LOCAL,
            )
        }
    }

    private fun restaurantName(strings: Context, restaurant: Restaurant): String =
        strings.getString(
            when (restaurant) {
                Restaurant.HAKSIK -> R.string.today_favorite_haksik
                Restaurant.DODAM -> R.string.today_favorite_dodam
                Restaurant.DORMITORY -> R.string.today_favorite_dormitory
                Restaurant.FACULTY -> R.string.today_favorite_faculty
                else -> restaurant.displayNameResId
            }
        )
}
