package com.darkzodiak.kontrol

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.ui.theme.KontrolTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var installedApps: List<ApplicationInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installedApps = getAllInstalledApps()
        enableEdgeToEdge()
        setContent {
            KontrolTheme {

            }
        }
    }

    private fun getAllInstalledApps(): List<ApplicationInfo> {
        // Get the PackageManager instance
        val packageManager: PackageManager = packageManager

        // Get a list of all installed applications
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return apps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
    }
}

