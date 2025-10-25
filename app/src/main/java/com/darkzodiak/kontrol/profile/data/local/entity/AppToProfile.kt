package com.darkzodiak.kontrol.profile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.darkzodiak.kontrol.core.data.local.entity.App

@Entity(
    primaryKeys = ["profileId", "appId"],
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = App::class,
            parentColumns = ["id"],
            childColumns = ["appId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AppToProfile(
    val profileId: Long,
    val appId: Long
)
