package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import online.song.onlinesong.Events.MusicData

import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM

@Composable
fun Search(
    navController: NavController,
    VM: songVM
){
    val active by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        VM.fetchTracksAsync()
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ){
        Box(){
            Searchbar(active,VM,navController)
        }


    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Searchbar(
    active: Boolean,
    viewModel: songVM,
    navController: NavController,
//    click:()->Unit
) {
    var search by remember { mutableStateOf(MusicData().Search) }

    var actives by remember { mutableStateOf(active) }
//    var search by remember { mutableStateOf(data.search) }

    // Animated padding and visibility
    val animatedPaddingH by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500
            , easing = FastOutSlowInEasing) // Smooth easing
    )
    val animatedPaddingV by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing) // Smooth easing
    )


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = animatedPaddingH,
                vertical = animatedPaddingV
            )
            .background(
                color = Color.Transparent,
                RoundedCornerShape(15.dp)
            )
    ) {

        SearchBar(
            query = search,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (actives) 0.dp else 14.dp , start = if (actives) 0.dp else 14.dp)
                .background(
                    color = Color.Transparent,
                    RoundedCornerShape(15.dp)
                ),
            colors = SearchBarDefaults.colors(
                containerColor = colorResource(R.color.test),
                dividerColor = colorResource(R.color.icon),
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.White,
                )
            ),
            onQueryChange = { search= it },
            onSearch = {

            },
            active = actives,
            onActiveChange = { actives = it },
            placeholder = {
                Text(
                    text = "Search",
                    color = colorResource(R.color.unfocus),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                if (actives) {
                    AnimatedVisibility(
                        visible = actives,
                        exit = fadeOut(animationSpec = tween(50)) + slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        ),
                        enter = fadeIn(animationSpec = tween(50)) + slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                actives = false

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Close",
                                modifier = Modifier.size(20.dp),
                                tint = colorResource(R.color.unfocus)
                            )
                        }
                    }
                }
                else{
                    search = ""
                }
            },
            trailingIcon = {
                if (!actives){
                    AnimatedVisibility(
                        visible = !actives,
                        exit = fadeOut(animationSpec = tween(50)) + slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        ),
                        enter = fadeIn(animationSpec = tween(50)) + slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.unfocus)
                        )
                    }
                }

            },

        ) {

                val tracks = viewModel.TrackSearch.observeAsState()

                val size = tracks.value!!.size
                Log.e("searchTrac","$size => ${search.lowercase()}")
                for (i in 0 until size) {
                    val track = tracks.value!![i]
                     val name =  track.name.lowercase()

                    if (name.contains(search.lowercase()) && search.isNotEmpty()) {
                        Log.e("searchTrac","$name => ${search.lowercase()}")
                        LazyColumn {
                            item(6){
                                val pic = track.album.images.firstOrNull()?.url
                                val picture = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(navController.context)
                                        .data(pic)
                                        .crossfade(true)
                                        .error(R.drawable.error)
                                        .placeholder(R.drawable.logo)
                                        .build()
                                )

                                itemSong(picture,track.name)
                            }
                        }

                    }
                    if ( (i-1).equals(size)){
                        break
                    }
                }

        }
}


@Composable
fun searchName(
    navController: NavController,
    name: String,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardColors(
            contentColor = colorResource(R.color.DarkSlateBlue),
            containerColor = colorResource(R.color.DarkSlateBlue),
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ), onClick = {
            navController.navigate("message/$name")
        }
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            color = colorResource(R.color.BurlyWood),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}}

@Composable
fun itemSong(
    image: AsyncImagePainter,
    name: String,
//    navController: NavController,
//    playlistId: String,
//    trackId: String,

    ){
//    val jsonList = Gson().toJson(list)
    Box(
        modifier = Modifier
            .clickable(onClick = {
//                navController.navigate("ScreenPlay/$trackId")

            })
            .background(color = colorResource(R.color.background))
            .fillMaxWidth()
            .padding(8.dp)
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
                    .size(80.dp)
                    .background(color = Color.Transparent)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = name,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.unfocus),
                fontSize = 18.sp,
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