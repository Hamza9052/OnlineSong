package online.song.onlinesong.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import kotlinx.coroutines.launch
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favorite(
    navController: NavController,
    userData: UserData?,
    viewModel: songVM
){
    val songs = viewModel.listOfFavorite.observeAsState(emptyMap<String,String>())
    val scope = rememberCoroutineScope()
    val isRefreshing = remember {mutableStateOf(false) }
    val isFavoriteLoading = viewModel.isFavoriteLoading.observeAsState(Boolean)


    val list = remember { mutableListOf<String>() }

    LaunchedEffect(userData) {
        viewModel.Action(SongEvent.checkFavoriteSong(userData!!),navController.context)
    }
    Scaffold(
         topBar = {
            CenterAlignedTopAppBar(

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.background),
                    titleContentColor = colorResource(R.color.White),
                ),
                title = {
                    Text(
                        "Favorite",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(R.color.White),
                        fontWeight = FontWeight.ExtraBold
                    )
                },


            )
        }
    ){ padding->

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = colorResource(R.color.background))
                .padding(padding), // Ensure padding is applied here
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            SwipeRefresh(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                state = rememberSwipeRefreshState(isRefreshing.value),
                onRefresh = {
                    isRefreshing.value == true
                    scope.launch{
                        viewModel.Action(SongEvent.checkFavoriteSong(userData!!),navController.context)
                    }
                }) {

                if (isFavoriteLoading.value == true) {
                    // Show a loading indicator
                    Process()
                }else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Log.e("test list ", "Favorite: ${songs.value.size}" )
                        items(
                            songs.value.size,
                            key = {index->
                                songs.value.keys.toList()[index]

                            }
                        ){index->
                            val name = songs.value.keys.toList()[index]
                            val nameSong = songs.value[name]

                                if (nameSong.toString() !in list) {
                                    list.add(nameSong.toString())
                                }

                            val image = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(navController.context)
                                    .data(viewModel.getImage(name,navController.context))
                                    .crossfade(true)
                                    .error(R.drawable.error)
                                    .placeholder(R.drawable.logo)
                                    .build()
                            )
                            Item(image, nameSong.toString(),navController,name,list)
                        }
                    }
                }
            }
        }
    }

}



@Composable
fun Item(
    image: AsyncImagePainter,
    name: String,
    navController: NavController,
    nameSinger:String,
    cat: List<String>
){
    cat.forEach{name ->
        Log.e("list songs","$name")
    }

    val jsonLis = Gson().toJson(cat)
    Box(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("ScreenPlay/$name/$nameSinger/$jsonLis")
//                onClick()
            })
            .background(color = colorResource(R.color.background))
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = image,
                contentDescription = "Image",
                modifier = Modifier
                    .size(80.dp)
                    .background(color = Color.Transparent)
                    .clip(RoundedCornerShape(15.dp))
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Column {
                Text(
                    text = name,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.unfocus),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(0.1f))
                Text(
                    text = nameSinger,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.unfocus),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

        }
    }

}