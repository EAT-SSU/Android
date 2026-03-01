package com.eatssu.android.presentation.util

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.eatssu.android.presentation.base.TokenEventBus
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import kotlinx.coroutines.flow.Flow

/**
 * BaseActivity의 token 만료 감지를 Compose로 대체.
 * TokenEventBus.tokenExpired를 lifecycle-aware하게 수집한다.
 */
@Composable
fun ObserveTokenExpiration(onExpired: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        TokenEventBus.tokenExpired
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { _ ->
                onExpired()
            }
    }
}

/**
 * UiEvent (SharedFlow) 수집 헬퍼.
 * ViewModel에서 emit하는 일회성 이벤트를 lifecycle-aware하게 수집한다.
 */
@Composable
fun <T> ObserveUiEvents(
    flow: Flow<T>,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { onEvent(it) }
    }
}

/**
 * 클릭 디바운스 Modifier (MenuSubAdapter의 800ms 방지 패턴).
 * 연속 클릭을 방지하여 중복 네비게이션/요청을 막는다.
 */
fun Modifier.debouncedClickable(
    debounceInterval: Long = 800L,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    this.clickable {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= debounceInterval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * EventLogger 래퍼 (BaseActivity.onResume의 screenView 대체).
 * 화면 진입 시 Firebase Analytics에 screenView 이벤트를 기록한다.
 */
@Composable
fun LogScreenView(screenId: ScreenId) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                EventLogger.screenView(screenId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Preview(showBackground = true)
@Composable
private fun DebouncedClickablePreview() {
    EatssuTheme {
        Box(modifier = Modifier.debouncedClickable { }) {
            Text("Debounced Clickable")
        }
    }
}
