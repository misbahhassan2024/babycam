package com.mexemai.babycam.aicam.ZeroMQ

import android.graphics.Bitmap
import android.util.Log
import com.mexemai.babycam.aicam.enums.MessageType

import org.zeromq.SocketType
import org.zeromq.ZMQ

class Dealer(private val ip: String) {
    private val context = ZMQ.context(1)
    private val dealer = context.socket(SocketType.DEALER)

    private var is_running = false

    init {
        dealer.identity = "Parent".toByteArray(Charsets.UTF_8)
        dealer.connect("tcp://$ip")
        Log.d("Dealer", "IPADDRESS: $ip")

    }

    fun get_running() : Boolean{
        return is_running
    }

    fun set_running(bol: Boolean){
        is_running = bol
    }



    suspend fun start(onMessageReceived: (Message) -> Unit) {

        while (is_running){//(!Thread.currentThread().isInterrupted) {
            try {
                val data = dealer.recv()
                val message = MessageUtils.deserializeMessage(data)
                onMessageReceived(message)
            }
            catch (e:Exception){
                Log.e("Exception", "Error in start Dealer : $e")
            }

        }
    }

    fun sendSingleMessage(message: String) {
        val data = Message(
            type = MessageType.SINGLE,
            payload = message.toByteArray()
        )
        dealer.send(MessageUtils.serializeMessage(data))

    }

    fun sendBitmap(bitmap: Bitmap) {
        val frame = Message(
            type = MessageType.STREAM,
            payload = MessageUtils.bitmapToByteArray(bitmap)
        )
        dealer.send(MessageUtils.serializeMessage(frame))
    }

    fun stop() {
        dealer.setLinger(0)
        //dealer.disconnect("tcp://$ip")
        dealer.close()
        context.term()

    }
}

