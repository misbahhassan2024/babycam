package com.mexemai.babycam.aicam.ZeroMQ

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mexemai.babycam.aicam.enums.MessageType
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object MessageUtils {
    // Convert Int to ByteArray
    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(value).array()
    }

    // Convert ByteArray to Int
    private fun byteArrayToInt(bytes: ByteArray): Int {
        return ByteBuffer.wrap(bytes).int
    }

    // Convert Bitmap to ByteArray
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    // Convert ByteArray to Bitmap
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // Serialize a Message (type + payload)
    fun serializeMessage(message: Message): ByteArray {
        val typeBytes = intToByteArray(message.type.ordinal)
        return typeBytes + message.payload
    }

    // Deserialize a Message (extract type and payload)
    fun deserializeMessage(data: ByteArray): Message {
        val type = MessageType.values()[byteArrayToInt(data.copyOfRange(0, 4))]
        val payload = data.copyOfRange(4, data.size)
        return Message(type, payload)
    }
}

