package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import androidx.compose.runtime.getValue

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun listOfSongs(
    navController: NavController,
    viewModel: songVM,
    cat:String,
    onClick: ()->Unit
){







    LaunchedEffect(Unit){
        viewModel.fetchPlaylistTracks(cat)
    }
    Log.e("Hamza tRack id", "${cat}")
    val album by viewModel.albumDetails.collectAsState()
    val track by viewModel.TrackDe.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val songs = viewModel.ListSongs.observeAsState(emptyMap<String,List<String>>())
    val totalTime = viewModel.totalTime.observeAsState(emptyMap<String,String>())
    val isLoading = viewModel.SongsisLoading.observeAsState(Boolean)
    val isRefreshing = remember { mutableStateOf(false) }
    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(album[cat]?.images?.firstOrNull()?.url)
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.logo)
            .build()
    )


    if (isLoading.value == true){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(color = colorResource(R.color.background))
        ) {
        LoadingProgress()
        }

    }
    else{

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .background(color = colorResource(R.color.background))

            ) {


                Box(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth()
                        .size(250.dp)
                ) {


                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = image,
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop
                    )
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(start = 3.dp, top = 8.dp)
                    ){
                        IconButton(onClick = {
                            navController.navigate(Screen.Home.route)
                            viewModel.clearTracks()
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "ArrowBack",
                                modifier = Modifier.size(30.dp),
                                tint = colorResource(R.color.background)
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.inverseOnSurface
                                    )
                                )
                            )
                            .padding(top = 25.dp, bottom = 6.dp)
                            .padding(horizontal = 16.dp),
                        text = album[cat]?.name ?:"Loading..",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                    )
                }
                HorizontalDivider(thickness = (0.7).dp)
                Spacer(modifier = Modifier.height(20.dp))

                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing.value),
                    onRefresh = {
                        isRefreshing.value == true
                        scope.launch{
                            viewModel.fetchPlaylistTracks(cat)
                        }
                    },


                ) {

                    if (isLoading.value == true) {
                        // Show a loading indicator
                      Process()
                    }else{
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1)
                        ) {
                            Log.e("Hamza tRack","${track}")
                            if (track != null ){
                                items(
                                    track.size
                                ){index->

                                    var name = track[index]?.name ?: "Loading..."
                                    var pic = track[index]?.album?.images?.firstOrNull()?.url
                                    val picture = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(navController.context)
                                            .data(pic)
                                            .crossfade(true)
                                            .error(R.drawable.error)
                                            .placeholder(R.drawable.logo)
                                            .build()
                                    )
//                                    scope.launch {
//                                        viewModel.T_Time(navController,name)
//                                    }
//                                    var time = if (index < totalTime.value.size && totalTime.value[name] != null) totalTime.value[name] else "00:00"
                                    val trackId = track[index].id.toString()
                                    ItemSong(picture,name,navController,cat,trackId)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }



                        }
                    }
                    if (track.size == null){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text ="There's no Songs",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = colorResource(R.color.icon),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

            }


    }





}


@Composable
fun ItemSong(
    image: AsyncImagePainter,
    name: String,
    navController: NavController,
    playlistId: String,
    trackId: String,

){
//    val jsonList = Gson().toJson(list)
    Box(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("ScreenPlay/$playlistId/$trackId")

            })
            .background(color = colorResource(R.color.background))
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .height(80.dp),
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
                    .size(50.dp)
                    .background(color = Color.Transparent)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = name,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.unfocus),
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "00:00",
                textAlign = TextAlign.Center,
                color = colorResource(R.color.unfocus),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }

}

@Composable
fun LoadingProgress() {

    Box(
        modifier = Modifier
            .background(
                Color.Transparent, RoundedCornerShape(12.dp)
            )
            .padding(bottom = 14.dp),

    ) {
        CircularProgressIndicator(
            color = colorResource(R.color.icon),
        )
    }
}
