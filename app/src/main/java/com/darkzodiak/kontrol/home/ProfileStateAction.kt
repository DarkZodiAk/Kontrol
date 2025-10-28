package com.darkzodiak.kontrol.home

sealed interface ProfileStateAction {
    object Activate: ProfileStateAction
    object Stop: ProfileStateAction
    object Pause: ProfileStateAction
    object Resume: ProfileStateAction
    data class ActivateAfter(val delay: Long): ProfileStateAction
}