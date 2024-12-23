package com.mexemai.babycam.aicam.Service

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.mexemai.babycam.aicam.enums.FlashOnOff
import com.mexemai.babycam.aicam.interfaces.FlashLight
import com.mexemai.babycam.aicam.interfaces.MessageReceiver
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream


class StreamServer(
    private val isQrCodeVisible: MutableState<Boolean>, // Add this to control QR code visibility
    private val context: Context,
    var messageReceiver: MessageReceiver
) : NanoHTTPD(8081)
{
    private var latestFrame: Bitmap? = null

    val message = mutableStateOf("")
    // New method to start receiving messages
    @RequiresApi(Build.VERSION_CODES.O)
    fun startReceivingMessages(isItFlashLight: Boolean, flashOnOff: FlashOnOff, flashLight: FlashLight?) {
        val pipedOutputStream = PipedOutputStream()
        val pipedInputStream = PipedInputStream(pipedOutputStream)

        Log.d("pipe", "${pipedOutputStream}")
        if(isItFlashLight){
            sendMessage(pipedInputStream,
                pipedOutputStream,
//                message.toString()
                isItFlashLight,
                flashOnOff,
                flashLight)
        }else {
            Thread {
                receiveMessages(
                    pipedInputStream,
                    pipedOutputStream,
//                message.toString()
                    isItFlashLight,
                    flashOnOff,
                    flashLight
                )
            }.start()
        }

    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        return when (uri) {
            "/message" -> newFixedLengthResponse(Response.Status.OK, "text/plain",
                message.value // Access the current value of the message
            )
            else -> newChunkedResponse(
                Response.Status.OK,
                "multipart/x-mixed-replace; boundary=--frame",
                streamMJPEG()
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun receiveMessages(
        inputStream: PipedInputStream,
        outputStream: PipedOutputStream,
//                                message: String
        isItFlashLight: Boolean,
        flashOnOff: FlashOnOff,
        flashLight: FlashLight?
    ) {
        val buffer = ByteArray(1024) // Buffer to hold incoming data
        try {
            while (true) {
             ///////////////////////////////////////////////////////////////////////////////////

                outputStream.write(("PING").toByteArray())



                //////////////////////////////////////////////////////////////////////////////////////
                val bytesRead = inputStream.read(buffer)
//                Log.d("parent", "Received message 1 $bytesRead")
//
//              Log.d("parent", "Received message")

                outputStream.write((flashOnOff.toString()).toByteArray())

                val receivedMessage = String(buffer, 0, bytesRead).trim()
                if (receivedMessage.isNotEmpty()) {
//                    Log.d("StreamServer2", "Received: $isItFlashLight")
//                    if(isItFlashLight) {// Log the received message
//                        flashLight?.flashOnOff(message = flashOnOff)
//                    }
//                    Log.d("StreamServer2", "Received: $receivedMessage") // Log the received message
//                    sendMessage(receivedMessage) // Update the message state
                }
            }
        } catch (e: Exception) {
            Log.e("parent", "Error in receiving messages: ${e.message}")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                Log.e("parent", "Error closing input stream: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(
        inputStream: PipedInputStream,
        outputStream: PipedOutputStream,
//                                message: String
        isItFlashLight: Boolean,
        flashOnOff: FlashOnOff,
        flashLight: FlashLight?
    ) {
        val buffer = ByteArray(1024) // Buffer to hold incoming data
        try {
            while (true) {
                ///////////////////////////////////////////////////////////////////////////////////

                outputStream.write(("1").toByteArray())



                //////////////////////////////////////////////////////////////////////////////////////
                val bytesRead = inputStream.read(buffer)
                Log.d("parent", "Received message 1 $bytesRead")

                Log.d("parent", "Received message")



                val receivedMessage = String(buffer, 0, bytesRead).trim()
                if (receivedMessage.isNotEmpty()) {
                    Log.d("StreamServer2", "Received: $isItFlashLight")
//                    if(isItFlashLight) {// Log the received message
//                        flashLight?.flashOnOff(message = flashOnOff)
//                    }
//                    Log.d("StreamServer2", "Received: $receivedMessage") // Log the received message
//                    sendMessage(receivedMessage) // Update the message state
                }
            }
        } catch (e: Exception) {
            Log.e("parent", "Error in receiving messages: ${e.message}")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                Log.e("parent", "Error closing input stream: ${e.message}")
            }
        }
    }


    private fun streamMJPEG(): InputStream {   // using in baby app
        val pipedOutputStream = PipedOutputStream()
        val pipedInputStream = PipedInputStream(pipedOutputStream)

        // Create a thread to write frames to the output stream
        Thread {
            Log.d("StreamServ", "Starting MJPEG stream thread")
            try {
                while (true) {
                    latestFrame?.let { frame ->
                        Log.d("StreamServ", "Preparing to send frame")

                        val jpegBytes = encodeJPEG(frame)

                        try {
                            // Write the MJPEG frame with boundary
                            pipedOutputStream.write(("--frame\r\n").toByteArray())
                            pipedOutputStream.write(("Content-Type: image/jpeg\r\n").toByteArray())
                            pipedOutputStream.write(("Content-Length: ${jpegBytes.size}\r\n\r\n").toByteArray())
                            pipedOutputStream.write(jpegBytes)
                            pipedOutputStream.write(("\r\n").toByteArray())

                            // Flush the stream to ensure the data is sent
                            pipedOutputStream.flush()

                            // Log frame was sent
                            Log.d("StreamServ", "Frame sent successfully")

                            // Hide the QR code after sending the first frame
                            if (isQrCodeVisible.value) {
                                isQrCodeVisible.value = false
                            }
                            else {

                            }
                        } catch (e: Exception) {
                            Log.e("StreamServ", "Error sending frame: ${e.message}")
                        }
                    }

                    // Sleep to control the frame rate
                    Thread.sleep(100) // 10 FPS
                }
            } catch (e: Exception) {
                Log.e("StreamServ", "Streaming thread error: ${e.message}")
            } finally {
                try {
                    pipedOutputStream.close()
                } catch (e: IOException) {
                    Log.e("StreamServ", "Error closing output stream: ${e.message}")
                }
            }
        }.start()

        return pipedInputStream
    }

    fun updateFrame(frame: Bitmap) {
        latestFrame = frame
        Log.d("StreamServ", "Frame updated: ${frame.width}x${frame.height}")
    }

    private fun encodeJPEG(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return outputStream.toByteArray()
    }
}
