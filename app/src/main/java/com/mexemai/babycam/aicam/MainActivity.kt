package com.mexemai.babycam.aicam

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mexemai.babycam.aicam.Helpers.NextScreenActivity
import com.mexemai.babycam.aicam.ui.theme.Compose_IpTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var animationStarted by mutableStateOf(false)  // Track whether the animation has already started

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_IpTheme {
                BabyMonitorScreen(
                    animationStarted = animationStarted,
                    onAnimationComplete = {
                        // Update the state to true when animation completes
                        animationStarted = true
                    }
                )
            }
        }

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset animation state when returning to the screen
        animationStarted = false
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
@Composable
        /*
        fun BabyMonitorScreen() {
            val context = LocalContext.current



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
                    ) // Set background color to blue
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Button at the bottom
                    Button(
                        onClick = {
                            val intent = Intent(context, NextScreenActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(48.dp)
                            .padding(start = 8.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8E24AA).copy(alpha = 0.5f),
                                        Color(0xFFBA68C8).copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(text = "Start Now", color = Color.Black, fontSize = 16.sp)
                    }
                }
            }
        }
        */

/*

fun BabyMonitorScreen() {
    val context = LocalContext.current

    val text = "AI Baby Monitor"
    val characterOffsets = remember { text.map { Animatable(500f) } }
    // Define animation states
    val logoOffsetX = remember { Animatable(500f) }
    val text2OffsetY = remember { Animatable(500f) }
    var showLoading by remember { mutableStateOf(false) }
    val sandHeight = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate the logo
        logoOffsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
        // Animate the title after logo
        */
/*characterOffsets.forEachIndexed { index, animatable ->
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 300, // Duration for each character
                    easing = LinearOutSlowInEasing
                )
            )
            delay(100) // Delay before animating the next character
        }*//*

        text2OffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
        // Show loading animation for 2 seconds after text animation
        */
/*  showLoading = true
          delay(2000)
          showLoading = false
          // Navigate to the next activity
          val intent = Intent(context, NextScreenActivity::class.java)
          context.startActivity(intent)*//*

        // Show hourglass animation for 2 seconds
        showLoading = true
        sandHeight.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
        )
        showLoading = false
        // Navigate to the next activity
        val intent = Intent(context, NextScreenActivity::class.java)
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBA68C8),
                        Color(0xFF00BBD4),
                        Color(0xFF0288D1),
                        Color(0xFFFFA726).copy(alpha = 0.6f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo at the center with animation
            Image(
                painter = painterResource(id = R.drawable.log), // Replace with your logo drawable
                contentDescription = "App Logo",
                modifier = Modifier
                    .offset(y = logoOffsetX.value.dp)
                    .size(260.dp)
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Title below the logo with animation
            */
/*Row(horizontalArrangement = Arrangement.Center) {
                text.forEachIndexed { index, char ->
                    Text(
                        text = char.toString(),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = characterOffsets[index].value.dp)
//                        modifier = Modifier.offset(y = characterOffsets[index].value.dp)
                    )
                }
            }*//*

            Text(
                text = "AI Baby Monitor",
                fontSize = 40.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = text2OffsetY.value.dp)
            )
            Spacer(modifier = Modifier.weight(2f))


            // Show loading animation when text and logo animation complete
            if (showLoading) {
                CircularLoadingAnimation()
            }
        }
    }
}


*/

fun BabyMonitorScreen(animationStarted: Boolean, onAnimationComplete: () -> Unit) {
    val context = LocalContext.current

    val text = "AI Baby Monitor"
    val characterOffsets = remember { text.map { Animatable(500f) } }
    // Define animation states
    val logoOffsetX = remember { Animatable(500f) }
    val text2OffsetY = remember { Animatable(500f) }
    var showLoading by remember { mutableStateOf(false) }
    val sandHeight = remember { Animatable(0f) }

    // Trigger animation immediately when animationStarted is false
    if (!animationStarted) {
        LaunchedEffect(animationStarted) {  // LaunchedEffect triggered when state changes
            // Animate the logo
            logoOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
            )
            text2OffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
            )
            showLoading = true
            sandHeight.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
            )
            showLoading = false
            // Notify when animation is complete
            onAnimationComplete()

            // Navigate to the next activity
            val intent = Intent(context, NextScreenActivity::class.java)
            context.startActivity(intent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBA68C8),
                        Color(0xFF00BBD4),
                        Color(0xFF0288D1),
                        Color(0xFFFFA726).copy(alpha = 0.6f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo at the center with animation
            Image(
                painter = painterResource(id = R.drawable.log), // Replace with your logo drawable
                contentDescription = "App Logo",
                modifier = Modifier
                    .offset(y = logoOffsetX.value.dp)
                    .size(260.dp)
            )

            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "AI Baby Monitor",
                fontSize = 40.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = text2OffsetY.value.dp)
            )
            Spacer(modifier = Modifier.weight(2f))

            // Show loading animation when text and logo animation complete
            CircularLoadingAnimation(isVisible = showLoading)
        }
    }
}
/*@SuppressLint("SuspiciousIndentation")
@Composable
fun CircularLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
        Canvas(modifier = Modifier.size(70.dp)) {
            val canvasSize = size.minDimension
            val arcStrokeWidth = canvasSize * 0.05f // Adjust stroke width based on canvas size
            val arcRadius = (canvasSize - arcStrokeWidth) / 2

            val gradientBrush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFBA68C8),
                    Color(0xFF00BBD4),
                    Color(0xFF0288D1),
                    Color(0xFFFFA726).copy(alpha = 0.6f)
                )
            )

            // Draw multiple rotating arcs
            for (i in 0..3) {
                drawArc(
                    brush = gradientBrush,
                    startAngle = rotationAngle.value + i * 90f, // Adjust angles for multiple arcs
                    sweepAngle = 60f, // Arc length
                    useCenter = false,
                    style = Stroke(width = arcStrokeWidth),
                    topLeft = Offset(
                        x = (canvasSize - arcRadius * 2) / 2,
                        y = (canvasSize - arcRadius * 2) / 2
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = arcRadius * 2,
                        height = arcRadius * 2
                    )
                )
            }
        }
    }*/

@SuppressLint("SuspiciousIndentation")
@Composable
fun CircularLoadingAnimation(isVisible: Boolean) {
    // Control the animation visibility based on the isVisible state
    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(70.dp)) {
        val canvasSize = size.minDimension
        val arcStrokeWidth = canvasSize * 0.05f // Adjust stroke width based on canvas size
        val arcRadius = (canvasSize - arcStrokeWidth) / 2

        val gradientBrush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFFBA68C8),
                Color(0xFF00BBD4),
                Color(0xFF0288D1),
                Color(0xFFFFA726).copy(alpha = 0.6f)
            )
        )

        // Draw multiple rotating arcs
        for (i in 0..3) {
            drawArc(
                brush = gradientBrush,
                startAngle = rotationAngle.value + i * 90f, // Adjust angles for multiple arcs
                sweepAngle = 60f, // Arc length
                useCenter = false,
                style = Stroke(width = arcStrokeWidth),
                topLeft = Offset(
                    x = (canvasSize - arcRadius * 2) / 2,
                    y = (canvasSize - arcRadius * 2) / 2
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = arcRadius * 2,
                    height = arcRadius * 2
                )
            )
        }
    }
}



/*
@Preview(showBackground = true)
@Composable
fun PreviewBabyMonitorScreen() {
    BabyMonitorScreen()
}
*/
