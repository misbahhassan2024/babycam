package com.mexemai.babycam.aicam.Helpers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mexemai.babycam.aicam.Child.CameraStreamActivity
import com.mexemai.babycam.aicam.IP_CODE.QrCodeActivity
import com.mexemai.babycam.aicam.Parent.UiScreen
import com.mexemai.babycam.aicam.R

class NextScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParentChildModeScreen(
                onBabyClick = {
                    if (isNetworkAvailable()) {
                        startActivity(Intent(this, CameraStreamActivity::class.java))
                    } else {
                        showNoInternetDialog()
                    }
                },
                onParentClick = {
                    if (isNetworkAvailable()) {
                        startActivity(Intent(this, UiScreen::class.java))
                    } else {
                        showNoInternetDialog()
                    }
                }
            )
        }
    }

    // Function to check if internet is available
    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Show a beautiful styled dialog if no internet connection
    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("No internet connection. Please connect to the internet and try again.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }

        // Customize the appearance of the AlertDialog
        val alert = builder.create()
        alert.show()



    }
}

@Composable
fun ParentChildModeScreen(
    onBabyClick: () -> Unit,
    onParentClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

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
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Icon
            Image(
                painter = painterResource(id = R.drawable.log),
                contentDescription = "",
                modifier = Modifier
                    .size(300.dp)
                    .padding(top = 76.dp),
                contentScale = ContentScale.Fit
            )

            // Title
            Text(
                text = "AI Baby Monitor",
                fontSize = 44.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Subtitle
            Text(
                text = "Watch your baby anytime at home.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Divider
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(0.5.dp)
                    .background(Color.White)
            )
            // "How to Use" Button
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("How to Use", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
            }
        }

        // Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.SpaceBetween,
//            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Baby Option
            OptionCard(
                title = "Baby",
                description = "",
                gradientColors = listOf(
                    Color(0xFFF57C00).copy(alpha = 0.9f),
                    Color(0xFFFFA726).copy(alpha = 0.6f)
                ),
                iconRes = R.drawable.bab,
                onClick = onBabyClick
            )

            // Parents Option
            OptionCard(
                title = "Parents",
                description = "",
                gradientColors = listOf(
                    Color(0xFF8E24AA).copy(alpha = 0.5f),
                    Color(0xFFBA68C8).copy(alpha = 0.3f)
                ),
                iconRes = R.drawable.pare,
                onClick = onParentClick
            )
        }
        // Show Dialog
        if (showDialog) {
            HowToUseDialog(onDismiss = { showDialog = false })
        }
    }
}

@Composable
fun HowToUseDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "How to Use AI Baby Monitor",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Baby Section
                Text(
                    text = "Baby",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "1- Click 'Baby' to start monitoring your baby. \n" +
                            "2- After connection live stream starts. \n" +
                            "3- Both devices connect with the same internet then your connection was successful",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Parents Section
                Text(
                    text = "Parents",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "1- Click 'Parents' to monitor your baby.\n" +
                            "2- Scan Qr code from baby device. \n" +
                            "3- After scanning the streaming starts. \n" +
                            "4- From setting option You can select different filters and alarms. \n" +
                            "5- From parent side You can turn flashlight ON/OFF of baby device. \n" +
                            "6- You can also Dim/High brightness of baby device from here.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Pairing Instructions
                Text(
                    text = "• This App should be install on Two devices. \n" +
                            "• Make sure both devices must be connected with the same Internet Connection.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onDismiss() },
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun OptionCard(
    title: String,
    description: String,
    gradientColors: List<Color>,
    iconRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .background(
                brush = Brush.linearGradient(gradientColors),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = onClick,
                indication = null, // Removes the click effect
                interactionSource = remember { MutableInteractionSource() } // No interaction source
            ) // Handle click event
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "$title Icon",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.W200,
            color = Color.White,
        )
        Text(
            text = description,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewParentChildModeScreen() {
    ParentChildModeScreen(
        onBabyClick = {},
        onParentClick = {}
    )
}



