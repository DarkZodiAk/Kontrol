package com.darkzodiak.kontrol.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darkzodiak.kontrol.data.local.entity.Profile

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenProfile: (Long) -> Unit,
    onNewProfile: () -> Unit
) {
    HomeScreen(
        profiles = viewModel.profiles.collectAsState().value,
        onAction = { action ->
            when(action) {
                HomeAction.NewProfile -> onNewProfile()
                is HomeAction.OpenProfile -> onOpenProfile(action.id)
                else -> Unit
            }
        }
    )
}

@Composable
fun HomeScreen(
    profiles: List<Profile>,
    onAction: (HomeAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->





        installedApps?.let { apps ->
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(apps) { appInfo ->
                    Text(text = appInfo.loadLabel(packageManager).toString())
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }



    }
}