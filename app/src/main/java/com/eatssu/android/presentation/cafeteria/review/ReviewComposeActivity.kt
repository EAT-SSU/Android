package com.eatssu.android.presentation.cafeteria.review

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.MenuType
import com.eatssu.design_system.theme.EatssuTheme
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReviewComposeActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private var menuType: String? = null
    private var itemId by Delegates.notNull<Long>()
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntents() // 컴포즈 화면 그리기 전에 호출

        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    val navHostController = rememberNavController()
                    val parsedMenuType = MenuType.entries.find { it.name == menuType }

                    parsedMenuType?.let { type ->
                        ReviewNav(
                            navHostController = navHostController,
                            menuType = type,
                            menuName = itemName,
                            id = itemId,
                            onExit = { finish() }
                        )
                    } ?: run {
                        Timber.e("Invalid or null MenuType received: $menuType")
                        ErrorScreen(
                            onBackClick = { finish() }
                        )
                    }
                }
            }
        }
    }

    private fun getIntents() { //todo 추후 변경
        menuType = intent.getStringExtra("menuType")
        itemId = intent.getLongExtra("itemId", 0)
        itemName = intent.getStringExtra("itemName").toString().replace(Regex("[\\[\\]]"), "")

        Timber.d("메뉴는 $itemName $menuType $itemId")
    }

    @Composable
    private fun ErrorScreen(onBackClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "메뉴 정보를 불러오는데 실패했습니다.\n다시 시도해주세요.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text(text = "뒤로가기")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun ErrorScreenPreview() {
        EatssuTheme {
            ErrorScreen(onBackClick = {})
        }
    }
}
