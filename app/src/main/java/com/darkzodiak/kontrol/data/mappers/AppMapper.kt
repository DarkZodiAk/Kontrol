package com.darkzodiak.kontrol.data.mappers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.darkzodiak.kontrol.data.local.entity.AppEntity
import com.darkzodiak.kontrol.domain.model.App
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager = context.packageManager

    fun appEntityToApp(appEntity: AppEntity): App {
        val appInfo = packageManager.getApplicationInfo(appEntity.packageName, 0)
        return App(
            id = appEntity.id,
            packageName = appEntity.packageName,
            title = appEntity.title,
            icon = appInfo.loadIcon(packageManager).toImageBitmap()
        )
    }

    fun appToAppEntity(app: App): AppEntity {
        return AppEntity(
            id = app.id,
            packageName = app.packageName,
            title = app.title
        )
    }
}

fun Drawable.toImageBitmap(): ImageBitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, intrinsicHeight, intrinsicWidth)
    draw(canvas)
    return bitmap.asImageBitmap()
}