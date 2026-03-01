package com.eatssu.android.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eatssu.android.R
import com.eatssu.android.presentation.cafeteria.CafeteriaRoute
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.android.presentation.map.MapRoute
import com.eatssu.android.presentation.mypage.MyPageRoute
import com.eatssu.android.presentation.mypage.MyPageViewModel
import com.eatssu.android.presentation.navigation.MainDestination
import com.eatssu.android.presentation.navigation.navigateToMyReview
import com.eatssu.android.presentation.navigation.navigateToSignOut
import com.eatssu.android.presentation.navigation.navigateToWebView
import com.eatssu.android.presentation.util.ObserveTokenExpiration
import com.eatssu.android.presentation.util.ObserveUiEvents
import com.eatssu.common.UiEvent
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.ToastType
import com.eatssu.design_system.component.EatssuToastHost
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Gray300
import com.eatssu.design_system.theme.Primary
import timber.log.Timber

@Composable
fun MainRoute(
    mainViewModel: MainViewModel = hiltViewModel(),
    myPageViewModel: MyPageViewModel = hiltViewModel(),
    infoViewModel: InfoViewModel = hiltViewModel(),
    appNavController: NavHostController,
    onNavigateToUserInfo: (force: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToReview: (menuType: MenuType, itemId: Long, itemName: String) -> Unit,
    onNavigateToInquire: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
    onNavigateToOss: () -> Unit,
) {
    val tabNavController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    ObserveTokenExpiration {
        Timber.e("Token expired")
        onNavigateToLogin()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            MainUiState.NicknameNull -> onNavigateToUserInfo(true)
            MainUiState.LoggedOut -> onNavigateToLogin()
            else -> Unit
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) myPageViewModel.setNotificationOn()
        else myPageViewModel.setNotificationOff()
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ObserveUiEvents(mainViewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowToast -> {
                toastType = event.type
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    val items = remember { mainBottomNavItems() }
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedDestination = currentDestination.toBottomNavDestination()

    MainScreenScaffold(
        snackbarHostState = snackbarHostState,
        toastType = toastType,
        items = items,
        selectedDestination = selectedDestination,
        onDestinationClick = { destination ->
            when (destination) {
                BottomNavDestination.Anyone -> {
                    appNavController.navigateToWebView(
                        url = context.getString(R.string.anyone_but_me_url),
                        title = context.getString(R.string.nav_anyone_but_me),
                        screenId = ScreenId.ANYONE_BUT_ME_MAIN,
                        backIconResId = com.eatssu.design_system.R.drawable.ic_close,
                    )
                }

                BottomNavDestination.Cafeteria -> {
                    tabNavController.navigateToMainDestination(MainDestination.Cafeteria)
                }

                BottomNavDestination.Map -> {
                    tabNavController.navigateToMainDestination(MainDestination.Map)
                }

                BottomNavDestination.MyPage -> {
                    tabNavController.navigateToMainDestination(MainDestination.MyPage)
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = MainDestination.Cafeteria,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable<MainDestination.Cafeteria> {
                CafeteriaRoute(
                    infoViewModel = infoViewModel,
                    onNavigateToReview = onNavigateToReview,
                )
            }

            composable<MainDestination.Map> {
                MapRoute(
                    navigateToUserInfo = { onNavigateToUserInfo(false) },
                )
            }

            composable<MainDestination.MyPage> {
                MyPageRoute(
                    onNavigateToUserInfo = onNavigateToUserInfo,
                    onNavigateToMyReview = {
                        appNavController.navigateToMyReview()
                    },
                    onNavigateToInquire = onNavigateToInquire,
                    onNavigateToWebView = { url, title, screenId ->
                        appNavController.navigateToWebView(
                            url = url,
                            title = title,
                            screenId = screenId,
                        )
                    },
                    onLogout = { mainViewModel.logOut() },
                    onNavigateToSignOut = { nickname ->
                        appNavController.navigateToSignOut(nickname)
                    },
                    onNavigateToDeveloper = onNavigateToDeveloper,
                    onNavigateToOss = onNavigateToOss,
                )
            }
        }
    }
}

@Composable
private fun MainScreenScaffold(
    snackbarHostState: SnackbarHostState,
    toastType: ToastType,
    items: List<BottomNavItem>,
    selectedDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        snackbarHost = {
            EatssuToastHost(
                hostState = snackbarHostState,
                toastType = toastType,
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            ) {
                NavigationBar(
                    modifier = Modifier
                        .heightIn(min = 72.dp)
                        .navigationBarsPadding(),
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    items.forEach { item ->
                        val isSelected = item.destination == selectedDestination

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onDestinationClick(item.destination) },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (isSelected) item.iconSelected else item.iconDefault
                                    ),
                                    contentDescription = stringResource(item.labelResId),
                                    tint = Color.Unspecified,
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.labelResId),
                                    style = EatssuTheme.typography.caption3,
                                    color = LocalContentColor.current,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = Gray300,
                                unselectedTextColor = Gray300,
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        },
        content = content,
    )
}

private data class BottomNavItem(
    val destination: BottomNavDestination,
    val labelResId: Int,
    val iconDefault: Int,
    val iconSelected: Int,
)

private enum class BottomNavDestination {
    Cafeteria,
    Map,
    Anyone,
    MyPage,
}

private fun mainBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            destination = BottomNavDestination.Cafeteria,
            labelResId = R.string.nav_cafeteria_menu,
            iconDefault = R.drawable.ic_cafeteria_menu_default,
            iconSelected = R.drawable.ic_cafeteria_menu_selected,
        ),
        BottomNavItem(
            destination = BottomNavDestination.Map,
            labelResId = R.string.nav_map,
            iconDefault = R.drawable.ic_map_default,
            iconSelected = R.drawable.ic_map_selected,
        ),
        BottomNavItem(
            destination = BottomNavDestination.Anyone,
            labelResId = R.string.nav_anyone_but_me,
            iconDefault = R.drawable.ic_anyone_but_me,
            iconSelected = R.drawable.ic_anyone_but_me,
        ),
        BottomNavItem(
            destination = BottomNavDestination.MyPage,
            labelResId = R.string.nav_mypage,
            iconDefault = R.drawable.ic_mypage_default,
            iconSelected = R.drawable.ic_mypage_selected,
        ),
    )
}

private fun NavDestination?.toBottomNavDestination(): BottomNavDestination {
    return when {
        this?.hierarchy?.any { destination -> destination.hasRoute<MainDestination.Map>() } == true -> {
            BottomNavDestination.Map
        }

        this?.hierarchy?.any { destination -> destination.hasRoute<MainDestination.MyPage>() } == true -> {
            BottomNavDestination.MyPage
        }

        else -> BottomNavDestination.Cafeteria
    }
}

@Preview
@Composable
private fun MainScreenScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    EatssuTheme {
        MainScreenScaffold(
            snackbarHostState = snackbarHostState,
            toastType = ToastType.INFO,
            items = mainBottomNavItems(),
            selectedDestination = BottomNavDestination.Cafeteria,
            onDestinationClick = {},
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Main Screen Preview",
                    style = EatssuTheme.typography.subtitle1,
                    color = Color.Black,
                )
            }
        }
    }
}

private fun <T : Any> NavHostController.navigateToMainDestination(destination: T) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
