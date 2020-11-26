package com.gtgt.pokerjacks.extensions

import com.github.salomonbrys.kotson.fromJson
import io.socket.client.Socket


inline fun Socket.onNoResponse(
    event: String,
    crossinline callback: () -> Unit
) {
    on(event) {
        callback()
    }
}

inline fun <reified T> Socket.on(event: String, crossinline callback: (T) -> Unit) where T : Any {
    on(event) {
        if (it.isNotEmpty()) {
            log("SocketIo, $event", it.toJson())
            callback(it[0] as T)
        } else {
            log("SocketIo, $event", it.toJson())
            callback(event as T)
        }
    }
}


inline fun <reified T> Socket.onModel(
    event: String,
    crossinline callback: (T) -> Unit
) where T : Any {
    on(event) {
        if (it.isNotEmpty()) {
            val response = it[0].toString()
            log("SocketIo, $event", response)
            callback(gson.fromJson(response))
        }
    }
}