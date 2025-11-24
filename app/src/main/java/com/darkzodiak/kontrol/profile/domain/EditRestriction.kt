package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import java.time.LocalDateTime

sealed interface EditRestriction {
    object NoRestriction: EditRestriction
    data class RandomText(val length: Int): EditRestriction
    data class Password(val password: String): EditRestriction
    data class UntilDate(val date: LocalDateTime, val stopAfterReachingDate: Boolean): EditRestriction
    data class UntilReboot(val stopAfterReboot: Boolean): EditRestriction

    fun isInstanceOf(type: EditRestrictionType) = when (type) {
        EditRestrictionType.NO_RESTRICTION -> this is NoRestriction
        EditRestrictionType.RANDOM_TEXT -> this is RandomText
        EditRestrictionType.PASSWORD -> this is Password
        EditRestrictionType.UNTIL_DATE -> this is UntilDate
        EditRestrictionType.UNTIL_REBOOT -> this is UntilReboot
    }

    fun toType() = when (this) {
        NoRestriction -> EditRestrictionType.NO_RESTRICTION
        is Password -> EditRestrictionType.PASSWORD
        is RandomText -> EditRestrictionType.RANDOM_TEXT
        is UntilDate -> EditRestrictionType.UNTIL_DATE
        is UntilReboot -> EditRestrictionType.UNTIL_REBOOT
    }
}