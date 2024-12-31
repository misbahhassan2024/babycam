package com.mexemai.babycam.aicam.Parent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.zxing.integration.android.IntentIntegrator

/*
class UiScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiScreenContent()
        }
    }
    @Composable
    fun UiScreenContent() {
        val context = LocalContext.current
        var ipAddress by remember { mutableStateOf("192.168.10.10:8081") }

        // Set up the QR scanner launcher
        val qrScannerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                if (intentResult != null) {
                    val scannedIp = intentResult.contents ?: ""
                    if (scannedIp.isNotBlank()) {
                        ipAddress = scannedIp
                        // Automatically move to ClientStreamer class
                        val intent = Intent(context, ClientStreamer::class.java).apply {
                            putExtra("ip", ipAddress)
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "QR Code not valid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Column(
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { // Text field for IP address input
            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Enter or Scan IP Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Uri // Allow both IP and URIs
                )
            )

            // Button to show the stream in ClientStreamer
            Button(
                onClick = {
                    // Open ClientStreamer activity
                    val intent = Intent(context, ClientStreamer::class.java)
                    intent.putExtra("ip", ipAddress)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Show Stream in App")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Button to scan the QR code
            Button(
                onClick = {
                    IntentIntegrator(context as Activity).apply {
                        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        setPrompt("Scan QR code containing IP address")
                        setBeepEnabled(true)
                        setOrientationLocked(false)
                        qrScannerLauncher.launch(createScanIntent())
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf( Color(0xFF8E24AA).copy(alpha = 0.7f),
                                Color(0xFFBA68C8).copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text("Scan QR Code")
            }
        }
    }
}
*/
class UiScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiScreenContent()
        }
    }

    @Composable
    fun UiScreenContent() {
        val context = LocalContext.current
        var ipAddress by remember { mutableStateOf("192.168.10.8:8081") }
        // State to manage dialog visibility
        var showDialog by remember { mutableStateOf(true) }
        // Set up the QR scanner launcher
        val qrScannerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentResult =
                    IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                if (intentResult != null) {
                    val scannedIp = intentResult.contents ?: ""
                    if (scannedIp.isNotBlank()) {
                        ipAddress = scannedIp
                        // Automatically move to ClientStreamer class
                        val intent = Intent(context, ClientStreamer::class.java).apply {
                            putExtra("ip", ipAddress)
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "QR Code not valid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Column(
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) { // Text field for IP address input
//            TextField(
//                value = ipAddress,
//                onValueChange = { ipAddress = it },
//                label = { Text("Enter or Scan IP Address") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    keyboardType = KeyboardType.Uri // Allow both IP and URIs
//                )
//            )
//
//            // Button to show the stream in ClientStreamer
//            Button(
//                onClick = {
//                    // Open ClientStreamer activity
//                    val intent = Intent(context, ClientStreamer::class.java)
//                    intent.putExtra("ip", ipAddress)
//                    context.startActivity(intent)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            ) {
//                Text("Show Stream in App")
//            }
//            Spacer(modifier = Modifier.height(16.dp))

            // Button to scan the QR code
            Button(
                onClick = {
                    IntentIntegrator(context as Activity).apply {
                        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        setPrompt("Scan QR code containing IP address")
                        setBeepEnabled(true)
                        setOrientationLocked(false)
                        qrScannerLauncher.launch(createScanIntent())
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8E24AA).copy(alpha = 0.7f),
                                Color(0xFFBA68C8).copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text("Scan QR Code")
            }
        }
        // Show dialog when the screen opens
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                }, // Dismiss the dialog when clicked outside
                title = {
                    Text(
                        "Important Note!",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("Make sure both devices are connected with the same Internet Connection.")
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}