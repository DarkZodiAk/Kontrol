package com.darkzodiak.kontrol.scheduling

class EventRunnable(
    private val profileId: Long,
): Runnable {

    override fun run() {
        if (EventCache.shouldRun(this).not()) return

        EventCache.profileActualizer.actualize(profileId)

        EventCache.deleteEvent(this)
    }
}