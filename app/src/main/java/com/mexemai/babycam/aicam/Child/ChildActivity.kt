package com.mexemai.babycam.aicam.Child


import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mexemai.babycam.aicam.Detection.DetectionResult
import com.mexemai.babycam.aicam.Detection.ObjectDetectionViewModel
import com.mexemai.babycam.aicam.Detection.ObjectDetectorHelper
import com.mexemai.babycam.aicam.Helpers.DrawFunction
import com.mexemai.babycam.aicam.Helpers.Utils.Companion.sizeHeight
import com.mexemai.babycam.aicam.Helpers.Utils.Companion.sizeWidth
import com.mexemai.babycam.aicam.IP_CODE.generateQRCode
import com.mexemai.babycam.aicam.IP_CODE.getIPAddress
import com.mexemai.babycam.aicam.ZeroMQ.Dealer
import com.mexemai.babycam.aicam.ZeroMQ.MessageUtils
import com.mexemai.babycam.aicam.ZeroMQ.Router
import com.mexemai.babycam.aicam.enums.FlashOnOff
import com.mexemai.babycam.aicam.enums.MessageType
import com.mexemai.babycam.aicam.interfaces.FlashLight
import com.mexemai.babycam.aicam.interfaces.MessageReceiver
import com.mexemai.babycam.aicam.ui.theme.Compose_IpTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class CameraStreamActivity : ComponentActivity() , MessageReceiver, FlashLight{

    private lateinit var router: Router
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var qrCodeBitmap: Bitmap
    private val isQrCodeVisible = mutableStateOf(true)  // Track QR code visibility
    private var isScreenDimmed = mutableStateOf<Boolean>(true) // Track screen dimming state


    private val detectionResultsList = mutableListOf<DetectionResult>()
    private val averageDetectionResult = mutableStateOf("")
    private val frameCount = 20 // Define the number of frames to average

    private lateinit var dealer: Dealer


    private val handler = Handler(Looper.getMainLooper()) // Handler to manage screen timeout
    private var isScreenOn = true // Track whether the screen is on
    private lateinit var windowManager: WindowManager // For managing window flags
    private lateinit var keyguardManager: KeyguardManager // To lock the device
    private lateinit var gestureDetector: GestureDetector // For detecting gestures

    private lateinit var viewModel: ObjectDetectionViewModel
    var top =  0
    var bottom =  0
    var left =  0
    var right =  0

    lateinit  var resultViewSize:Size


    private lateinit var cameraExecutor: ExecutorService

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: androidx.camera.core.Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var context: Context
    var cameraControl = mutableStateOf<CameraControl?>(null)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ObjectDetectionViewModel::class.java)
        objectDetectorHelper = ObjectDetectorHelper(
            context = this@CameraStreamActivity,
            viewModel = viewModel,
            objectDetectorListener = this
        )

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


        gestureDetector = GestureDetector(this, SingleTapListener())

        val ipAddress = getIPAddress()

        qrCodeBitmap = generateQRCode("$ipAddress:8081")
//        zeroMQPublisher = ZeroMQPublisher("$ipAddress:8081")
//        zeroMQPublisher.start()

        setContent {
            Compose_IpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    context = LocalContext.current

                        bindCameraUseCases(
                            qrCodeBitmap,
                            isQrCodeVisible,
                            turnOffScreen = { turnOffScreen() },
                            viewModel,
                        )
                    CameraViewContent()
                }
            }
        }
        router = Router("$ipAddress:8081")
        router.set_running(true)
        coroutineScope.launch {
            router.start { clientId, message ->
                when (message.type) {
                    MessageType.SINGLE -> {

                        Log.d("Router", "Received single message: ${String(message.payload)}")
                        if(String(message.payload) == "AppOff")
                        {
                            Log.d("Messages", "ya lo${message.payload}")
                            isQrCodeVisible.value = true
//                            Log.d("Router", "shouting down")
//                            cameraExecutor.shutdown()
//                            router.set_running(false)
//                            while(router.get_running()){router.set_running(false)}
//                            while(!cameraExecutor.isShutdown){
//                                cameraExecutor.shutdown()
//                            }
//                            router.stop()
//                            coroutineScope.cancel()


                        }
                         else if(String(message.payload).equals("LightOn")){
                            runOnUiThread {
                                flashlightHandling(true)
                            }
                        }else if (String(message.payload).equals("LightOff")){
                            runOnUiThread {
                                flashlightHandling(false)
                            }
                        }else if (String(message.payload).equals("DimLight")){
                            runOnUiThread {
                                screenLightHandling(true)
                            }
                        }else if (String(message.payload).equals("HighLight")){
                            runOnUiThread {
                                screenLightHandling(false)
                            }
                        }else if (String(message.payload).equals("HideQR")){
                            isQrCodeVisible.value = false
                        }
                    }
                    MessageType.STREAM -> {
                        Log.d("Router", "Received a frame stream")
                    }
                }
            }
        }
//        timer(initialDelay = 1000L, period = 1000L ) {
//            coroutineScope.launch {
//                router.sendSingleMessage("Parent","hello i am child")
//            }
//        }
        // Keep the screen on initially
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        zeroMQSubscriber.startSubscriber("$ipAddress:8081", this)
    }

    // Method to simulate turning off the screen by dimming the brightness
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun turnOffScreen() {
//        if (isScreenOn) {
//            // Dim the screen to the lowest brightness
//            val layoutParams = window.attributes
//            layoutParams.screenBrightness = 0f // 0f means dimmest
//            window.attributes = layoutParams
//
//            // Optionally lock the device after dimming
//            keyguardManager.requestDismissKeyguard(this, null)
//            isScreenOn = false
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun turnOffScreen() {
        if (isScreenDimmed.value) {


        } else {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun screenLightHandling(screenFlag : Boolean) {
        if (screenFlag) {
            dimScreen()
        } else {
            normalScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dimScreen(){
        Log.d("Screen", "Screen dimmed")
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0f
        window.attributes = layoutParams
        keyguardManager.requestDismissKeyguard(this, null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun normalScreen(){
        Log.d("Screen", "Screen normal")
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 1.0f
        window.attributes = layoutParams
        keyguardManager.requestDismissKeyguard(this, null)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun flashlightHandling(isFlashlightOn: Boolean) {
        if (isFlashlightOn) {
            turnOnFlashlight(context, cameraControl.value)
        } else {
            turnOffFlashlight(context, cameraControl.value)
        }
    }

    fun turnOnFlashlight(context: Context, cameraControl: CameraControl?) {
        try {
            cameraControl?.enableTorch(true) ?: run {
                Log.e("Flashlight", "Camera control is still null")
                Toast.makeText(context, "Camera control not initialized", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun turnOffFlashlight(context: Context, cameraControl: CameraControl?) {
        try {
            cameraControl?.enableTorch(false) ?: run {
                Log.e("Flashlight", "Camera control is still null")
                Toast.makeText(context, "Camera control not initialized", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Method to wake up the screen by restoring brightness
    @RequiresApi(Build.VERSION_CODES.O)
    private fun wakeUpScreen() {
        if (!isScreenOn) {
            // Restore brightness to normal
            val layoutParams = window.attributes
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = layoutParams

            // Unlock the screen if locked
            keyguardManager.requestDismissKeyguard(this, null)

            isScreenOn = true
        }
    }


    // Gesture listener for single tap detection
    inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // Wake up the screen on single tap
            wakeUpScreen()
            return true
        }
    }

    // Override dispatchTouchEvent to detect touch events
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // Pass touch events to the gesture detector
        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }


    // Handle captured frame and pass it to stream server
    fun handleCapturedFrame(frame: Bitmap) {
        router.sendBitmapToDealer(frame)
    }

    @Composable
    fun CameraViewContent() {
        CameraView(viewModel)
    }


    @Preview(showBackground = true)
    @Composable
    fun CameraPreviewPreview() {
    }

    @SuppressLint("NotConstructor")
    @Composable
    fun CameraView(viewModel: ObjectDetectionViewModel = viewModel()) {
        // Observe LiveData as State
//        val q1 by viewModel.q1.observeAsState(Offset.Zero)
        val q1 by viewModel.q1.observeAsState(Offset.Zero)
        val q2 by viewModel.q2.observeAsState(Offset.Zero)
        val q3 by viewModel.q3.observeAsState(Offset.Zero)
        val q4 by viewModel.q4.observeAsState(Offset.Zero)

        val detectedText by viewModel.detectedText.observeAsState("")

        // Pass these values to the DrawFunction
        DrawFunction(
            q1 = q1,
            q2 = q2,
            q3 = q3,
            q4 = q4,
            drawableText = detectedText
        )

        router.sendSingleMessage("Parent", detectedText)

        Log.d("PointsDetectionCamera", "P 1 $q1")
        Log.d("PointsDetectionCamera", "P 2 $q2")
        Log.d("PointsDetectionCamera", "P 3 $q3")
        Log.d("PointsDetectionCamera", "P 4 $q4")
        Log.d("drawableText", "............@@@@@@@@@@$detectedText")

//        dealer.sendSingleMessage("drawableText")

    }

    @Composable
    @SuppressLint("UnsafeOptInUsageError")
    fun bindCameraUseCases(
        qrCodeBitmap: Bitmap,
        isQrCodeVisible: MutableState<Boolean>,
        turnOffScreen: () -> Unit,
        viewModel: ObjectDetectionViewModel,
//        streamServer: StreamServer,
//        cameraControl: CameraControl?, // Added cameraControl here

    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraProvider = cameraProviderFuture.get()

        var imageCapture: ImageCapture? = null
        lateinit var previewView: PreviewView
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        var isFlashlightOn by remember { mutableStateOf(false) }
        val hasFlashlight = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        val detectionResults by viewModel.detectionResults.observeAsState("")



        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxConstraint = this
            sizeWidth = with(LocalDensity.current) { boxConstraint.maxWidth.toPx() }
            sizeHeight = with(LocalDensity.current) { boxConstraint.maxHeight.toPx() }
            resultViewSize = android.util.Size(sizeWidth.toInt(), sizeHeight.toInt())

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    previewView = PreviewView(ctx)

                    cameraProviderFuture.addListener({
                        // Set up Preview use case
                        preview = androidx.camera.core.Preview.Builder()
                            .build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        // Combined ImageAnalyzer for handleCapturedFrame and detectObjects
                          imageAnalyzer = ImageAnalysis.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build().also {
                                it.setAnalyzer(executor) { imageProxy ->
                                    // Call handleCapturedFrame
                                    val bitmap = imageProxy.toBitmap()
                                    (context as CameraStreamActivity).handleCapturedFrame(bitmap)

                                    // Call detectObjects
                                    if (!::bitmapBuffer.isInitialized) {
                                        bitmapBuffer = Bitmap.createBitmap(
                                            imageProxy.width,
                                            imageProxy.height,
                                            Bitmap.Config.ARGB_8888
                                        )
                                    }
                                    detectObjects(imageProxy)

                                    // Close the image after analysis
                                    imageProxy.close()
                                }
                            }

                        // Set up ImageCapture use case
                        imageCapture = ImageCapture.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .build()

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        // Unbind existing use cases and bind new ones
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer
                        )
                        cameraControl.value = camera.cameraControl
                    }, executor)

                    preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    previewView
                }
            )

            // QR Code overlay, shown only when isQrCodeVisible is true
            if (isQrCodeVisible.value) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code for IP Address",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(300.dp)
                        .padding(16.dp)
                )
            }

//            // Display detection results on screen
            Text(
                text = detectionResults,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
                Text(
                    text = averageDetectionResult.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )


            // Position the flashlight and turn-off screen buttons at the bottom-right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Turn Off Screen Button
                IconButton(
                    onClick = { turnOffScreen() }
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Turn Off Screen",
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Flashlight Button
                /*if (hasFlashlight) {
                    IconButton(
                        onClick = {
                            if (isFlashlightOn) {
                                cameraControl?.enableTorch(false) ?: run {
                                    Log.e("Flashlight", "Camera control is still null")
                                    Toast.makeText(
                                        context,
                                        "Camera control not initialized",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                cameraControl?.enableTorch(true) ?: run {
                                    Log.e("Flashlight", "Camera control is still null")
                                    Toast.makeText(
                                        context,
                                        "Camera control not initialized",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            isFlashlightOn = !isFlashlightOn
                        }
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Filled.FlashlightOff else Icons.Filled.FlashlightOn,
                            contentDescription = if (isFlashlightOn) "Turn Off Flashlight" else "Turn On Flashlight",
                            tint = if (isFlashlightOn) Color.Yellow else Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }*/
                if (hasFlashlight) {
                    IconButton(
                        onClick = {
                            if (isFlashlightOn) {
                                turnOffFlashlight(context, cameraControl.value)
                            } else {
                                turnOnFlashlight(context, cameraControl.value)
                            }
                            isFlashlightOn = !isFlashlightOn
                        }
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Filled.FlashlightOff else Icons.Filled.FlashlightOn,
                            contentDescription = if (isFlashlightOn) "Turn Off Flashlight" else "Turn On Flashlight",
                            tint = if (isFlashlightOn) Color.Yellow else Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

        }
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection

        val result = objectDetectorHelper.detect(bitmapBuffer, imageRotation)

        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
        // Update the detected bitmap state

        //        // Add the result to the list
        //        if (result != null) {
        //            detectionResultsList.add(result)
        //        }

        // Check if we have enough frames
        if (detectionResultsList.size >= frameCount) {
            // Calculate average result
            calculateAverageResult()
            // Clear the list for the next set of frames
            detectionResultsList.clear()
        }
    }
    private fun calculateAverageResult() {
        if (detectionResultsList.isNotEmpty()) {
            // Assuming DetectionResult has fields like `score`, `boundingBox`
            val averageScore = detectionResultsList.map { it.score }.average().toFloat()
            val averageBoundingBox = RectF(
                detectionResultsList.map { it.boundingBox.left }.average().toFloat(),
                detectionResultsList.map { it.boundingBox.top }.average().toFloat(),
                detectionResultsList.map { it.boundingBox.right }.average().toFloat(),
                detectionResultsList.map { it.boundingBox.bottom }.average().toFloat()
            )

            // Update averaged detection result to display
            averageDetectionResult.value = "Score: $averageScore, Box: $averageBoundingBox"
        }
    }
    //------------------------Fin  onCreate --------------------------------

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
        router.set_running(false)
        router.stop()
        coroutineScope.cancel()
    }

    var message: String = ""
//    override fun messageReceived(message: String) {
//        Log.d("Test", "child$message")
//        this.message = message
//        Log.d("Test", "child$message")
//    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun flashOnOff(message: FlashOnOff) {
        Log.d("flash","its child${message.toString()}")
        turnOffScreen()
    }

    override fun singleMessageReceived(message: String) {

        Log.d("Single", "Single_Message_Received$message")
        //AppOff

    }

    override fun streamMessageReceived(message: Bitmap) {

    }

//    override fun streamMessageReceived(message: Bitmap) {
//        streamBitmap = message
//        streamBitmapState.value = message
//        Log.d("parent", "converted $streamBitmap")
//    }
}




