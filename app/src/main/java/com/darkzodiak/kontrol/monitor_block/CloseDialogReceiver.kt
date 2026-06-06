package com.darkzodiak.kontrol.monitor_block

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.darkzodiak.kontrol.core.data.AppWideBroadcastReceiver

class CloseDialogReceiver(
    private val onHomePress: () -> Unit,
    private val onRecentsPress: () -> Unit
): AppWideBroadcastReceiver() {
    override val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason = intent.getStringExtra("reason")
            when (reason) {
                "homekey" -> onHomePress()
                "recentapps" -> onRecentsPress()
                else -> Unit
            }
        }
    }
}