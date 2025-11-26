package com.darkzodiak.kontrol.profile.presentation.editRestriction

sealed interface EditRestrictionEvent {
    data class ShowWarning(val text: String): EditRestrictionEvent
}