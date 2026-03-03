package com.eatssu.android.presentation.cafeteria.review

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.presentation.cafeteria.review.ReviewNav
import com.eatssu.common.enums.MenuType
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReviewComposeActivity : ComponentActivity() {

    private var menuType: String? = null
    private var itemId by Delegates.notNull<Long>()
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatssuTheme {
                val navHostController = rememberNavController()

                val parsedMenuType = runCatching {
                    if (menuType.isNullOrBlank()) null else MenuType.valueOf(menuType!!)
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to parse MenuType: \$menuType")
                    FirebaseCrashlytics.getInstance().recordException(
                        IllegalArgumentException("Invalid MenuType '\$menuType' for itemId \$itemId. Original exception: \${exception.message}", exception)
                    )
                }.getOrNull()

                parsedMenuType?.let { type ->
                    ReviewNav(
                        navHostController = navHostController,
                        menuType = type,
                        menuName = itemName,
                        id = itemId,
                        onExit = { finish() }
                    )
                } ?: run {
                    Timber.e("Invalid or null MenuType received: \$menuType")
                    ErrorScreen(
                        onBackClick = { finish() }
                    )
                }
            }
        }
        getIntents()
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