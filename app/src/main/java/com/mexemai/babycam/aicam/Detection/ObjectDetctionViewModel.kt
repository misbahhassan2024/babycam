package com.mexemai.babycam.aicam.Detection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ObjectDetectionViewModel : ViewModel() {
    private val _q1 = MutableLiveData(Offset.Zero)
    private val _q2 = MutableLiveData(Offset.Zero)
    private val _q3 = MutableLiveData(Offset.Zero)
    private val _q4 = MutableLiveData(Offset.Zero)
    private val _drawableText = MutableLiveData("")

    val q1: LiveData<Offset> = _q1
    val q2: LiveData<Offset> = _q2
    val q3: LiveData<Offset> = _q3
    val q4: LiveData<Offset> = _q4
    val detectedText: LiveData<String> = _drawableText

    //    fun updateDetectionResults(q1Value: Offset, q2Value: Offset, q3Value: Offset, q4Value: Offset, text: String,  inferenceTime: String, resultsText: String) {
//        CoroutineScope(Dispatchers.Main).launch {
//            _q1.value = q1Value
//            _q2.value = q2Value
//            _q3.value = q3Value
//            _q4.value = q4Value
//            _drawableText.value = text
//        }
//        _detectionResults.postValue(resultsText) // Update with formatted detection results
//    }
    private val _detectionResults = MutableLiveData<String>()
    val detectionResults: LiveData<String> get() = _detectionResults



    fun updateDetectionResults(q1: Offset, q2: Offset, q3: Offset, q4: Offset,  categoryName: String, scoreLabel: String, ) {
        CoroutineScope(Dispatchers.Main).launch {
            _q1.value = q1
            _q2.value = q2
            _q3.value = q3
            _q4.value = q4
            _drawableText.value = categoryName

        }
        // Update bounding box points and inference time as before
        _detectionResults.postValue(scoreLabel) // Update with formatted detection results
    }
}
