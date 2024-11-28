package com.darkzodiak.kontrol.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkzodiak.kontrol.domain.App
import com.darkzodiak.kontrol.presentation.components.KontrolTextField

@Composable
fun AppList(
    apps: List<App>,
    selectedApps: List<App>,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        KontrolTextField(
            text = searchQuery,
            placeholder = "Поиск приложения",
            onTextChange = { searchQuery = it },
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(apps.filter { it.title.contains(searchQuery, ignoreCase = true) }) { app ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(
                                if(selectedApps.contains(app)) ProfileAction.UnselectApp(app)
                                else ProfileAction.SelectApp(app)
                        )}
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = null
                    )
                    Text(
                        text = app.title,
                    )
                    RadioButton(
                        selected = selectedApps.contains(app),
                        onClick = {
                            onAction(
                                if(selectedApps.contains(app)) ProfileAction.UnselectApp(app)
                                else ProfileAction.SelectApp(app)
                            )
                        },
                    )
                }
            }
        }
    }
}