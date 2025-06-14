package com.example.whiteboard.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.whiteboard.R
import com.example.whiteboard.ui.components.BottomBar
import com.example.whiteboard.ui.components.CustomCanvas
import com.example.whiteboard.ui.components.TopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState", "ReturnFromAwaitPointerEventScope")
@Composable
fun WhiteBoardScreen(viewModel: WhiteBoardViewModel) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.TopStart)
        )

        CustomCanvas(
            coroutineScope = coroutineScope,
            scrollState = scrollState,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        ScrollButtons(
            modifier = Modifier.fillMaxSize(),
            showScrollUpButton = scrollState.value > 0,
            showScrollDownButton = scrollState.value < 3000,
            coroutineScope = coroutineScope,
            scrollState = scrollState
        )

        BottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            viewModel = viewModel
        )
    }
}


@Composable
fun ScrollButtons(
    showScrollUpButton: Boolean,
    showScrollDownButton: Boolean,
    coroutineScope: CoroutineScope,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        if (showScrollUpButton) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollBy(-300f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(15.dp)
                    .background(Color(ContextCompat.getColor(context, R.color.button_gray)), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll Up",
                    tint = Color.Black
                )
            }
        }


        if (showScrollDownButton) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollBy(300f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(15.dp)
                    .background(Color(ContextCompat.getColor(context, R.color.button_gray)), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll Down",
                    tint = Color.Black
                )
            }
        }
    }
}






//@Preview(
//    name = "Compact Phone",
//    device = "spec:width=320dp,height=568dp,orientation=portrait"
//)
//@Composable
//fun PreviewCompactPhone() {
//    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
//}

@Preview(
    name = "Medium Phone",
    device = "spec:width=411dp,height=891dp,orientation=portrait"
)
@Composable
fun PreviewMediumPhone() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}

//@Preview(
//    name = "Tall Slim Phone",
//    device = "spec:width=360dp,height=800dp,orientation=portrait"
//)
//@Composable
//fun PreviewTallSlimPhone() {
//    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
//}
//
//@Preview(
//    name = "Large Phone",
//    device = "spec:width=480dp,height=960dp,orientation=portrait"
//)
//@Composable
//fun PreviewLargePhone() {
//    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
//}






