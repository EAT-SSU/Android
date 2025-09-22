package com.eatssu.android.presentation.cafeteria.review

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.data.enums.MenuType
import com.eatssu.design_system.theme.EatssuTheme
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
                val navHostController = rememberNavController()

                ReviewNav(
                    navHostController = navHostController,
                    menuType = MenuType.valueOf(menuType),
                    menuName = itemName,
                    id = itemId,
                    onExit = { finish() }
                )
            }
        }
        getIntents()
    }

    private fun getIntents() { //todo 추후 변경
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Timber.d("메뉴는 $itemName $menuType $itemId")
    }
}