package com.darkzodiak.kontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.darkzodiak.kontrol.permission.data.PermissionObserver
import com.darkzodiak.kontrol.navigation.NavRoot
import com.darkzodiak.kontrol.ui.theme.KontrolTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionObserver: PermissionObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KontrolTheme {
                NavRoot(navController = rememberNavController())
            }
        }
    }

    override fun onResume() {
        permissionObserver.updateAllPermissions()
        super.onResume()
    }
}

