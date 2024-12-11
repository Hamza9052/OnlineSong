package online.song.onlinesong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.launch
import online.song.onlinesong.BottomBar.SwipeScreen
import online.song.onlinesong.BottomBar.bottomBar
import online.song.onlinesong.Screens.Favorite
import online.song.onlinesong.Screens.Home
import online.song.onlinesong.Screens.Screen
import online.song.onlinesong.Screens.Search
import online.song.onlinesong.ViewModel.songVM
import online.song.onlinesong.itemList.bottomIcons
import online.song.onlinesong.ui.theme.OnlineSongTheme

class MainActivity : ComponentActivity() {
   private val VM:songVM by viewModels<songVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineSongTheme {

                val pageState = rememberPagerState(
                    pageCount ={ bottomIcons.size }
                )
                val navController = rememberNavController()



                navigate(navController,VM)
                Scaffold (
                    bottomBar = {
                        bottomBar(navController = navController, pagerState = pageState)
                    }
                ) {paddingValues ->
                    SwipeScreen(pageState,paddingValues,navController,VM)
                }
            }
        }
    }
}

@Composable
fun navigate(
    navController:NavHostController,
    VM: songVM
){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route){
            Home(
                navController,
                VM
            )
        }
        composable(Screen.Search.route){
            Search(
                navController,
                VM
            )
        }
        composable(Screen.Favorite.route){
            Favorite(navController)
        }
    }
}

