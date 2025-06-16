package com.example.whiteboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.whiteboard.ui.WhiteBoardScreen
import com.example.whiteboard.ui.WhiteBoardViewModel
import com.example.whiteboard.ui.theme.WhiteboardTheme

//class MainActivity : ComponentActivity() {
//    private val viewModel by viewModels<WhiteBoardViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge()
//        super.onCreate(savedInstanceState)
//        setContent {
//            WhiteBoardScreen(viewModel = viewModel)
//        }
//    }
//}


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<WhiteBoardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            WhiteboardTheme {
                WhiteBoardScreen(viewModel = viewModel)
            }
        }
    }
}
