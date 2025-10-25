package com.darkzodiak.kontrol.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val packageName: String = "",
    val title: String = "",
    val icon: String = "",
)