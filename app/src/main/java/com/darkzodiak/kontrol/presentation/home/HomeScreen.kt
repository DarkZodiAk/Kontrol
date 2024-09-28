package com.darkzodiak.kontrol.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
            viewModel.onAction(action)
        }
    )
}

@Composable
fun HomeScreen(
    profiles: List<Profile>,
    onAction: (HomeAction) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(HomeAction.NewProfile) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create new profile")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(profiles) { profile ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                        .clickable { onAction(HomeAction.OpenProfile(profile.id!!)) }
                ) {
                    Text(text = profile.name)
                    Switch(
                        checked = profile.isEnabled,
                        onCheckedChange = {
                            onAction(HomeAction.SwitchProfileState(profile.copy(isEnabled = it)))
                        }
                    )
                }
            }
        }
    }
}