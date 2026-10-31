package com.eatssu.android.presentation.favorite

import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import java.text.Collator
import java.util.Locale

internal fun sortMenuFavoriteSearchResults(
    menus: List<MenuFavoriteSearchResult>,
    keyword: String,
): List<MenuFavoriteSearchResult> {
    val collator = Collator.getInstance(Locale.KOREAN)
    return menus.sortedWith(
        compareBy<MenuFavoriteSearchResult> { !it.menuName.startsWith(keyword, ignoreCase = true) }
            .thenComparator { left, right -> collator.compare(left.menuName, right.menuName) }
            .thenBy { it.menuId },
    )
}
