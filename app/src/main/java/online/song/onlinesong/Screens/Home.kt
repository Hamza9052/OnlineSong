package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.common.math.Quantiles.scale
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.max


@SuppressLint("UnrememberedMutableState")
@Composable
fun Home(
    navController: NavController,
    VM: songVM,
) {
    val names_types = VM.Categories.observeAsState(emptyList<String>())
    var isLoading = VM.isLoading.observeAsState(Boolean)
    LaunchedEffect(Unit) {
        VM.getname()
    }
    var songs = listOf<String>(
        "Hamza",
        "Ilias",
        "khalid",
        "khadija",
        "farah",
        "test",
        "case",
        "pc",
        "pause",
        "flow",
        "Barca",
        "for",
        "me",
        "this",
    )
    val lazyGridState = rememberLazyGridState()
    val scrollOffset = derivedStateOf {
        lazyGridState.firstVisibleItemScrollOffset
    }
    val rowHeight by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 60.dp else 150.dp,
        animationSpec = tween(1000)
    )
    val text by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 0.dp else 6.dp,
        animationSpec = tween(1000)
    )
    val VisibiltyOfText by animateFloatAsState(
        targetValue = if (scrollOffset.value > 0) 0.4f else 1f,
        animationSpec = tween(1000),
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ) {
        Spacer(modifier = Modifier.height(77.dp))

        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier
                .padding(text)
                .alpha(VisibiltyOfText)
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth() // Make the LazyRow fill the available width
                .height(rowHeight),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = names_types.value.size,
                key = {index->
                    names_types.value[index]
                }
            ) { index ->

                val nameType =  names_types.value[index]
                val uriImag = VM.getImage(nameType, navController.context)
                val image = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(navController.context)
                        .data(uriImag)
                        .crossfade(true)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.error)
                        .build())
                val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(500))
                Log.e("Row", "Home:$uriImag ")
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {

                    Box(
                        modifier = Modifier
                            .width(100.dp)  // Set fixed width for each item
                            .padding(start = 8.dp)  // Padding around each item
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    ) {
                        Item(image, nameType)
                    }
                }

            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.icon)
        )
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(4),
        ){

//            items(names_types.value.size) { index ->
//                val nameType = names_types.value[index]
            items(
                count = songs.size,
                key = {index->
                    songs[index]
                }
            ) { index ->
                val nameType = songs[index]
                val uriImag = VM.getImage(nameType, navController.context)
                val image = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(navController.context)
                        .data(uriImag)
                        .crossfade(true)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.error)
                        .build()
                )
                Log.e("Row", "Home:$uriImag ")
                val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(500))

                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()  // Set fixed width for each item
                            .padding(8.dp)  // Padding around each item
                            .scale(scale = scale)
                    ) {
                        Item(image, nameType)
                    }
                }

            }

        }

    }


}


@Composable
fun Item(
    image: AsyncImagePainter,
    type: String,
) {

    Box(
        modifier = Modifier
            .background(
                Color.Transparent,
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = image,
                contentDescription = "Image Item",
                alignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Color.Transparent)
                    .clip(RoundedCornerShape(20.dp))
                    .size(80.dp)
            )

            Spacer(Modifier.height(13.dp))
            Text(
                text = type,
                color = Color.White,
                fontSize = 19.sp,
            )
        }
    }


}

//var mediaPlayer = MediaPlayer()
//Log.e("mp3", "Home: ${VM.generateSignedUrl("112")}", )
//Button(onClick = {
//
//    mediaPlayer.apply {
//        setDataSource(VM.generateSignedUrl("112").toString())
//        prepare()
//        start()
//    }
//}){
//    Text("Play")
//}