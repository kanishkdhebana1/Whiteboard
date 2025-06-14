package com.example.whiteboard.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.whiteboard.data.DrawView
import com.example.whiteboard.ui.WhiteBoardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun CustomCanvas(
    coroutineScope: CoroutineScope,
    scrollState: ScrollState,
    viewModel: WhiteBoardViewModel,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        var event = awaitPointerEvent()
                        val pointers = event.changes.filter { it.pressed }

                        if (pointers.size == 2) {
                            val pointerId1 = pointers[0].id
                            val pointerId2 = pointers[1].id

                            while (true) {
                                val dragEvent = awaitPointerEvent()

                                val currentPointers = dragEvent.changes.filter { it.pressed }
                                if (currentPointers.size != 2) break // stop if not 2 fingers

                                val p1 = currentPointers.find { it.id == pointerId1 }
                                val p2 = currentPointers.find { it.id == pointerId2 }

                                if (p1 != null && p2 != null) {
                                    val avgDeltaY = (p1.positionChange().y + p2.positionChange().y) / 2

                                    if (avgDeltaY != 0f) {
                                        coroutineScope.launch {
                                            scrollState.scrollBy(-avgDeltaY) // fix scroll direction
                                        }

                                        p1.consume()
                                        p2.consume()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState, enabled = false)
                .fillMaxSize(),
        ) {
            AndroidView(
                factory = {
                    DrawView(it, null).apply {
                        viewModel.setDrawView(this)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3000.dp) // adjust canvas height as needed
            )
        }
    }
}