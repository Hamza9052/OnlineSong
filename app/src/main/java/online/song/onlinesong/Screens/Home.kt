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
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import online.song.onlinesong.Events.dataSong
import java.util.UUID


@SuppressLint("UnrememberedMutableState")
@Composable
fun Home(
    navController: NavController,
    VM: songVM,
) {


    val names_types = VM.Categories.observeAsState(emptyList<String>())
    var isLoading = VM.isLoading.observeAsState(Boolean)
    val pop = VM.ListSingerCata.observeAsState(emptyMap<String, List<String>>())
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
    var data = dataSong()
    data.list = songs
    val lazyGridState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()


    val scrollOffset = derivedStateOf {
        lazyGridState.firstVisibleItemScrollOffset
    }
    val rowHeight by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 60.dp else 150.dp, animationSpec = tween(1000)
    )
    val text by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 0.dp else 8.dp, animationSpec = tween(1000)
    )
    val VisibiltyOfText by animateFloatAsState(
        targetValue = if (scrollOffset.value > 0) 0f else 1f,
        animationSpec = tween(1000),
    )
    val Height by animateDpAsState(
        targetValue = if (scrollOffset.value > 0) 0.dp else 30.dp, animationSpec = tween(1000)
    )
    val test by animateFloatAsState(
        targetValue = if (scrollOffset.value > 0) 0f else 1f,
        animationSpec = tween(1000),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(Height))

        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = Height, bottom = text)
                .alpha(VisibiltyOfText)
                .align(alignment = Alignment.Start)
        )

        if (isLoading.value == true && names_types.value.isEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(10) { index ->
                    Process()
                }

            }

        }
        else if (names_types.value.isNotEmpty()) {

            LazyRow(
                state = lazyRowState,
                modifier = Modifier
                    .fillMaxWidth() // Make the LazyRow fill the available width
                    .height(rowHeight),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(
                    count = names_types.value.size, // Limit to a very large number
                ) { index ->

                    val nameType =  names_types.value[index]
                    val uriImag = VM.getImage(nameType, navController.context)
                    val image = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(navController.context).data(uriImag)
                            .crossfade(true).error(R.drawable.error).placeholder(R.drawable.error)
                            .build()
                    )
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

                            Item(image, nameType, test)
                        }
                    }

                }
            }
            var l = 0
            LazyColumn(state = lazyGridState) {
                items(names_types.value.size) { index ->
                    val names = names_types.value[index]


                    LaunchedEffect(Unit) {
                        VM.pop(names_types.value[l])
                    }
                    if (l == index){
                        if (l+1 == names_types.value.size){

                        }else{
                            l++
                        }
                        Log.e("test", "Home: Out of bounds${l}", )
                    }else{

                    }

                    Text(
                        text = names,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(top = Height, bottom = text)
                            .alpha(VisibiltyOfText)
                            .align(Alignment.Start)
                    )


                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        if (pop.value[names].isNullOrEmpty()){
                            Log.e("expe", "Home: ${pop.value[names]} +${names}",)
                        }
                        else{
                            Log.e("expe", "Home: ${pop.value[names]} +${names}",)
                            items(pop.value[names]!!.size) { index ->

                                val nameType = pop.value[names]?.get(index)
                                val uriImag = VM.getImage(nameType.toString(), navController.context)
                                val image = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(navController.context)
                                        .data(uriImag)
                                        .crossfade(true)
                                        .error(R.drawable.error)
                                        .placeholder(R.drawable.error)
                                        .build()
                                )
                                val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(500))

                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally() + fadeIn(),
                                    exit = slideOutHorizontally() + fadeOut()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(150.dp)
                                            .graphicsLayer(scaleX = scale, scaleY = scale)
                                    ) {
                                        Item2(image, nameType.toString())
                                    }
                                }
                            }
                        }

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
                Color.Transparent, RoundedCornerShape(12.dp)
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
                Color.Transparent, RoundedCornerShape(12.dp)
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
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Color.Transparent
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .alpha(Height)
                    .size(80.dp)
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = type,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.alpha(Height)
            )

        }

    }

    if (Height == 0f) {
        Button(
            onClick = {},
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .padding(start = 4.dp)
                .width(150.dp)
        ) {
            Text(
                text = type, color = Color.White, fontSize = 14.sp, maxLines = 1
            )
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
                Color.Transparent, RoundedCornerShape(12.dp)
            )
            .padding(0.dp)
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
                        Color.Transparent
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .size(120.dp)
            )

            Spacer(Modifier.height(5.dp))
            Text(
                text = type, color = Color.White, fontSize = 19.sp, minLines = 1
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