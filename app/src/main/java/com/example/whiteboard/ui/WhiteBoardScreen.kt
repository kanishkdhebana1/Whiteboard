package com.example.whiteboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.whiteboard.R
import com.example.whiteboard.data.DrawView
import com.example.whiteboard.data.StrokeManager
import kotlinx.coroutines.launch


@Composable
fun WhiteBoardScreen() {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    var isModelInitialized by remember { mutableStateOf(false) }
    var drawView: DrawView? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Initialize the recognition model
    LaunchedEffect(Unit) {
        // Ensure the model is downloaded and initialized
        StrokeManager.download()
        // Delay to ensure model is ready; ideally you would use a callback here
        kotlinx.coroutines.delay(2000) // Adjust delay as needed
        isModelInitialized = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawing area using DrawView
        AndroidView(
            factory = {
                DrawView(context, null).apply {
                    drawView = this // Assign reference to drawView
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )



        // Column for buttons at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display recognized text on top
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
                        text = recognizedText,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )

                    if (recognizedText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val clip = ClipData.newPlainText("Recognized Text", recognizedText)
                                clipboardManager.setPrimaryClip(clip)
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterVertically)
                                .padding(8.dp),

                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy), // Replace with your copy icon resource
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
                        // Clear the drawing and recognized text
                        drawView?.clear() // Clear canvas
                        StrokeManager.clear() // Clear stroke data
                        recognizedText = "" // Clear recognized text
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            // Ensure model is initialized before recognizing
                            if (isModelInitialized) {
                                StrokeManager.recognize { resultText ->
                                    recognizedText = resultText
                                }
                            } else {
                                recognizedText = "Model not initialized"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Recognize")
                }
            }
        }
    }
}

