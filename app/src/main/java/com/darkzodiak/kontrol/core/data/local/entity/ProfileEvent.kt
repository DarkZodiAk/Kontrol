package com.darkzodiak.kontrol.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProfileEvent(
    @PrimaryKey
    val id: Long? = null,
    val profileId: Long,
//    val eventType: ,
//    val time Long
)