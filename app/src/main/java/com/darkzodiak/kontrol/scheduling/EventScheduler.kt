package com.darkzodiak.kontrol.scheduling

import android.os.Handler
import android.os.Looper
import com.darkzodiak.kontrol.core.data.millisUntil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventScheduler @Inject constructor(
    private val planner: EventPlanner
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    // TODO(): Pass ProfileEntity directly to the scheduler?
    fun addEvent(profileId: Long) = scope.launch {
        createEvent(profileId)
    }

    fun updateEvent(profileId: Long) = scope.launch {
        EventCache.deleteEvent(profileId)
        createEvent(profileId)
    }

    fun deleteEvent(profileId: Long) = scope.launch {
        EventCache.deleteEvent(profileId)
    }

    private suspend fun createEvent(profileId: Long) {
        val time = planner.getNearestEventTime(profileId) ?: return
        val runnable = EventRunnable(profileId)
        EventCache.addEvent(profileId, runnable)
        val delay = millisUntil(time)
        handler.postDelayed(runnable, delay)
    }
}