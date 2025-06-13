package com.example.whiteboard.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiteboard.data.DrawView
import com.example.whiteboard.data.StrokeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mariuszgromada.math.mxparser.Expression

class WhiteBoardViewModel: ViewModel() {

    var recognizedText by mutableStateOf("")
        private set

    var strokeWidth by mutableFloatStateOf(8f)
        private set


    private var isModelInitialized by mutableStateOf(false)

    @SuppressLint("StaticFieldLeak")
    private var drawView: DrawView? = null

    private val _showResultCard = MutableStateFlow(false)
    val showResultCard: StateFlow<Boolean> = _showResultCard

    var calculationResult by mutableStateOf("")
        private set

    fun recognizeAndCalculate() {
        viewModelScope.launch {
            if (isModelInitialized) {
                StrokeManager.recognize { resultText ->
                    updateRecognizedText(resultText)

                    val cleanedExpression = resultText
                        .replace("x", "*", ignoreCase = true)
                        .replace("X", "*") // Just to be extra safe
                        .trim()


                    val expression = cleanedExpression.trim()
                    if (expression.isEmpty()) {
                        calculationResult = "No expression"
                    } else {
                        val exp = Expression(expression)
                        val result = exp.calculate()

                        calculationResult = if (result.isNaN()) {
                            "Invalid expression"
                        } else {
                            result.toString()
                        }
                    }
                }
            } else {
                updateRecognizedText("Model not initialized")
                calculationResult = "Model not initialized"
            }
        }
    }




    init {
        initializeModel()
    }

    private fun initializeModel() {
        viewModelScope.launch {
            StrokeManager.download()
            kotlinx.coroutines.delay(2000)
            isModelInitialized = true
        }
    }

    fun setDrawView(drawView: DrawView) {
        this.drawView = drawView
    }

    private fun updateRecognizedText(text: String) {
        recognizedText = text
    }

    fun clear() {
        recognizedText = ""
        calculationResult = ""
        StrokeManager.clear()
        drawView?.clear()
    }

    fun recognize() {
        viewModelScope.launch {
            if (isModelInitialized) {
                StrokeManager.recognize { resultText ->
                    updateRecognizedText(resultText)
                }
            } else {
                updateRecognizedText("Model not initialized")
            }
        }
    }

    fun setStrokeColor(color: Int) {
        drawView?.setStrokeColor(color)
    }

    fun updateStrokeWidth(width: Float) {
        strokeWidth = width
        drawView?.setStrokeWidth(width)
    }

    fun saveDrawingToFile(context: Context, onComplete: (Boolean) -> Unit) {
        val bitmap = drawView?.getBitmap()
        if (bitmap == null) {
            onComplete(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val filename = "whiteboard_${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Whiteboard")
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val success = try {
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    true
                } == true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

            withContext(Dispatchers.Main) {
                onComplete(success)
            }
        }
    }

    fun setShowResultCard(show: Boolean) {
        _showResultCard.value = show
    }
}
