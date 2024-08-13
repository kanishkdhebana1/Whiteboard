package com.example.whiteboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.whiteboard.R
import com.example.whiteboard.data.DrawView


@Composable
fun WhiteBoardScreen(viewModel: WhiteBoardViewModel) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawing area using DrawView
        AndroidView(
            factory = {
                DrawView(context, null).apply {
                    viewModel.setDrawView(this)
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

