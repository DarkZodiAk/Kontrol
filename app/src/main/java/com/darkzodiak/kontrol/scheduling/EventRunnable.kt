package com.darkzodiak.kontrol.scheduling

import java.time.LocalDateTime

class EventRunnable(
    private val profileId: Long,
    val eventTime: LocalDateTime
): Runnable {

    override fun run() {
        if (EventCache.shouldRun(this).not()) return

        EventCache.profileActualizer.actualize(profileId)

        EventCache.deleteEvent(this)
    }
}