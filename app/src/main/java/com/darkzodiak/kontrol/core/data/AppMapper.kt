package com.darkzodiak.kontrol.core.data

import com.darkzodiak.kontrol.core.data.local.entity.AppEntity
import com.darkzodiak.kontrol.core.domain.App

object AppMapper {

    fun AppEntity.entityToDomain(): App {
        return App(
            id = id ?: App.DEFAULT_ID,
            packageName = packageName,
            title = title,
            icon = icon
        )
    }

    fun entityListToDomainList(list: List<AppEntity>): List<App> {
        return list.filter { it.id != null }.map { it.entityToDomain() }
    }
}