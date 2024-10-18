package com.darkzodiak.kontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.darkzodiak.kontrol.data.AppObserver
import com.darkzodiak.kontrol.data.KontrolService
import com.darkzodiak.kontrol.presentation.NavRoot
import com.darkzodiak.kontrol.ui.theme.KontrolTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var appObserver: AppObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appObserver.update()
        enableEdgeToEdge()
        setContent {
            KontrolTheme {
                LaunchedEffect(true) {
                    if(hasUsageStatisticsPermission() && hasAccessibilityPermission()) {
                        appObserver.update()
                        startService(KontrolService.buildActionIntent(this@MainActivity, KontrolService.ACTION_START))
                    }
                }

                NavRoot(
                    navController = rememberNavController(),
                    hasPermissions = hasUsageStatisticsPermission() && hasAccessibilityPermission()
                )
                /*AppRoot(
                    navController = rememberNavController()
                )*/
            }
        }
    }
}

