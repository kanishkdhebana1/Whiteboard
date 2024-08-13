package com.example.whiteboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.whiteboard.ui.WhiteBoardScreen
import com.example.whiteboard.ui.WhiteBoardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteBoardScreen(viewModel = WhiteBoardViewModel())
        }
    }
}
