package online.song.onlinesong.BottomBar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import online.song.onlinesong.LoginWithGoogle.SignInState
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.Screens.Favorite
import online.song.onlinesong.Screens.Home
import online.song.onlinesong.Screens.Search
import online.song.onlinesong.Screens.Screen.Search
import online.song.onlinesong.Screens.testScreen
import online.song.onlinesong.ViewModel.songVM
import online.song.onlinesong.itemList.bottomIcons

@Composable
fun SwipeScreen(
    pagerState: PagerState,
    paddingValues: PaddingValues,
    navController: NavHostController,
    VM: songVM,
    state: SignInState,
    onSignInClick:() -> Unit,
    onSignOutClick:() -> Unit,
    userdata: UserData?
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        userScrollEnabled = false
    ) {page ->
        when (page) {
//            0 -> Home(navController = navController,VM,state,onSignInClick,onSignOutClick,userdata)
            0 -> testScreen(VM,navController)
            1 -> Search(navController = navController,VM)
            2 -> Favorite(navController = navController)
        }

    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val newRoute = bottomIcons[page].title.lowercase()
            if (currentRoute != newRoute) {
                navController.navigate(newRoute) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}