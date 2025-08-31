package com.eatssu.android.presentation.cafeteria.review.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReviewComposeActivity : ComponentActivity() {

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()

    private lateinit var itemName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {
                ReviewListScreen(
                    menuType = MenuType.valueOf(menuType),
                    id = itemId
                )
            }
        }
        getIntents()
    }

    private fun getIntents() {
        //get menuId
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Timber.d("메뉴는 $itemName $menuType $itemId")
    }
}