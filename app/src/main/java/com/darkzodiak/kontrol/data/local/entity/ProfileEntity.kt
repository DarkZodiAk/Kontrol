package com.darkzodiak.kontrol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.darkzodiak.kontrol.data.local.EditRestrictionType

@Entity
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String = "",
    val isEnabled: Boolean = false,
    val editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION,
    val editRestrictionValue: String = ""
)
