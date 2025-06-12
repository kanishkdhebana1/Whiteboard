package com.example.whiteboard.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiteboard.data.DrawView
import com.example.whiteboard.data.StrokeManager
import kotlinx.coroutines.launch

class WhiteBoardViewModel: ViewModel() {

    var recognizedText by mutableStateOf("")
        private set

    private var isModelInitialized by mutableStateOf(false)

    @SuppressLint("StaticFieldLeak")
    private var drawView: DrawView? = null

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

}
