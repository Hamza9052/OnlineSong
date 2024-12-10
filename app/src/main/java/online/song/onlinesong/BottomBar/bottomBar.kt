package online.song.onlinesong.BottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import online.song.onlinesong.itemList.bottomIcons
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import online.song.onlinesong.R

@Composable
fun bottomBar(
    navController: NavHostController,
    pagerState: PagerState
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val coroutineScope = rememberCoroutineScope()

    NavigationBar(
        containerColor = colorResource(R.color.background),
        modifier = Modifier
            .background(
               color =  Color.Transparent,
               shape =  RoundedCornerShape(topEnd = 40.dp, topStart = 40.dp),
            )
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(topEnd = 40.dp, topStart = 40.dp),
                clip = false,
                ambientColor = Color.White,
                spotColor = Color.White
            )


    ) {


            bottomIcons.forEachIndexed { index,item->
                val isSelected = currentRoute == item.title.lowercase()

                NavigationBarItem(
                    colors =  NavigationBarItemColors(
                        selectedIconColor = colorResource(R.color.icon),
                        selectedTextColor = Color.Transparent,
                        selectedIndicatorColor = Color.Transparent,
                        unselectedIconColor = colorResource(R.color.unfocus),
                        unselectedTextColor = Color.Transparent,
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    ),
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index) // Scroll pager to the selected page without animation
                        }
                        navController.navigate(item.title.lowercase()) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }



}