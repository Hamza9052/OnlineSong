package online.song.onlinesong.itemList

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomIcons(
val title: String,
val selectedIcon: ImageVector,
val unselectedIcon: ImageVector,
val hasNews: Boolean
)
val bottomIcons = listOf(
    BottomIcons(
        title ="home",
        Icons.Default.Home,
        Icons.Default.Home,
        false,
    ),
    BottomIcons(
        title ="search",
        Icons.Default.Search,
        Icons.Default.Search,
        false,
    ),
    BottomIcons(
        title ="favorite",
        Icons.Default.Favorite,
        Icons.Default.Favorite,
        false,
    )
)