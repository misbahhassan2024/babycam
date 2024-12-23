package com.mexemai.babycam.aicam.ZeroMQ

import android.graphics.Bitmap
import android.util.Log
import com.mexemai.babycam.aicam.enums.MessageType
import org.zeromq.SocketType
import org.zeromq.ZMQ


class Router(private val ip: String) {
    private val context = ZMQ.context(1)
    private val router = context.socket(SocketType.ROUTER)
    private var is_running = false


    init {
        router.bind("tcp://$ip")
        Log.d("Router", "IPADDRESS: $ip")
    }

    fun set_running(bol : Boolean){
        is_running = bol
    }

    fun get_running(): Boolean{
        return is_running
    }

    suspend fun start(onMessageReceived: (String, Message) -> Unit) {
        while (is_running){//(!Thread.currentThread().isInterrupted) {
            try {
                val clientId = router.recvStr()
                val data = router.recv()
                val message = MessageUtils.deserializeMessage(data)
                onMessageReceived(clientId, message)
            }
            catch (e:Exception){
                Log.d("exception ", "Error in $e")
            }

        }
    }

    fun sendSingleMessage(clientId: String, message: String) {
        val response = Message(
            type = MessageType.SINGLE,
            payload = message.toByteArray()
        )
        try{
            router.sendMore(clientId.toByteArray())
            router.send(MessageUtils.serializeMessage(response))
        }
        catch (e:Exception){}

    }

    fun sendBitmapToDealer(bitmap: Bitmap) {
        val frame = Message(
            type = MessageType.STREAM,
            payload = MessageUtils.bitmapToByteArray(bitmap)
        )
        try {
            router.sendMore("Parent".toByteArray())
            router.send(MessageUtils.serializeMessage(frame))
        }
        catch(e:Exception)
        {

        }

    }

    fun stop() {

        Log.d("exception", "Flow done here")
        router.setLinger(0)
        router.close()
        if(!context.isTerminated) {context.term()}

    }
}
