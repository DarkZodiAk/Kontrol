package com.darkzodiak.kontrol.profile.domain

import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType

sealed interface AppRestriction {
    object SimpleBlock: AppRestriction
    data class RandomText(val length: Int): AppRestriction
    data class Password(val password: String): AppRestriction

    fun isInstanceOf(type: AppRestrictionType) = when (type) {
        AppRestrictionType.SIMPLE_BLOCK -> this is SimpleBlock
        AppRestrictionType.RANDOM_TEXT -> this is RandomText
        AppRestrictionType.PASSWORD -> this is Password
    }

    fun toType() = when (this) {
        is Password -> AppRestrictionType.PASSWORD
        is RandomText -> AppRestrictionType.RANDOM_TEXT
        SimpleBlock -> AppRestrictionType.SIMPLE_BLOCK
    }
}