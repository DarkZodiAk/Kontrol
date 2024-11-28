package com.darkzodiak.kontrol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val packageName: String = "",
    val title: String = "",
    val icon: Int = 0,
)