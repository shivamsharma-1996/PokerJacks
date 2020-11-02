package com.gtgt.pokerjacks.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import java.util.regex.Matcher
import java.util.regex.Pattern

class SmsReceiver(val callback: ((String) -> Unit)?) : BroadcastReceiver() {
    constructor() : this(null)

    var p: Pattern = Pattern.compile("(|^)\\d{4}")

    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.extras
        val pdus = data!!["pdus"] as Array<Any>?
        for (i in pdus!!.indices) {
            val smsMessage =
                SmsMessage.createFromPdu(pdus[i] as ByteArray)
            val sender = smsMessage.displayOriginatingAddress
            if (sender.contains("GTGT")) {
                val messageBody = smsMessage.messageBody
                if (messageBody != null) {
                    val m: Matcher = p.matcher(messageBody)
                    if (m.find()) {
                        callback?.invoke(m.group(0))
                    }
                }
            }
        }
    }
}