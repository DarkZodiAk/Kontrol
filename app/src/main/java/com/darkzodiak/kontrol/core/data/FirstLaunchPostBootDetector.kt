package com.darkzodiak.kontrol.core.data

import android.content.Context
import android.os.SystemClock
import androidx.core.content.edit
import kotlin.math.abs

class FirstLaunchPostBootDetector(context: Context) {
    private val pref = context.getSharedPreferences("BOOT", Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        val bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val storedBootTime = pref.getLong(BOOT_TIME, -1L)
        if(storedBootTime == -1L) {
            saveBootTime(bootTime)
            return true
        }
        if(abs(bootTime - storedBootTime) > 20) {
            saveBootTime(bootTime)
            return true
        }
        return false
    }

    private fun saveBootTime(time: Long) {
        pref.edit {
            putLong(BOOT_TIME, time)
        }
    }

    private companion object {
        const val BOOT_TIME = "BOOT_TIME"
    }
}