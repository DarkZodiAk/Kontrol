package com.darkzodiak.kontrol.scheduling

import kotlinx.coroutines.Job
import java.time.LocalDateTime

class Event(val job: Job, val scheduledAt: LocalDateTime)
