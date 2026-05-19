package com.darkzodiak.kontrol.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.darkzodiak.kontrol.core.data.local.types.AppRestrictionType
import com.darkzodiak.kontrol.core.data.local.types.EditRestrictionType
import com.darkzodiak.kontrol.core.data.local.types.ProfileStateType
import java.time.LocalDateTime

@Entity
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String = "",

    val state: ProfileStateType = ProfileStateType.STOPPED,
    val pausedUntil: LocalDateTime? = null,

    val appRestrictionType: AppRestrictionType = AppRestrictionType.SIMPLE_BLOCK,

    val aPassword: String? = null,

    val aRandomTextLength: Int? = null,

    val editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION,

    val ePassword: String? = null,

    val eRandomTextLength: Int? = null,

    val eUntilDate: LocalDateTime? = null,
    val eStopAfterReachingUntilDate: Boolean? = null,

    val eStopAfterReboot: Boolean? = null
)