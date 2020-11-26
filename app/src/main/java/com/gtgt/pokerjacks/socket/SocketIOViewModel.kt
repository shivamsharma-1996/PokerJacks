package com.gtgt.pokerjacks.socket

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonElement
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.utils.ProgressBarHandler
import io.socket.client.Ack
import kotlin.math.abs

typealias ChannelCallbackType<T> = (T) -> Unit

open class SocketIOViewModel() : BaseViewModel() {
    val socketIO = socketInstance
    val events = mutableSetOf<String>()

    var previousSocketResponseReceivedAt = 0L

    inline fun <reified T> on(
        eventName: String,
        crossinline callback: (T) -> Unit
    ) where T : Any {
        executeOnBackground(Runnable {
            socketIO.connect()
            socketIO.socket.off(eventName)
            events.add(eventName)
            socketIO.socket.onModel<T>(eventName) {
                val time = System.currentTimeMillis()
                if (abs(time - previousSocketResponseReceivedAt) < 250L) {
                    previousSocketResponseReceivedAt = time + 250L
                    socketIO.socketHandler.postDelayed({
                        try {
                            callback(it)
                        } catch (ex: Exception) {
                        }
                    }, 250)
                }
                callback(it)
            }
        })
    }

    fun executeOnBackground(runnable: Runnable) {
        socketIO.executor.execute(runnable)
    }

    inline fun <reified T> emit(
        eventName: String,
        data: Any,
        showLoading: Boolean = false,
        noinline callback: ChannelCallbackType<T?>? = null
    ) where T : Any {
        socketIO.connect()
        val progressBarHandler: ProgressBarHandler? =
            activity?.let { if (showLoading) ProgressBarHandler(it) else null }

        runOnMain {
            progressBarHandler?.show()
        }

        executeOnBackground(Runnable {
            val request = jsonObject(
                "token" to getModel<JsonElement>("loginInfo")?.get("token")?.string,
                "data" to data.toJsonTree()
            )

            log("SocketIo, $eventName", request.toJson())
            socketIO.socket.emit(eventName, request.toJson(), Ack {
                try {
                    if (activity?.isRunning() == true) {
                        runOnMain { progressBarHandler?.hide() }
                        if (it.isEmpty()) {
                            runOnMain { callback?.invoke(null) }
//                            callback?.invoke(null)
                            log(
                                "SocketIo:response, $eventName", "empty"
                            )
                        } else if (it[0] != null) {
                            log(
                                "SocketIo:response, $eventName", try {
                                    it[0].toString()
                                } catch (ex: Exception) {
                                    "null"
                                }
                            )

                            try {
                                val r: T = gson.fromJson(it[0].toString())
                                callback?.let {
                                    runOnMain {
                                        try {
                                            it(r)
                                        } catch (ex: Exception) {
                                        }
                                    }
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                } catch (ex: Exception) {
                }
            })
        })
    }

    fun disconnectChannels() {
        executeOnBackground(Runnable {
            events.forEach { socketIO.socket.off(it) }
        })
    }
}