package com.gtgt.pokerjacks.socket

import android.os.Handler
import android.os.HandlerThread
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonElement
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.utils.NetworkStateReceiver
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import java.util.concurrent.Executors

val socketInstance = SocketIoInstance(BuildConfig.SOCKET_URL)

class SocketIoInstance(private val url: String) :
    NetworkStateReceiver.NetworkStateReceiverListener {
    override fun networkAvailable() {
    }

    override fun networkUnavailable() {

    }

    private val socketHandlerThread = HandlerThread("socketHandlerThread")
    val socketHandler by lazy { Handler(socketHandlerThread.looper) }

    val executor = Executors.newFixedThreadPool(4)

    private val opts = IO.Options()
    val socket: Socket

//    private val connectionDelayHandler = Handler()

    init {
        socketHandlerThread.start()

        opts.forceNew = true
        opts.reconnection = true

        socket = IO.socket(url, opts)
        socketHandler.post {
            socket.onNoResponse(Socket.EVENT_CONNECT) {
                log("SocketIo, connected", socket.connected())
                val userId = retrieveString("USER_ID")

                socket.emit(
                    "authenticateUser", jsonObject(
                        "token" to getModel<JsonElement>("loginInfo")?.get("token")?.string,
                        "data" to jsonObject("userId" to userId, "userUniqueId" to userId)
                    ), Ack {
                        log("SocketIo:response, authenticateUser", it[0])
                    }
                )
                notifyConnectionAvailable()
            }

            socket.onNoResponse(Socket.EVENT_RECONNECT) {
                runOnMain {
                    log("SocketIo:reconnect", socket.connected().toString())
                    notifyReConnection()
                }
            }

            socket.onNoResponse(Socket.EVENT_DISCONNECT) {

                runOnMain {
//                    connectionDelayHandler.removeCallbacksAndMessages(null)
//                    connectionDelayHandler.postDelayed({
                    if (!socket.connected())
                        notifyConnectionUnAvailable()
//                    }, 0/*3000*/)
                    notifyNetworkSpeed(-1)
                }
            }

            socket.onNoResponse(Socket.EVENT_CONNECT_TIMEOUT) {}
//        socket.onNoResponse(Socket.EVENT_PING) {}
            socket.onModel<Int>(Socket.EVENT_PONG) {
                //            log("SocketIo,timetaken", it)
                runOnMain {
                    notifyNetworkSpeed(it)
                }
            }
            socket.onNoResponse(Socket.EVENT_ERROR) {}
        }
    }

    fun connect() {
        socketHandler.post {
            if (!socket.connected()) {
                socket.connect()
            }
        }
    }

    fun disConnect() {
        socketHandler.post {
            socket.disconnect()
        }
    }

    var listeners: MutableSet<SocketConnectionChangeListener> = HashSet()
    fun addSocketChangeListener(socketConnectionChangeListener: SocketConnectionChangeListener) {
        socketHandler.post {
            if (socket.connected()) {
                runOnMain {
                    socketConnectionChangeListener.connectionAvailable()
                }
            } else {
                socket.connect()
                if (!socket.connected()) {
                    runOnMain {
                        socketConnectionChangeListener.connectionUnavailable()
                    }
                }
            }

            listeners.add(socketConnectionChangeListener)
        }
    }

    fun removeSocketChangeListener(socketConnectionChangeListener: SocketConnectionChangeListener) {
        listeners.remove(socketConnectionChangeListener)
    }

    private fun notifyConnectionAvailable() {
        runOnMain {
            listeners.forEach { it.connectionAvailable() }
        }
    }

    private fun notifyConnectionUnAvailable() {
        listeners.forEach { it.connectionUnavailable() }
    }

    private fun notifyReConnection() {
        listeners.forEach { it.reconnected() }
    }

    private fun notifyNetworkSpeed(speed: Int) {
        listeners.forEach { it.networkSpeed(speed) }
    }

    interface SocketConnectionChangeListener {
        fun connectionAvailable()
        fun reconnected()
        fun connectionUnavailable()
        fun networkSpeed(speed: Int)
    }
}