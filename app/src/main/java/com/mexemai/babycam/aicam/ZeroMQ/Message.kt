package com.mexemai.babycam.aicam.ZeroMQ

import com.mexemai.babycam.aicam.enums.MessageType

data class Message(
    val type: MessageType,
    val payload: ByteArray
)
