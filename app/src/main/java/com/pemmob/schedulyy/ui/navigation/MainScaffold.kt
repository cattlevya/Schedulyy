package com.pemmob.schedulyy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pemmob.schedulyy.R
import com.pemmob.schedulyy.ui.catalog.ComponentCatalogScreen
import com.pemmob.schedulyy.ui.screens.HomeScreen
import com.pemmob.schedulyy.ui.screens.PlaceholderScreen
import com.pemmob.schedulyy.ui.tasks.TasksScreen
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemmob.schedulyy.ui.onboarding.OnboardingScreen
import com.pemmob.schedulyy.ui.settings.SettingsViewModel

@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.uiState.collectAsState()

    if (!settingsState.userSettings.hasCompletedOnboarding) {
        OnboardingScreen(
            onFinishOnboarding = { name, nim, major, university, semester, colorKey ->
                settingsViewModel.updateProfile(name, nim, major, university, semester, colorKey)
                settingsViewModel.setOnboardingCompleted(true)
            }
        )
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Today.route
    val isCatalog = currentRoute == Screen.Catalog.route

    val inkColor = SchedulyyTheme.extendedColors.ink
    val secondaryTextColor = SchedulyyTheme.extendedColors.textSecondary

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(visible = !isCatalog) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Screen.bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.icon,
                                    contentDescription = stringResource(screen.contentDescriptionRes),
                                    tint = if (isSelected) inkColor else secondaryTextColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = stringResource(screen.titleRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) inkColor else secondaryTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                HomeScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) }
                )
            }
            composable(Screen.Schedule.route) {
                com.pemmob.schedulyy.ui.schedule.ScheduleScreen(
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) }
                )
            }
            composable(Screen.Scan.route) {
                com.pemmob.schedulyy.ui.scan.ScanScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen()
            }
            composable(Screen.Spaces.route) {
                com.pemmob.schedulyy.ui.spaces.SpacesScreen(
                    onNavigateToDetail = { spaceId ->
                        navController.navigate("space_detail/$spaceId")
                    }
                )
            }
            composable(
                route = "space_detail/{spaceId}",
                arguments = listOf(androidx.navigation.navArgument("spaceId") { type = androidx.navigation.NavType.LongType })
            ) {
                com.pemmob.schedulyy.ui.spaces.SpaceDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChildSpace = { childId ->
                        navController.navigate("space_detail/$childId")
                    }
                )
            }
            composable(Screen.Settings.route) {
                com.pemmob.schedulyy.ui.settings.SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Catalog.route) {
                ComponentCatalogScreen()
            }
        }
    }
}
