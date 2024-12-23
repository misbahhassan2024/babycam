package com.mexemai.babycam.aicam.Parent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mexemai.babycam.aicam.BabyMonitorScreen
import com.mexemai.babycam.aicam.Child.CameraStreamActivity
import com.mexemai.babycam.aicam.Service.StreamServer
import com.mexemai.babycam.aicam.ZeroMQ.Dealer
import com.mexemai.babycam.aicam.ZeroMQ.MessageUtils
import com.mexemai.babycam.aicam.enums.FlashOnOff
import com.mexemai.babycam.aicam.enums.MessageType
import com.mexemai.babycam.aicam.interfaces.FlashLight
import com.mexemai.babycam.aicam.interfaces.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush


class ClientStreamer : ComponentActivity() , MessageReceiver{

    var flashLight: FlashLight? = null
    var streamBitmap: Bitmap? = null
    var ipAddress: String? = ""

    var streamBitmapState = mutableStateOf<Bitmap?>(null)
    var singleMessageState = mutableStateOf<String>("")
    private lateinit var dealer: Dealer
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ipAddress = intent.getStringExtra("ip") ?: ""

            setContent {
                imageView()
            }
        dealer = Dealer(ipAddress!!) // Replace with Router's IP
        dealer.set_running(true)

        coroutineScope.launch {
            dealer.start { message ->
                when (message.type) {
                    MessageType.SINGLE -> {
                        val messageContent = String(message.payload)
                        Log.d("Dealer", "Received single message: ${String(message.payload)}")
                        singleMessageState.value = messageContent // Update the state
                    }
                    MessageType.STREAM -> {
                        streamBitmap = MessageUtils.byteArrayToBitmap(message.payload)
                        streamBitmapState.value = MessageUtils.byteArrayToBitmap(message.payload)
                        // Process the received bitmap
                    }
                }
            }
        }
        dealer.sendSingleMessage("HideQR")
    }
    init {
        flashLight = CameraStreamActivity()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun imageView(){
        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val screenWidth = configuration.screenWidthDp.dp

        var expanded by remember { mutableStateOf(false) } // State to control dropdown visibility
        var selectedOption by remember { mutableStateOf("") } // Declare and remember selectedOption
        var showDialog by remember { mutableStateOf(false) }

        var babysleepingState by remember {
            mutableStateOf(getBooleanPreference(context, "BabySleeping", false))
        }
        var babyawakeState by remember {
            mutableStateOf(getBooleanPreference(context, "BabyAwake", false))
        }
        var flashState by remember { mutableStateOf(false) } // Tracks Flash On/Off state
        var brightnessState by remember { mutableStateOf(false) } // Tracks Dim/High state


        val screenHeight = configuration.screenHeightDp.dp
        var currentPlayingOption by remember { mutableStateOf<String?>(null) }

        val audioFiles = mapOf(
            "Alert1" to "Alert1.mp3",
            "Alert2" to "Alert2.mp3",
            "Alert3" to "Alert3.mp3",
            "Alert4" to "Alert4.mp3",
            "Alert5" to "Alert5.mp3"
        )
        var isPlaying by remember { mutableStateOf(false) } // Track playback state
        var currentMediaPlayer: MediaPlayer? by remember { mutableStateOf(null) } // Keep MediaPlayer instance

        Log.d("ScreenSize", "Width: ${screenWidth.value} dp, Height: ${screenHeight.value} dp")

        // Trigger alert for BabyAwake
        LaunchedEffect(singleMessageState.value) {
            if (singleMessageState.value == "baby-lying-on-stomach" && babyawakeState) {
                playSelectedAlert(context, audioFiles[selectedOption]) { mediaPlayer, option ->
                    currentMediaPlayer?.stop()
                    currentMediaPlayer?.release()
                    currentMediaPlayer = mediaPlayer
                    currentPlayingOption = option
                    isPlaying = true
                }
            }
        }

        // Trigger alert for BabySleeping
        LaunchedEffect(singleMessageState.value) {
            if (singleMessageState.value == "baby-lying-on-back" && babysleepingState) {
                playSelectedAlert(context, audioFiles[selectedOption]) { mediaPlayer, option ->
                    currentMediaPlayer?.stop()
                    currentMediaPlayer?.release()
                    currentMediaPlayer = mediaPlayer
                    currentPlayingOption = option
                    isPlaying = true
                }
            }
        }
        dealer.sendSingleMessage("HideQR")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
//                        Color(0xFF8E24AA),//.copy(alpha = 0.5f),
                            Color(0xFFBA68C8),
                            Color(0xFF00BBD4),
                            Color(0xFF0288D1),
                            Color(0xFFFFA726).copy(alpha = 0.6f)
                        )
                    )
                )
        ) {
            // Settings button at the top-right corner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top, // Align rows at the top
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Flash On/Off Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8E24AA).copy(alpha = 0.7f),
                                        Color(0xFFBA68C8).copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text("Flashlight", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        Switch(
                            checked = flashState,
                            onCheckedChange = {
                                flashState = it
                                if (it) {
                                    dealer.sendSingleMessage("LightOn")
                                } else {
                                    dealer.sendSingleMessage("LightOff")
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                        )
                    }

                    // Dim/High Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8E24AA).copy(alpha = 0.7f),
                                        Color(0xFFBA68C8).copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text("Brightness", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        Switch(
                            checked = brightnessState,
                            onCheckedChange = {
                                brightnessState = it
                                if (it) {
                                    dealer.sendSingleMessage("DimLight")
                                } else {
                                    dealer.sendSingleMessage("HighLight")
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Add space between rows

                // Display the single message
                Text(
                    text = singleMessageState.value, // Display the message
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
//                        .background(Color.White)
                        .padding(8.dp)
                )
            }

            streamBitmapState.value?.let {
                Image(bitmap = streamBitmap!!.asImageBitmap(),
                    contentDescription = "null",
                    modifier = Modifier
                        .size(screenWidth, screenHeight)
                        .rotate(90.0f),
                    contentScale = ContentScale.Fit

                )
            }
            // Settings popup dialog
            if (showDialog) {
                Box(
                    modifier = Modifier
                        .padding(16.dp) // Add padding for better appearance
                ) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Settings") },
                        text = {
                            Column(

                            ) {

                                // First Switch
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Baby Sleeping",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Switch(
                                        checked = babysleepingState,
                                        onCheckedChange = {
                                            babysleepingState = it
                                            saveBooleanPreference(context, "BabySleeping", it)
                                        }
                                    )
                                }

                                // Baby Awake Switch
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Baby Awake",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Switch(
                                        checked = babyawakeState,
                                        onCheckedChange = {
                                            babyawakeState = it
                                            saveBooleanPreference(context, "BabyAwake", it)
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp)) // Add space between rows

                                // Dropdown with audio options and play icon
                                val context = LocalContext.current
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = {
                                        expanded = !expanded
                                    } // Toggle dropdown visibility
                                ) {
                                    TextField(
                                        value = selectedOption, // Show selected option
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Select an Alarm") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        audioFiles.forEach { (option, fileName) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(text = option) // Display option label

                                                        IconButton(
                                                            onClick = {
                                                                // Toggle playback for this specific option
                                                                if (currentPlayingOption == option) {
                                                                    // Stop playback
                                                                    currentMediaPlayer?.stop()
                                                                    currentMediaPlayer?.release()
                                                                    currentMediaPlayer = null
                                                                    currentPlayingOption = null
                                                                } else {
                                                                    // Stop any currently playing audio
                                                                    currentMediaPlayer?.stop()
                                                                    currentMediaPlayer?.release()

                                                                    // Start playback for the new option
                                                                    currentMediaPlayer =
                                                                        MediaPlayer()
                                                                    try {
                                                                        val assetFileDescriptor =
                                                                            context.assets.openFd(
                                                                                fileName
                                                                            )
                                                                        currentMediaPlayer?.apply {
                                                                            setDataSource(
                                                                                assetFileDescriptor.fileDescriptor,
                                                                                assetFileDescriptor.startOffset,
                                                                                assetFileDescriptor.length
                                                                            )
                                                                            prepare()
                                                                            start()
                                                                        }
                                                                        currentPlayingOption =
                                                                            option // Set this option as playing
                                                                        assetFileDescriptor.close()
                                                                    } catch (e: Exception) {
                                                                        e.printStackTrace()
                                                                        currentMediaPlayer?.release()
                                                                        currentMediaPlayer = null
                                                                        currentPlayingOption = null
                                                                    }
                                                                }
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = if (currentPlayingOption == option) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                                contentDescription = if (currentPlayingOption == option) "Stop $option" else "Play $option"
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedOption =
                                                        option // Update selected option
                                                    expanded = false // Close dropdown
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Close")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Save")
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp) // Add padding for better appearance
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFBA68C8),
                                        Color(0xFF00BBD4),
                                        Color(0xFF0288D1),
                                        Color(0xFFFFA726).copy(alpha = 0.6f)
                                    ),
                                    startY = 0f,
                                    endY = 1000f // Adjust gradient spread
                                ),
                                shape = MaterialTheme.shapes.medium // Rounded corners
                            )
                    )
                }
            }
        }
    }
    fun playSelectedAlert(
        context: Context,
        fileName: String?,
        onPlaybackStart: (MediaPlayer?, String?) -> Unit
    ) {
        if (fileName != null) {
            val mediaPlayer = MediaPlayer()
            try {
                val assetFileDescriptor = context.assets.openFd(fileName)
                mediaPlayer.apply {
                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                    prepare()
                    start()
                }
                assetFileDescriptor.close()
                onPlaybackStart(mediaPlayer, fileName)
            } catch (e: Exception) {
                e.printStackTrace()
                mediaPlayer.release()
                onPlaybackStart(null, null)
            }
        } else {
            Log.d("Alert", "No alert selected.")
        }
    }
    fun saveBooleanPreference(context: Context, key: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences("SwitchPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBooleanPreference(context: Context, key: String, defaultValue: Boolean): Boolean {
        val sharedPreferences = context.getSharedPreferences("SwitchPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun onDestroy() {

        super.onDestroy()
        dealer.sendSingleMessage("AppOff")
        dealer.set_running(false)
        while(dealer.get_running()){
            dealer.set_running(false)
        }
        dealer.stop()
        coroutineScope.cancel()
    }

    override fun singleMessageReceived(message: String) {

    }

    override fun streamMessageReceived(message: Bitmap) {
        streamBitmap = message
        streamBitmapState.value = message
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewScreen() {
        imageView()
    }
}
