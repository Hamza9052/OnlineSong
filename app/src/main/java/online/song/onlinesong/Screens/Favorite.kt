package online.song.onlinesong.Screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM


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
    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ){
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing.value),
            onRefresh = {
                isRefreshing.value == true
                scope.launch{
                    viewModel.Action(SongEvent.checkFavoriteSong(userData!!),navController.context)
                }
            },


            ) {

            if (isFavoriteLoading.value == true) {
                // Show a loading indicator
                Process()
            }else{
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1)
                ) {
                    items(
                        songs.value.size,
                        key = {index->
                            songs.value.keys.toList()[1]

                        }
                    ){index->
                        val name = songs.value.keys.toList()[index]
                        val nameSong = songs.value[name]?.get(index)
                        val image = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(navController.context)
                                .data(viewModel.getImage(name,navController.context))
                                .crossfade(true)
                                .error(R.drawable.error)
                                .placeholder(R.drawable.logo)
                                .build()
                        )
                        Item(image, nameSong.toString(),navController,name,"")
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
//    onClick: () -> Unit,
    navController: NavController,
    nameSinger:String,
    cat: String
){
    Box(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("ScreenPlay/$name/$nameSinger/$cat")
//                onClick()
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

        }
    }

}