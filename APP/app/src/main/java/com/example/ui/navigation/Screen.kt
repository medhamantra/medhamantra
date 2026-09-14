package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Courses : Screen("courses", "Courses", Icons.AutoMirrored.Filled.MenuBook)
    object Notices : Screen("notices", "Notices", Icons.Default.Notifications)
    object Results : Screen("results", "Results", Icons.Default.EmojiEvents)
    object Faculty : Screen("faculty", "Faculty", Icons.Default.Group)
    object Gallery : Screen("gallery", "Gallery", Icons.Default.Collections)
    object About : Screen("about", "About", Icons.Default.Info)
    object Contact : Screen("contact", "Contact", Icons.Default.ContactPhone)
    object More : Screen("more", "More", Icons.Default.MoreHoriz)
    object Search : Screen("search", "Search", Icons.Default.Home)

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, Courses, Notices, Results, More)
    }
}
