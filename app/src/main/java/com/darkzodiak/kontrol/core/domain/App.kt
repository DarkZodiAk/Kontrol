package com.darkzodiak.kontrol.core.domain

data class App (
    val id: Long = DEFAULT_ID,
    val packageName: String = "",
    val title: String = "",
    val icon: String = "",
    val isDeleted: Boolean = false
) {
    companion object {
        const val DEFAULT_ID = -1L
    }
}