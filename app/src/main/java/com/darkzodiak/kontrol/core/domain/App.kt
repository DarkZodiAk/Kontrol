package com.darkzodiak.kontrol.core.domain

data class App (
    val id: Long = DEFAULT_ID,
    val packageName: String = "",
    val title: String = "",
    val icon: String = "",
) {
    companion object {
        const val DEFAULT_ID = -1L
    }
}