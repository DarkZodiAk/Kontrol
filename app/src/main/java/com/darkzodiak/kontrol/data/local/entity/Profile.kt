package com.darkzodiak.kontrol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val enabled: Boolean = false
)
