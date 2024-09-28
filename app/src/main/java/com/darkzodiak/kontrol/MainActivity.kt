package com.darkzodiak.kontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.darkzodiak.kontrol.presentation.NavRoot
import com.darkzodiak.kontrol.ui.theme.KontrolTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KontrolTheme {
                NavRoot(navController = rememberNavController())
            }
        }
    }
}

