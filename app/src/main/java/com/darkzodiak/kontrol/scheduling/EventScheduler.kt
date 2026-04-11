package com.darkzodiak.kontrol.scheduling

import com.darkzodiak.kontrol.core.data.millisUntil
import com.darkzodiak.kontrol.profile.data.ProfileActualizer
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventScheduler @Inject constructor(
    private val planner: EventPlanner,
    private val profileActualizer: Lazy<ProfileActualizer>
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val profileToEvent = ConcurrentHashMap<Long, Event>()

    fun upsertEvent(profileId: Long) = scope.launch {
        createEvent(profileId)
    }

    fun deleteEvent(profileId: Long) {
        val event = profileToEvent[profileId] ?: return
        event.job.cancel()
        profileToEvent.remove(profileId)
    }

    private suspend fun createEvent(profileId: Long) {
        val time = planner.getNearestEventTime(profileId) ?: return
        profileToEvent[profileId]?.let { event ->
            if (event.scheduledAt == time) return
            event.job.cancel()
        }

        val delay = millisUntil(time)
        val job = createDelayedJob(profileId, delay)
        profileToEvent[profileId] = Event(job, time)
    }

    private fun createDelayedJob(profileId: Long, delayMillis: Long): Job {
        return scope.launch {
            delay(delayMillis)
            profileActualizer.get().actualize(profileId)
            profileToEvent.remove(profileId)
        }
    }
}