package com.eatssu.android.presentation.cafeteria.review.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import java.io.File

class ReviewListActivity : ComponentActivity() {


    private var itemId: Long = 0
    private lateinit var itemName: String
    private var comment: String? = ""

    private var imageFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            EatssuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReviewListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ReviewWritePreview() {
//    EatssuTheme {
//        ReviewWriteScreen()
//    }
//}