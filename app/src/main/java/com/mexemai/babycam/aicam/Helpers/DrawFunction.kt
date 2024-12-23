package com.mexemai.babycam.aicam.Helpers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

@Composable
/* ORIGINAL
fun DrawFunction(k1: Offset, k2: Offset, k3: Offset, k4: Offset) {
    // Initial points
    var p1 by remember { mutableStateOf(Offset(0f, 0f)) }
    var p2 by remember { mutableStateOf(Offset(0f, 0f)) }
    var p3 by remember { mutableStateOf(Offset(0f, 0f)) }
    var p4 by remember { mutableStateOf(Offset(0f, 0f)) }

    val pointsP = arrayOf(p1, p2, p3, p4)
    var pointsQ by remember { mutableStateOf(arrayOf(k1, k2, k3, k4)) }

    // Animatable instances for each point
    val animatedPoints = remember {
        pointsP.map { point ->
            Animatable(point.x) to Animatable(point.y)
        }    }

    // Animation specification

    val animationSpec = tween<Float>(durationMillis = 2)

    LaunchedEffect(k1, k2, k3, k4) {
        pointsQ = arrayOf(k1, k2, k3, k4)
        // Launch animations for all points at the same time
        animatedPoints.forEachIndexed { index, (animatableX, animatableY) ->
            val targetPoint = pointsQ[index]
            launch {
                animatableX.animateTo(targetPoint.x, animationSpec)
            }
            launch {
                animatableY.animateTo(targetPoint.y, animationSpec)
            }
        }

        // Wait for the animation to complete
        delay(animationSpec.durationMillis.toLong())
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        animatedPoints.forEachIndexed { index, (animatableX, animatableY) ->
            val point = Offset(animatableX.value, animatableY.value)
            val nextIndex = (index + 1) % animatedPoints.size
            val nextPoint = Offset(animatedPoints[nextIndex].first.value, animatedPoints[nextIndex].second.value)

            // Draw line between current point and next point
            drawLine(
                color = Color.Blue,
                start = point,
                end = nextPoint,
                strokeWidth = 12f
            )

            // Draw the circle
            drawCircle(
                color = Color.Red,
                center = point,
                radius = 8f
            )
        }
    }
}
*/

/*

fun DrawFunction(detectionResults: List<DetectionResult>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        detectionResults.forEach { detection ->
            val boundingBox = detection.boundingBox
            val label = detection.label
            val score = detection.score

            // Draw a rectangle around the detected object
            drawRect(
                color = Color.Green,
                topLeft = Offset(boundingBox.left, boundingBox.top),
                size = Size(boundingBox.width(), boundingBox.height()),
                style = Stroke(width = 4f)
            )

            // Draw the label and score text near the bounding box
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 32f
                }
                drawText("$label: ${"%.2f".format(score)}", boundingBox.left, boundingBox.top - 10f, textPaint)
            }
        }
    }
}


*/


fun DrawFunction(q1: Offset, q2: Offset, q3: Offset, q4: Offset, drawableText: String) {
    // Define points of the bounding box
    val points = listOf(q1, q2, q3, q4)

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw the bounding box by connecting the four corner points
        points.forEachIndexed { index, point ->
            val nextPoint = points[(index + 1) % points.size]
            drawLine(
                color = Color.Red,
                start = point,
                end = nextPoint,
                strokeWidth = 5f
            )
        }

        // Calculate bottom left and right points for the label background
        val bottomLeft = q3
        val bottomRight = q4

        // Draw the label background rectangle above the bottom edge of the bounding box
        val labelHeight = 80f // Increased height to fit larger text
        drawRect(
            color = Color.Red,
            topLeft = Offset(bottomLeft.x, bottomLeft.y - labelHeight),
            size = Size(
                width = bottomRight.x - bottomLeft.x,
                height = labelHeight
            )
        )

        // Draw the label text inside the background rectangle, centered vertically and horizontally
        drawIntoCanvas { canvas ->
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE // Set to white for visibility
                textSize = 40f // Adjusted to fit comfortably inside the rectangle
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }

            // Calculate center of the rectangle for positioning the text
            val textX = (bottomLeft.x + bottomRight.x) / 2 // Center horizontally
            val textY = bottomLeft.y - labelHeight / 2 + textPaint.textSize / 3 // Center vertically within the rectangle

            // Draw the label text
            canvas.nativeCanvas.drawText(
                drawableText.ifEmpty { "Label" }, // Use default text if drawableText is empty
                textX,
                textY,
                textPaint
            )
        }
    }
}
