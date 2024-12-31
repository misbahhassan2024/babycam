package com.mexemai.babycam.aicam.IP_CODE

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.mexemai.babycam.aicam.Child.CameraStreamActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class QrCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QrCode()
        }
    }
}

@Composable
fun QrCode() {
    val context = LocalContext.current

    // Get the device IP address
    val ipAddress = getIPAddress()

    // Generate the QR code
    val qrCodeBitmap = remember {
        generateQRCode("$ipAddress:8081")
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display the QR code
        Image(
            bitmap = qrCodeBitmap.asImageBitmap(),
            contentDescription = "QR Code for IP Address",
            modifier = Modifier
                .size(400.dp)  // Adjust the size as needed
        )

        Spacer(modifier = Modifier.height(16.dp))

//        // Display the IP address
//        Text("IP Address: http://$ipAddress:8081")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(context, CameraStreamActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(Color.Transparent),

            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF57C00).copy(alpha = 0.9f),
                            Color(0xFFFFA726).copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                "Done",
                color = androidx.compose.ui.graphics.Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


fun generateQRCode(text: String): Bitmap {
    val size = 700 // Size of the QR code
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

