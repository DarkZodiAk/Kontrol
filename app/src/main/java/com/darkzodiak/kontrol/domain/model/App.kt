package com.darkzodiak.kontrol.domain.model

import androidx.compose.ui.graphics.ImageBitmap

data class App(
    val id: Long? = null,
    val packageName: String = "",
    val title: String = "",
    val icon: ImageBitmap
)
