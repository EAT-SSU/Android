package com.eatssu.android.presentation.widget.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import com.google.firebase.annotations.PreviewApi

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // 기본 스레드에서 실행되기 떄문에
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            Text("Hello World")
        }
    }
}


@Composable
@PreviewApi
fun PreviewWidget() {
    Text("Hello World")

}