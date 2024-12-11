package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale


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
        "??????",
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
        "Apple",
        "Banana",
        "Cherry",
        "Date",
        "Elderberry",
        "Fig",
        "Grape",
        "Honeydew",
        "Kiwi",
        "Lemon"
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
        targetValue = if (scrollOffset.value > 0) 0.dp else 8.dp,
        animationSpec = tween(1000)
    )
    val VisibiltyOfText by animateFloatAsState(
        targetValue = if (scrollOffset.value > 0) 0f else 1f,
        animationSpec = tween(1000),
    )
    val Height by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 0.dp else 30.dp,
        animationSpec = tween(1000)
    )
    val test by animateFloatAsState(
        targetValue = if (scrollOffset.value > 0) 0f else 1f,
        animationSpec = tween(1000),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ) {
       Spacer(modifier = Modifier.height(Height))

        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = Height, start = Height, bottom =Height )
                .alpha(VisibiltyOfText)
        )

      if (isLoading.value == true){
          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(15.dp)
          ) {
              items(10){index->
                  Process()
              }

          }

      }else{
          LazyRow(
              modifier = Modifier
                  .fillMaxWidth() // Make the LazyRow fill the available width
                  .height(rowHeight),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {

              items(
                  count = names_types.value.size,
                  key = {index-> names_types.value[index] }
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
                              .padding(start = text)  // Padding around each item
                              .graphicsLayer(scaleX = scale, scaleY = scale)
                      ) {

                          Item(image, nameType,test)
                      }
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
                        Item2(image, nameType)
                    }
                }

            }

        }

    }


}

@Composable
fun Process(
    ) {

    Box(
        modifier = Modifier
            .background(
                Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .padding(bottom = 14.dp)
    ) {
           CircularProgressIndicator(
               color = colorResource(R.color.icon),
           )
    }
}

@Composable
fun Item(
    image: AsyncImagePainter,
    type: String,
    Height: Float,

    ) {

    Box(
        modifier = Modifier
            .background(
                Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .alpha(Height)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(Height)
        ) {

            Image(
                painter = image,
                contentDescription = "Image Item",
                alignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Color.Transparent)
                    .clip(RoundedCornerShape(20.dp))
                    .alpha(Height)
                    .size(80.dp)
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = type,
                color = Color.White,
                fontSize = 19.sp,
                maxLines = 1,
                modifier = Modifier.alpha(Height)
            )

        }

    }

            if (Height == 0f) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier.padding(start = 4.dp)
                        .width(150.dp)
                ) {
                    Text(
                        text = type,
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1)
                }
            }


}

@Composable
fun Item2(
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