package com.darkzodiak.kontrol.presentation.permission

sealed interface PermissionAction {
    data class SendPermissionInfo(
        val permission: Permission,
        val hasPermission: Boolean
    ): PermissionAction

    object GoToApp: PermissionAction
}