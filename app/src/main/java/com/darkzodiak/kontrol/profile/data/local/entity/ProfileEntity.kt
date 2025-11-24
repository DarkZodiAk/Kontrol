package com.darkzodiak.kontrol.profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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

    val editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION,

    // Relevant for PASSWORD restriction
    val password: String? = null,

    // Relevant for RANDOM_TEXT restriction
    val randomTextLength: Int? = null,

    // Relevant for UNTIL_DATE restriction
    val restrictUntilDate: LocalDateTime? = null,
    val stopAfterReachingUntilDate: Boolean? = null,

    // Relevant for UNTIL_REBOOT restriction
    val stopAfterReboot: Boolean? = null
)
