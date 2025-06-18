package com.example.whiteboard.ui.components

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.whiteboard.R
import com.example.whiteboard.ui.WhiteBoardViewModel
import com.example.whiteboard.ui.theme.LocalCustomColors
import kotlinx.coroutines.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SideBar(
    viewModel: WhiteBoardViewModel,
    modifier: Modifier = Modifier
) {

    val showResultCard by viewModel.showResultCard.collectAsState()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var expanded by remember { mutableStateOf(false) }
    var showLeftIcon by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        delay(100)
        showLeftIcon = expanded
    }

    val customColors = LocalCustomColors.current

    Box(
        modifier = modifier
            .zIndex(2f)
            .padding(top = 120.dp, end = 8.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .width(35.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                .background(Color.Red.copy(alpha = 0.6f))
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (showLeftIcon) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                contentDescription = "Toggle Tools",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 38.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(customColors.bottomBarColor.copy(alpha = 0.7f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IconButton(
                    onClick = {
                        viewModel.recognize()
                        viewModel.recognizeAndCalculate()
                        viewModel.setShowResultCard(true)
                        expanded = !expanded
                    },

                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF1F192A)),
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Parse",
                            tint = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.clear()
                        expanded = !expanded
                    },

                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear screen"
                        )
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.saveDrawingToFile(context) { success ->
                            val message = if (success) "Saved!" else "Save failed"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }

                        expanded = !expanded
                    },

                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
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

        if (showResultCard && viewModel.recognizedText.isNotEmpty()) {
            ParseDisplay(
                viewModel = viewModel,
                clipboardManager = clipboardManager,
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ParseDisplay(
    viewModel: WhiteBoardViewModel,
    clipboardManager: ClipboardManager,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 100.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF2F2F2),
                contentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {

            val calculationResult = viewModel.calculationResult.trim()

            val showCalculatedResult = calculationResult.isNotEmpty() &&
                    calculationResult != "Invalid expression" &&
                    calculationResult != "No expression"

            val text = if (showCalculatedResult) {
                "${viewModel.recognizedText} = ${viewModel.calculationResult}"
            } else {
                viewModel.recognizedText
            }

            Row {
                Text(
                    text = text,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f, fill = false)
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Clip
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


@Preview
@Composable
fun TopBarPreview() {
    SideBar(viewModel = WhiteBoardViewModel())
}