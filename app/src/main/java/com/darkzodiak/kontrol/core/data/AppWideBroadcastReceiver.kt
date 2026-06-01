package com.darkzodiak.kontrol.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build

abstract class AppWideBroadcastReceiver: BroadcastReceiver() {
    abstract val intentFilter: IntentFilter
    fun register(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
            context.registerReceiver(this, intentFilter)
        } else {
            context.registerReceiver(this, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        }
    }
}