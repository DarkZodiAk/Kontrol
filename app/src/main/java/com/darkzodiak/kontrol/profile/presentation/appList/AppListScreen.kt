package com.darkzodiak.kontrol.profile.presentation.appList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.darkzodiak.kontrol.core.presentation.KontrolTextField

@Composable
fun AppListScreenRoot(
    viewModel: AppListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.render()
    }

    AppListScreen(
        state = viewModel.state,
        onAction = { action ->
            viewModel.onAction(action)
            when(action) {
                AppListAction.Dismiss -> onBack()
                AppListAction.Save -> onBack()
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    state: AppListState,
    onAction: (AppListAction) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onAction(AppListAction.Dismiss) }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {  },
                actions = {
                    IconButton(onClick = { onAction(AppListAction.Save) }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            KontrolTextField(
                text = searchQuery,
                placeholder = "Поиск приложения",
                onTextChange = { searchQuery = it },
                modifier = Modifier.padding(12.dp)
            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(state.apps.filter { it.title.contains(searchQuery, ignoreCase = true) }) { app ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(
                                    if (state.selectedApps.any { it.id == app.id }) AppListAction.UnselectApp(app)
                                    else AppListAction.SelectApp(app)
                                )
                            }
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = app.icon,
                            contentDescription = null
                        )
                        Text(
                            text = app.title,
                        )
                        RadioButton(
                            selected = state.selectedApps.any { it.id == app.id },
                            onClick = {
                                onAction(
                                    if(state.selectedApps.any { it.id == app.id }) AppListAction.UnselectApp(app)
                                    else AppListAction.SelectApp(app)
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}