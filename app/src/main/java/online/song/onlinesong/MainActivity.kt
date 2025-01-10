package online.song.onlinesong

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import online.song.onlinesong.BottomBar.bottomBar
import online.song.onlinesong.LoginWithGoogle.GoogleAuthUiClient
import online.song.onlinesong.Screens.Favorite
import online.song.onlinesong.Screens.Home
import online.song.onlinesong.Screens.Screen
import online.song.onlinesong.Screens.Search
import online.song.onlinesong.Screens.listOfSongs
import online.song.onlinesong.Screens.playSong
import online.song.onlinesong.Service.MusicService
import online.song.onlinesong.ViewModel.songVM
import online.song.onlinesong.itemList.bottomIcons
import online.song.onlinesong.ui.theme.OnlineSongTheme
import java.lang.reflect.Type

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(Intent(this, MusicService::class.java))
        }else{
            startService(Intent(this, MusicService::class.java))
        }

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

                Scaffold (
                    bottomBar = {
                        val currentBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry?.destination?.route ?: ""
                        if (currentRoute.startsWith("ScreenPlay")) {
                            Log.e("Screen", "I'm on the correct screen")
                        } else {
                            Log.e("Screen", "I'm on a different screen")
                            bottomBar(navController = navController, pagerState = pageState)
                        }

                    }
                ) {paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
//                            composable(
//                                Screen.Test.route,
//                                exitTransition = {
//                                    slideOutHorizontally(
//                                        targetOffsetX = { -1000 },
//                                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
//                                    )
//                                },
//                                popEnterTransition = {
//                                    slideInHorizontally(
//                                        initialOffsetX = { -1000 },
//                                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
//                                    )
//                                }
//                            ){
//                                testScreen(VM,navController)
//                            }
                            composable(
                                Screen.Home.route,
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { -1000 },
                                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                    )
                                },
                                popEnterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { -1000 },
                                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                                    )
                                }
                            ){
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
                            composable(
                                Screen.Search.route,
                                enterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                                    )
                                },
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                    )
                                }
                            ){
                                Search(
                                    navController,
                                    VM
                                )
                            }
                            composable(
                                Screen.Favorite.route,
                                enterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                                    )
                                },
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                    )
                                }
                            ){
                                Favorite(
                                    navController,
                                    googleAuthUiClient.getSignedInUser(),
                                     VM
                                )
                            }
                            composable(
                                "list/{singerName}/{cat}",
                                enterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                                    )
                                },
                                exitTransition = {
                                    slideOutHorizontally(
                                        targetOffsetX = { 1000 },
                                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                    )
                                }
                            ){backStackEntry->
                                val singerName =backStackEntry.arguments?.getString("singerName")?:"Loading..."
                                val cat =backStackEntry.arguments?.getString("cat")?:"Loading..."
                                listOfSongs(
                                    navController, singerName, VM, cat,
                                    onClick = {}
                                )

                            }
                            composable(
                                "ScreenPlay/{name}/{singerName}/{list}",
                                enterTransition = {
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                },
                                exitTransition = {
                                    slideOutVertically(
                                        targetOffsetY = { 2 },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                            ){backStackEntry->
                                val singerName =backStackEntry.arguments?.getString("singerName")?:"Loading..."
                                val name =backStackEntry.arguments?.getString("name")?:"Loading..."
                                val serializedList = backStackEntry.arguments?.getString("list")?:"Loading..."
                                val list:List<String> = Gson().fromJson(serializedList,object : TypeToken<List<String>>() {}.type)
                                playSong(
                                    VM,
                                    navController,
                                    singerName,
                                    name,
                                    list,
                                     googleAuthUiClient.getSignedInUser()
                                )

                            }
                        }
                    }


                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}




