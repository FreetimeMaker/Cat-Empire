package com.freetime.catempire.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Toys
import androidx.compose.ui.graphics.vector.ImageVector

enum class GameTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    CATS("Cats", Icons.Default.Pets),
    UPGRADES("Upgrades", Icons.Default.Toys),
    CATDEX("Catdex", Icons.AutoMirrored.Filled.MenuBook),
    MORE("More", Icons.Default.MoreHoriz),
}
