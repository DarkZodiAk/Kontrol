package com.darkzodiak.kontrol.profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import java.time.LocalDateTime

@Entity
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String = "",

    val state: ProfileStateType = ProfileStateType.STOPPED,
    val pausedUntil: LocalDateTime? = null,


    /* Everything related to app restrictions. All properties start from 'a' */
    val appRestrictionType: AppRestrictionType = AppRestrictionType.SIMPLE_BLOCK,

    // Relevant for PASSWORD restriction
    val aPassword: String? = null,

    // Relevant for RANDOM_TEXT restriction
    val aRandomTextLength: Int? = null,


    /* Everything related to profile edit restrictions. All properties start from 'e' */
    val editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION,

    // Relevant for PASSWORD restriction
    val ePassword: String? = null,

    // Relevant for RANDOM_TEXT restriction
    val eRandomTextLength: Int? = null,

    // Relevant for UNTIL_DATE restriction
    val eRestrictUntilDate: LocalDateTime? = null,
    val eStopAfterReachingUntilDate: Boolean? = null,

    // Relevant for UNTIL_REBOOT restriction
    val eStopAfterReboot: Boolean? = null
)
