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
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM


@SuppressLint("SuspiciousIndentation")
@Composable
fun listOfSongs(
    navController: NavController,
    names:String?,
    viewModel: songVM,
    cat:String,
    onClick: ()->Unit
){
    LaunchedEffect(Unit) {
        viewModel.listSongs(cat, names.toString())
    }

    val songs = viewModel.ListSongs.observeAsState(emptyList<String>())
    val isLoading = viewModel.SongsisLoading.observeAsState(Boolean)
    val isRefreshing = remember { mutableStateOf(false) }
    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(viewModel.getImage(names!!,navController.context))
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.error)
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
                        text = names,
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
                        viewModel.listSongs(cat, names.toString())
                    },


                ) {
                    if (isLoading.value == true) {
                        // Show a loading indicator
                      Process()
                    }else{
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1)
                        ) {

                            items(
                                songs.value.size,
                                key = {index->
                                    songs.value[index]
                                }
                            ){index->
                                val name = songs.value[index]
                                Log.d("songslist",name)
                                ItemSong(image,name,onClick,navController,names)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
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
    onClick: () -> Unit,
    navController: NavController,
    nameSinger:String
){
    Box(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("ScreenPlay/$name/$nameSinger")
                onClick
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