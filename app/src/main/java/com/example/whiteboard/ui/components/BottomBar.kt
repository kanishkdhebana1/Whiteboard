package com.example.whiteboard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.whiteboard.R
import com.example.whiteboard.ui.WhiteBoardViewModel
import kotlinx.coroutines.launch

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    viewModel: WhiteBoardViewModel
) {

    val strokeWidth = viewModel.strokeWidth
    var selectedColor by remember { mutableIntStateOf(Color.Black.toArgb()) }

    // State to track expansion
    val collapsedHeight = 58.dp
    val expandedHeight = 170.dp
    val collapsedPx = with(LocalDensity.current) { collapsedHeight.toPx() }
    val expandedPx = with(LocalDensity.current) { expandedHeight.toPx() }

    val heightAnim = remember { Animatable(collapsedPx) }
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isExpanded) {
        heightAnim.animateTo(
            if (isExpanded) expandedPx else collapsedPx,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .height(with(LocalDensity.current) { heightAnim.value.toDp() })
                .clip(RoundedCornerShape(30.dp))
                .background(Color.LightGray.copy(alpha = 0.24f))
                .weight(1f)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            scope.launch {
                                val newHeight = heightAnim.value - dragAmount
                                heightAnim.snapTo(newHeight.coerceIn(collapsedPx, expandedPx))
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                isExpanded = heightAnim.value > (collapsedPx + expandedPx) / 2
                                heightAnim.animateTo(if (isExpanded) expandedPx else collapsedPx)
                            }
                        }
                    )
                }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Gray.copy(alpha = 0.6f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                ColorPicker(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    selectedColor = selectedColor,
                    onColorSelected = { color ->
                        selectedColor = color
                        viewModel.setStrokeColor(color)
                    }
                )

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    StrokeWidthSlider(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        value = strokeWidth,
                        onWidthChanged = {
                            viewModel.updateStrokeWidth(it)
                        },
                        color = Color(selectedColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(end = 8.dp)
            .size(60.dp)
        )
    }
}



@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onColorSelected: (Int) -> Unit,
    selectedColor: Int
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val dotSize = screenWidth * 0.09f
    val context = LocalContext.current

    val colors = listOf(
        ContextCompat.getColor(context, R.color.pen_black),
        ContextCompat.getColor(context, R.color.pen_yellow),
        ContextCompat.getColor(context, R.color.pen_orange),
        ContextCompat.getColor(context, R.color.pen_red),
        ContextCompat.getColor(context, R.color.pen_blue)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { color ->
            val isSelected = selectedColor == color

            Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color.Black else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(3.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color = Color(color))
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
fun StrokeWidthSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onWidthChanged: (Float) -> Unit,
    color: Color = Color.Black
) {
    Column(
        modifier = modifier.width(300.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 4.dp),
            text = "Stroke Width"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = value,
                onValueChange = onWidthChanged,
                valueRange = 2f..40f,
                steps = 0,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.DarkGray,
                    activeTrackColor = Color.Gray,
                    inactiveTrackColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Canvas(
                modifier = Modifier
                    .size(40.dp)
            ) {
                drawCircle(
                    color = color,
                    radius = value / 1.4f
                )
            }
        }
    }
}

