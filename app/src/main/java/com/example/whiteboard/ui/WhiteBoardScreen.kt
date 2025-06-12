package com.example.whiteboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.whiteboard.R
import com.example.whiteboard.data.DrawView


@Composable
fun WhiteBoardScreen(viewModel: WhiteBoardViewModel) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var showColorPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                DrawView(context, null).apply {
                    viewModel.setDrawView(this)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .zIndex(0f)
        )

        Column {
            IconButton(
                onClick = { showColorPicker = !showColorPicker }
            ) {
                Icon(
                    imageVector = if (showColorPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle Color Picker"
                )
            }


            AnimatedVisibility(
                visible = showColorPicker,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> 0 },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> 0 },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                ColorPicker(
                    onColorSelected = { color ->
                        viewModel.setStrokeColor(color)
                        showColorPicker = false // optional: auto-collapse
                    }
                )
            }

        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Card(colors = CardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row {
                    Text(
                        text = viewModel.recognizedText,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )

                    if (viewModel.recognizedText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val clip = ClipData.newPlainText("Recognized Text", viewModel.recognizedText)
                                clipboardManager.setPrimaryClip(clip)
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterVertically)
                                .padding(8.dp),

                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy),
                                contentDescription = "Copy"
                            )
                        }
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {



                Button(
                    onClick = {
                        viewModel.clear()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        viewModel.recognize()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Recognize")
                }
            }
        }
    }
}


@Composable
fun ColorPicker(
    onColorSelected: (Int) -> Unit
) {
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Gray,
        Color.Black,
    )

    Column(modifier = Modifier.padding(8.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.dp, Color.Transparent, CircleShape)
                    .clickable { onColorSelected(color.toArgb()) }
            )
        }
    }
}


@Preview
@Composable
fun WhiteBoardScreenPreview() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}


