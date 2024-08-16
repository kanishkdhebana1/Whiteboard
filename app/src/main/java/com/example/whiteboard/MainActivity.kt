package com.example.whiteboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.whiteboard.ui.WhiteBoardScreen
import com.example.whiteboard.ui.WhiteBoardViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<WhiteBoardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteBoardScreen(viewModel = viewModel)
        }
    }
}
