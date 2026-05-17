package com.darkzodiak.kontrol.apps.data

import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.core.data.local.entity.AppEntity

object AppMapper {

    fun AppEntity.entityToDomain(): App {
        return App(
            id = id ?: App.DEFAULT_ID,
            packageName = packageName,
            title = title,
            icon = icon,
            isDeleted = isDeleted
        )
    }

    fun entityListToDomainList(list: List<AppEntity>): List<App> {
        return list.filter { it.id != null }.map { it.entityToDomain() }
    }
}