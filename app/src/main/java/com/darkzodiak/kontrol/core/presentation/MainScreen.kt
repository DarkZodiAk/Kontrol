package com.darkzodiak.kontrol.core.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    showScreenOnItem: @Composable ((NavItem) -> Unit)
) {
    var selectedScreenIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState(pageCount = { NavItem.entries.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        selectedScreenIndex = pagerState.currentPage
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavItem.entries.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedScreenIndex == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            selectedScreenIndex = index
                        },
                        icon = { Icon(navItem.icon, null) },
                        label = { Text(navItem.title) }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { index ->
            showScreenOnItem(NavItem.entries[index])
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen {}
}

enum class NavItem(
    val title: String,
    val icon: ImageVector
) {
    PROFILES("Профили", Icons.Default.Home),
    STATISTICS("Статистика", Icons.Outlined.InsertChart)
}