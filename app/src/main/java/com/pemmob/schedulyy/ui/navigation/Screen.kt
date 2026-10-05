package com.pemmob.schedulyy.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.pemmob.schedulyy.R

sealed class Screen(
    val route: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Today : Screen(
        route = "today",
        titleRes = R.string.nav_home,
        contentDescriptionRes = R.string.cd_nav_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Schedule : Screen(
        route = "schedule",
        titleRes = R.string.nav_schedule,
        contentDescriptionRes = R.string.cd_nav_schedule,
        icon = Icons.Outlined.CalendarToday,
        selectedIcon = Icons.Outlined.CalendarToday
    )

    data object Tasks : Screen(
        route = "tasks",
        titleRes = R.string.nav_tasks,
        contentDescriptionRes = R.string.cd_nav_tasks,
        icon = Icons.Outlined.CheckCircle,
        selectedIcon = Icons.Outlined.CheckCircle
    )

    data object Spaces : Screen(
        route = "spaces",
        titleRes = R.string.nav_folder,
        contentDescriptionRes = R.string.cd_nav_folder,
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Outlined.Folder
    )

    data object Catalog : Screen(
        route = "catalog",
        titleRes = R.string.title_catalog,
        contentDescriptionRes = R.string.cd_catalog_button,
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Outlined.Folder
    )

    data object Settings : Screen(
        route = "settings",
        titleRes = R.string.settings_title,
        contentDescriptionRes = R.string.settings_title,
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Outlined.Folder
    )

    data object Scan : Screen(
        route = "scan",
        titleRes = R.string.title_catalog,
        contentDescriptionRes = R.string.title_catalog,
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Outlined.Folder
    )

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Today, Schedule, Tasks, Spaces)
    }
}
