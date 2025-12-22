package com.darkzodiak.kontrol.scheduling

import com.darkzodiak.kontrol.profile.data.ProfileActualizer

object EventCache {
    private val shouldRun = mutableSetOf<Runnable>()

    private val profileToRunnable = hashMapOf<Long, EventRunnable>()
    private val runnableToProfile = hashMapOf<Runnable, Long>()

    lateinit var profileActualizer: ProfileActualizer

    fun addEvent(profileId: Long, runnable: EventRunnable) {
        shouldRun.add(runnable)
        profileToRunnable[profileId] = runnable
        runnableToProfile[runnable] = profileId
    }

    fun deleteEvent(runnable: Runnable) {
        shouldRun.remove(runnable)
        runnableToProfile.remove(runnable)
        val profileId = runnableToProfile[runnable] ?: return
        profileToRunnable.remove(profileId)
    }

    fun deleteEvent(profileId: Long) {
        profileToRunnable.remove(profileId)
        val runnable = profileToRunnable[profileId] ?: return
        shouldRun.remove(runnable)
        runnableToProfile.remove(runnable)
    }

    fun shouldRun(runnable: Runnable) = runnable in shouldRun

    fun getScheduledEventTimeForProfile(profileId: Long) = profileToRunnable[profileId]?.eventTime
}