package online.song.onlinesong

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import online.song.onlinesong.BottomBar.SwipeScreen
import online.song.onlinesong.BottomBar.bottomBar
import online.song.onlinesong.LoginWithGoogle.GoogleAuthUiClient
import online.song.onlinesong.Screens.Favorite
import online.song.onlinesong.Screens.Home
import online.song.onlinesong.Screens.Screen
import online.song.onlinesong.Screens.Search
import online.song.onlinesong.ViewModel.songVM
import online.song.onlinesong.itemList.bottomIcons
import online.song.onlinesong.ui.theme.OnlineSongTheme

class MainActivity : ComponentActivity() {
   private val VM by viewModels<songVM>()
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineSongTheme {

                val pageState = rememberPagerState(
                    pageCount ={ bottomIcons.size }
                )
                val navController = rememberNavController()

                val state by VM.state.collectAsStateWithLifecycle()
               val launch = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = {resul->
                        if (resul.resultCode == RESULT_OK){
                            lifecycleScope.launch{
                                val signResult = googleAuthUiClient.SignInWithIntent(
                                    intent = resul.data ?: return@launch
                                )
                                VM.onSignInResult(signResult)
                            }
                        }

                    }
               )

                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if (state.isSignInSuccessful){

                        Toast.makeText(navController.context,
                            "Sign is Successful",
                            Toast.LENGTH_LONG).show()

                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {

                    composable(Screen.Home.route){
                        Home(
                            navController = navController,
                            VM = VM,
                            state = state,
                            onSignInClick = {lifecycleScope.launch{
                                val signInIntentSender = googleAuthUiClient.signIn()

                                launch.launch(
                                    IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                                )

                            }},
                            onSignOutClick =  {
                                lifecycleScope.launch{
                                    googleAuthUiClient.signout()
                                    Toast.makeText(navController.context,
                                        "SignOut",
                                        Toast.LENGTH_LONG).show()
                                }
                            },
                            userdata = googleAuthUiClient.getSignedInUser(),
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

                Scaffold (
                    bottomBar = {
                        bottomBar(navController = navController, pagerState = pageState)
                    }
                ) {paddingValues ->
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    HorizontalPager(
                        state = pageState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        userScrollEnabled = false
                    ) {page ->
                        when(page){
                            0 -> Home(
                                navController = navController,
                                VM = VM,
                                state = state,
                                onSignInClick = {lifecycleScope.launch{
                                    val signInIntentSender = googleAuthUiClient.signIn()

                                    launch.launch(
                                        IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                                    )

                                }},
                                onSignOutClick =  {
                                    lifecycleScope.launch{
                                        googleAuthUiClient.signout()
                                        Toast.makeText(navController.context,
                                            "SignOut",
                                            Toast.LENGTH_LONG).show()
                                    }
                                },
                                userdata = googleAuthUiClient.getSignedInUser(),
                            )
                            1 ->   Search(
                                navController,
                                VM
                            )
                            2-> Favorite(navController)
                        }

                    }
                    LaunchedEffect(pageState) {
                        snapshotFlow { pageState.currentPage }.collect { page ->
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
            }
        }
    }
}




