package com.mexemai.babycam.aicam.Detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.SystemClock
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.mexemai.babycam.aicam.Child.CameraStreamActivity
import com.mexemai.babycam.aicam.Helpers.Utils
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var SCORE_THRESHOLD: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 10,
    var currentDelegate: Int = 0,
    var currentModel: Int = 0,
    val context: Context,
    val viewModel: ObjectDetectionViewModel,
    val objectDetectorListener: CameraStreamActivity,
) {
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    fun clearObjectDetector() {
        objectDetector = null
    }

    fun setupObjectDetector() {
        // Create the base options for the detector using specifies max results and score threshold
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(SCORE_THRESHOLD)
                .setMaxResults(maxResults)

        // Set general detection options, including number of used threads
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        // Use the specified hardware for running the model. Default to CPU
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    // objectDetectorListener?.onError("GPU is not supported on this device")
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        val modelName =
            when (currentModel) {
                MODEL_MOBILENETV1 -> "updated_baby.tflite"
                MODEL_EFFICIENTDETV0 -> "efficientdet-lite0.tflite"
                MODEL_EFFICIENTDETV1 -> "efficientdet-lite1.tflite"
                MODEL_EFFICIENTDETV2 -> "efficientdet-lite2.tflite"
                else -> "updated_baby.tflite"
            }

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(context, modelName, optionsBuilder.build())
        } catch (e: IllegalStateException) {

            Log.e("Test", "TFLite failed to load model with error: " + e.message)
        }
    }

    /*
        fun detect(image: Bitmap, imageRotation: Int) {
            if (objectDetector == null) {
                setupObjectDetector()
            }
            // Inference time is the difference between the system time at the start and finish of the
            // process
            var inferenceTime = SystemClock.uptimeMillis()

            // Create preprocessor for the image.
            // See https://www.tensorflow.org/lite/inference_with_metadata/
            //            lite_support#imageprocessor_architecture
            val imageProcessor = ImageProcessor.Builder()
                .add(Rot90Op(-imageRotation / 90))
                .build()

            // Preprocess the image and convert it into a TensorImage for detection.
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

            val detectedObjectss = mutableListOf<DetectionResult>()
            val results = objectDetector?.detect(tensorImage)
            results?.let { detections ->
                val detectedObjects = detections.map { detection ->
                    val boundingBox = detection.boundingBox // Get the bounding box from each detection
                    val categoryName = detection.categories.firstOrNull()?.label ?: "Unknown" // Get category name or default to "Unknown"
                    val score = detection.categories.firstOrNull()?.score ?: 0f // Get score or default to 0
                    Log.d("ObjectDetect", " BoundingBox -> $boundingBox CategoryName -> $categoryName Score -> $score")

                    if (score >= SCORE_THRESHOLD) {
                        // Add list of detected objects in DetectionViewModel
                        detectedObjectss.add(DetectionResult(score, categoryName, boundingBox))

                        val scaleX = Utils.sizeWidth / tensorImage.width
                        val scaleY = Utils.sizeHeight / tensorImage.height

                        val top = boundingBox.top * scaleY
                        val bottom = boundingBox.bottom * scaleY
                        val left = (boundingBox.left * scaleX) - 100
                        val right = (boundingBox.right * scaleX) + 100

                        var q1 = Offset(left,top)
                        var q2 = Offset(right,top)
                        var q3 = Offset(right,bottom)
                        var q4 = Offset(left,bottom)

                        // Update the ViewModel's LiveData with detection results
                        viewModel.updateDetectionResults(q1, q2, q3, q4, inferenceTime.toString())

                    }


                }
                Log.d("DetectionResult","Detection Result <---) $detectedObjects")
            }
    //        DetectionResult.add(DetectionResult(score = score, label = categoryName, boundingBox = boundingBox))
                 inferenceTime = SystemClock.uptimeMillis() - inferenceTime


            // Pass the detectedObjects list to the DrawFunction


        }
    */



    fun detect(image: Bitmap, imageRotation: Int) {
        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()
        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-imageRotation / 90))
            .build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))
        val results = objectDetector?.detect(tensorImage)
        val detectedObjects = mutableListOf<DetectionResult>()

        results?.forEach { detection ->
            val boundingBox = detection.boundingBox
            val categoryName = detection.categories.firstOrNull()?.label ?: "Unknown"
            val score = detection.categories.firstOrNull()?.score ?: 0f

            if (score >= SCORE_THRESHOLD) {
                detectedObjects.add(DetectionResult(score = score, label = categoryName, boundingBox = boundingBox))

                val scaleX = Utils.sizeWidth / tensorImage.width
                val scaleY = Utils.sizeHeight / tensorImage.height

                val top = boundingBox.top * scaleY
                val bottom = boundingBox.bottom * scaleY
                val left = (boundingBox.left * scaleX) - 100
                val right = (boundingBox.right * scaleX) + 100

                var q1 = Offset(left,top)
                var q2 = Offset(right,top)
                var q3 = Offset(right,bottom)
                var q4 = Offset(left,bottom)

                // Log the results to the ViewModel
                val categoryName = categoryName
                val scoreLabel = " $categoryName $score"

                viewModel.updateDetectionResults(q1, q2, q3, q4,  categoryName, scoreLabel)
            }
        }

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
//        Log.d("DetectionResult", "Detection Results: $detectedObjects")
    }




    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_MOBILENETV1 = 0
        const val MODEL_EFFICIENTDETV0 = 1
        const val MODEL_EFFICIENTDETV1 = 2
        const val MODEL_EFFICIENTDETV2 = 3
    }
}
data class DetectionResult(
    val score: Float,
    val label: String,
    val boundingBox: RectF,


)