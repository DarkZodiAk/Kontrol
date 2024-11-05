package com.darkzodiak.kontrol.domain.eventBus

sealed interface PermissionEvent {
    object GrantedAllPermissions: PermissionEvent
    object LostAccessibilityPermission: PermissionEvent
}