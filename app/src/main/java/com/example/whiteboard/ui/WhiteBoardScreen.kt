package com.example.whiteboard.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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


@SuppressLint("UnrememberedMutableState")
@Composable
fun WhiteBoardScreen(viewModel: WhiteBoardViewModel) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var showColorPicker by remember { mutableStateOf(false) }
    val strokeWidth = viewModel.strokeWidth

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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            IconButton(
                onClick = { showColorPicker = !showColorPicker }
            ) {

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(30.dp)
                        .height(30.dp)
                        .background(Color(0xFFF2F2F2))
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = if (showColorPicker) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Toggle Color Picker"
                    )
                }
            }

            AnimatedVisibility(
                visible = showColorPicker,
                enter = slideInHorizontally(
                    initialOffsetX = { fullHeight -> 0 },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullHeight -> 0 },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Column(
                    Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shadow(4.dp)
                        .background(Color(0xFFF2F2F2))

                ) {
                    ColorPicker(
                        modifier = Modifier,
                        onColorSelected = { color ->
                            viewModel.setStrokeColor(color)
                            showColorPicker = false // optional: auto-collapse
                        }
                    )

                    StrokeWidthSlider(
                        value = strokeWidth,
                        onWidthChanged = {
                            viewModel.updateStrokeWidth(it)
                        }
                    )
                }
            }

            Spacer(modifier =  Modifier.weight(1f))

            IconButton(
                onClick = {
                    viewModel.saveDrawingToFile(context) { success ->
                        val message = if (success) "Saved!" else "Save failed"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(30.dp)
                        .height(30.dp)
                        .background(Color(0xFFF2F2F2))
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = "Save"
                    )
                }
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
                                val clip = ClipData.newPlainText(
                                    "Recognized Text",
                                    viewModel.recognizedText
                                )
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
    modifier: Modifier = Modifier,
    onColorSelected: (Int) -> Unit
) {
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Gray,
        Color.Black,
    )

    Row(modifier = modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
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


@Composable
fun StrokeWidthSlider(
    value: Float,
    onWidthChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .width(164.dp)
    ) {
        Slider(
            colors = SliderDefaults.colors(
                thumbColor = Color.DarkGray,
                activeTrackColor = Color.Gray,
                inactiveTrackColor = Color.LightGray
            ),
            value = value,
            onValueChange = {
                onWidthChanged(it)
            },
            valueRange = 2f..30f,
            steps = 0
        )
    }
}



@Preview
@Composable
fun WhiteBoardScreenPreview() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}


