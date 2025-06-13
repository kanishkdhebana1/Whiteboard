package com.example.whiteboard.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.whiteboard.R
import com.example.whiteboard.data.DrawView
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@Composable
fun WhiteBoardScreen(viewModel: WhiteBoardViewModel) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        TopBar(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )

        AndroidView(
            factory = {
                DrawView(context, null).apply {
                    viewModel.setDrawView(this)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
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
fun TopBar(
    viewModel: WhiteBoardViewModel,
    modifier: Modifier = Modifier
) {

    val showResultCard by viewModel.showResultCard.collectAsState()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .zIndex(1f)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = {
                    viewModel.clear()
                },

                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
                    .size(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F2))
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear screen"
                    )
                }
            }


            Button(
                onClick = {
                    viewModel.recognize()
                    viewModel.setShowResultCard(true)
                },
                modifier = Modifier.width(85.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2F2F2),
                    contentColor = Color.DarkGray

                )
            ) {
                Text(
                    text = "Parse",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                )
            }
        }

        if (showResultCard && viewModel.recognizedText.isNotEmpty()) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF2F2F2),
                    contentColor = Color.DarkGray
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

                    IconButton(
                        onClick = {
                            val clip = ClipData.newPlainText(
                                "Recognized Text",
                                viewModel.recognizedText
                            )
                            clipboardManager.setPrimaryClip(clip)
                            viewModel.setShowResultCard(false)
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
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    viewModel: WhiteBoardViewModel
) {

    val context = LocalContext.current
    val strokeWidth = viewModel.strokeWidth
    var selectedColor by remember { mutableIntStateOf(Color.Black.toArgb()) }

    // State to track expansion
    val collapsedHeight = 55.dp
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
                .padding(8.dp)
                .height(with(LocalDensity.current) { heightAnim.value.toDp() })
                .clip(RoundedCornerShape(30.dp))
                .background(Color.LightGray.copy(alpha = 0.17f))
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

        IconButton(
            onClick = {
                viewModel.saveDrawingToFile(context) { success ->
                    val message = if (success) "Saved!" else "Save failed"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },

            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp)
                .size(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
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
}


@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onColorSelected: (Int) -> Unit,
    selectedColor: Int
) {
    val colors = listOf(
        Color.Black,
        Color(0xFFF2DD15),
        Color(0xFFF9922A),
        Color(0xFFF72222),
        Color(0xFF259DF1),
    )

    Row(modifier = modifier.padding(0.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        colors.forEach { color ->
            val isSelected = selectedColor == color.toArgb()

                Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color.Black else Color.Transparent,
                        shape = CircleShape
                    )
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color.toArgb()) }
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





@Preview(
    name = "Compact Phone",
    device = "spec:width=320dp,height=568dp,orientation=portrait"
)
@Composable
fun PreviewCompactPhone() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}

@Preview(
    name = "Medium Phone",
    device = "spec:width=411dp,height=891dp,orientation=portrait"
)
@Composable
fun PreviewMediumPhone() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}

@Preview(
    name = "Tall Slim Phone",
    device = "spec:width=360dp,height=800dp,orientation=portrait"
)
@Composable
fun PreviewTallSlimPhone() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}

@Preview(
    name = "Large Phone",
    device = "spec:width=480dp,height=960dp,orientation=portrait"
)
@Composable
fun PreviewLargePhone() {
    WhiteBoardScreen(viewModel = WhiteBoardViewModel())
}






