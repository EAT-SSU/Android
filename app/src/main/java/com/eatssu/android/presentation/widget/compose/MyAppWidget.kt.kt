package com.eatssu.android.presentation.widget.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.eatssu.android.R

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // 기본 스레드에서 실행되기 떄문에 withContext를 사용해서 다른 스레드로 전환할 수 있음
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            MyContent()

            var isError = false;
            var data = null
            try {
//                val repository = (context.applicationContext as MyApplication).myRepository
//                data = repository.loadData()
            } catch (e: Exception) {
                isError = true;
                //handleError
            }

            if (isError) {
                ErrorView()
            } else {
//                Content(data)
                MyContent()

            }
        }
    }
}


@Composable
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 240)
private fun MyContent() {


    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(GlanceTheme.colors.background)
//            .background(color = Color(0xFFD3D3D3)) //0xFFFAFAFB
            .padding(16.dp)
            .cornerRadius(20.dp),
    ) {

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
//            verticalAlignment = Alignment.Top,
//            horizontalAlignment = Alignment.Start
        ) {
            // 조식/중식/석식
            Column(
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                    provider = ImageProvider(R.drawable.img_new_logo_primary),
                    contentDescription = "Logo"
                )

                Text(
                    "중식", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                    modifier = GlanceModifier.padding(top = 2.dp, bottom = 12.dp)
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            //식당 이름
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
            ) {
                Image(
                    modifier = GlanceModifier.size(18.dp),
                    provider = ImageProvider(R.drawable.ic_arrow_left),
                    contentDescription = "left"
                )
                Text(
                    "기숙사 식당",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                )
                Image(
                    modifier = GlanceModifier.size(18.dp),
                    provider = ImageProvider(R.drawable.ic_arrow_right),
                    contentDescription = "right"
                )
            }

        }


        MenuList()

    }


}

@Composable
fun MenuList() {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

    Box(
        modifier = GlanceModifier.fillMaxSize()
            .background(color = Color(0xFFD3D3D3)) //0xFFFAFAFB
            .padding(top = 5.dp, start = 14.dp, end = 5.dp)
            .cornerRadius(10.dp)
    )
    {
        Column(
            modifier = GlanceModifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = GlanceModifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}


@Composable
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 240)
fun ErrorView() {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(GlanceTheme.colors.background)
//            .background(color = Color(0xFFD3D3D3)) //0xFFFAFAFB
            .padding(16.dp)
            .cornerRadius(20.dp),
    ) {

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
//            verticalAlignment = Alignment.Top,
//            horizontalAlignment = Alignment.Start
        ) {
            // 조식/중식/석식
            Column(
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    modifier = GlanceModifier.size(height = 14.dp, width = 43.dp),
                    provider = ImageProvider(R.drawable.img_new_logo_primary),
                    contentDescription = "Logo"
                )

                Text(
                    "중식", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                    modifier = GlanceModifier.padding(top = 2.dp, bottom = 12.dp)
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            //식당 이름
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally  // 수평 중앙 정렬
            ) {
                Image(
                    modifier = GlanceModifier.size(18.dp),
                    provider = ImageProvider(R.drawable.ic_arrow_left),
                    contentDescription = "left"
                )
                Text(
                    "기숙사 식당",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                )
                Image(
                    modifier = GlanceModifier.size(18.dp),
                    provider = ImageProvider(R.drawable.ic_arrow_right),
                    contentDescription = "right"
                )
            }

        }

        Box(
            modifier = GlanceModifier.fillMaxSize()
                .background(color = Color(0xFFFAFAFB))
                .cornerRadius(10.dp)
        )
        {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = GlanceModifier.size(20.dp)
                        .padding(bottom = 6.dp),
                    provider = ImageProvider(R.drawable.ic_alert_circle),
                    contentDescription = "alert"
                )
                Text(
                    "네트워크 연결 상태를 확인해주세요.",
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
                )
            }
        }
    }
}