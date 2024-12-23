package com.mexemai.babycam.aicam.IP_CODE

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

fun getIPAddress(useIPv4: Boolean = true): String? {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress
                    val isIPv4 = addr is Inet4Address

                    if (useIPv4) {
                        if (isIPv4) {
                            return sAddr
                        }
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // Drop IP6 zone suffix
                            return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                0,
                                delim
                            ).uppercase(Locale.getDefault())
                        }
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
}