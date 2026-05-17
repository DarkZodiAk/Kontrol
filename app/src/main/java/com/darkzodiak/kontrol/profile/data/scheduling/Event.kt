package com.darkzodiak.kontrol.profile.data.scheduling

import kotlinx.coroutines.Job
import java.time.LocalDateTime

class Event(val job: Job, val scheduledAt: LocalDateTime)
