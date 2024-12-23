package com.mexemai.babycam.aicam.interfaces

import android.graphics.Bitmap


interface MessageReceiver {
    fun singleMessageReceived(message: String)
    fun streamMessageReceived(message: Bitmap)
}