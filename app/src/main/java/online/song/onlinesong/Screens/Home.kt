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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.CachePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import online.song.onlinesong.Events.Playlist

import online.song.onlinesong.LoginWithGoogle.SignInState
import online.song.onlinesong.LoginWithGoogle.UserData


@SuppressLint("UnrememberedMutableState")
@Composable
fun Home(
    navController: NavController,
    VM: songVM,
    state: SignInState,
    onSignInClick:() -> Unit,
    onSignOutClick:() -> Unit,
    userdata: UserData?
) {



    var isLoading = VM.isLoading.observeAsState(false)
//    var CatisLoading = VM.CatisLoading.observeAsState(Boolean)
//    val pop = VM.ListSingerCata.observeAsState(emptyMap<String, List<String>>())


    var PlayLists = remember { mutableListOf<String>(
        "47gSXaPgD5Ln7fs8YkPJEW"
        ,"519II5E4pZryeyySl2YLgn"
        ,"4lbTi1TTzjvYmOGh1x4IIx"
        ,"4Hzg3lPUjWjMvlKmXHBRL9"
        ,"2SM6rniZl84fEyMCB5KMQB"
        ,"0eGfpDb1TTvxIXdMk5DyyD"
        ,"6ZNoF0voOucfax96hu1lRm"
        ,"4zQDKb9McbC7T7yiJT6Oqy"
        ,"1AyxSpveGEJsBaGZubIKo8"
        ,"6fqaB0I1gHgNy4TaPphgro"
        ,"3GQvSbCBLWctClpYZXSYUG"
        ,"3mgNDhdzgwoAxlAI29VdzN"
    ) }

    LaunchedEffect(Unit) {
        // Fetch each playlist one by one
                VM.fetchPlaylistAsync()


    }

     val album by VM.albumDetails.collectAsState()

    val lazyGridState = rememberLazyGridState()
//    val lazyRowState = rememberLazyListState()
//    val scope = rememberCoroutineScope()


    val scrollOffset = derivedStateOf {
        lazyGridState.firstVisibleItemScrollOffset
    }
//    val rowHeight by animateDpAsState(
//        targetValue = if (scrollOffset.value > 0) 60.dp else 150.dp, animationSpec = tween(1000)
//    )
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
            text = "Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = Height, bottom = text)
                .alpha(VisibiltyOfText)
                .align(alignment = Alignment.Start)
        )



        if (isLoading.value) {


               Spacer(modifier = Modifier.weight(1f))
                   Process()
            Spacer(modifier = Modifier.weight(1f))



        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Fixed Icon (e.g., "G")


                if (test == 0f) {
                    Box(
                        modifier = Modifier
                            .size(48.dp) // Adjust size as needed
                            .background(
                                shape = CircleShape,
                                color = Color.Transparent
                            ), // Circle shape
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = if (userdata == null)onSignInClick else onSignOutClick,
                            modifier = Modifier
                                .size(48.dp) ,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(colorResource(R.color.test))
                        ) {
                            Text(
                                text = userdata?.userName?.first()?.uppercase().toString() ?: "P",
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }else{
                    val Profil = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(navController.context)
                            .data(userdata?.ProfilePicUri)
                            .crossfade(true)
                            .error(R.drawable.profil)
                            .placeholder(R.drawable.logo)
                            .build()
                    )


                    Box(
                        modifier = Modifier
                            .background(
                                shape = CircleShape,
                                color = Color.Transparent
                            )
                            .alpha(VisibiltyOfText)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = Profil,
                                contentDescription = "Image Item",
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        Color.Transparent
                                    )
                                    .clickable(onClick = if (userdata == null) onSignInClick else onSignOutClick)
                                    .clip(CircleShape)
                                    .alpha(VisibiltyOfText)
                                    .size(80.dp)
                            )

                            Spacer(modifier = Modifier.height(5.dp))
                            var nam = ""
                            if (userdata?.userName != null){
                                for (l in 0 until  userdata.userName.length){

                                    if (userdata.userName[l].toString() == " "){
                                        break
                                    }
                                    nam += userdata.userName[l].toString()
                                }
                            }

                            Text(
                                text = nam.ifEmpty {
                                    "SignIn"
                                } ,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                modifier = Modifier
                                    .alpha(VisibiltyOfText)
                            )
                        }


                    }
                }


//                Spacer(modifier = Modifier.width(8.dp))


//            LazyRow(
//                state = lazyRowState,
//                modifier = Modifier
//                    .fillMaxWidth() // Make the LazyRow fill the available width
//                    .height(rowHeight),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//
//
//                items(
//                    count = names_types.value.size, // Limit to a very large number
//                ) { index ->
//
//
//
//
//                    val nameType =  names_types.value[index]
//                    val uriImag = VM.getImage(nameType, navController.context)
//                    val image = rememberAsyncImagePainter(
//                        model = ImageRequest.Builder(navController.context).data(uriImag)
//                            .crossfade(true).error(R.drawable.error).placeholder(R.drawable.logo)
//                            .build()
//                    )
//                    val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(500))
//                    Log.e("Row", "Home:$uriImag ")
//
//                    AnimatedVisibility(
//                        visible = true,
//                        enter = slideInHorizontally() + fadeIn(),
//                        exit = slideOutHorizontally() + fadeOut()
//                    ) {
//
//                        Box(
//                            modifier = Modifier
//                                .width(100.dp)  // Set fixed width for each item
//                                .padding(start = text)  // Padding around each item
//                                .graphicsLayer(scaleX = scale, scaleY = scale)
//                        ) {
//
//                                Item(image, nameType, test,names_types.value)
//
//
//                        }
//                    }
//
//                }
//            }
            }


// Track whether all images are loaded



                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier.padding(start = 8.dp),
                ){



                    items(
                        album.size,
                        key = {indx->
                            PlayLists[indx]
                        }
                    )
                    {index->
                        val Playlist =PlayLists.get(index)
                        val name = album.get(Playlist)?.name ?:"Loading.."
                        Log.e("Album","${album}  ${PlayLists[index]}")
                        val imagePainter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(album.get(Playlist)?.images?.firstOrNull()?.url,)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.ENABLED) // Enable disk caching
                                .memoryCachePolicy(CachePolicy.ENABLED) //
                                .size(100) // Specify the target size (in pixels, not dp)
                                .build()
                        )
                            Item2(imagePainter,name,navController, PlayLists[index])

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
fun Item2(
    image: AsyncImagePainter,
    type: String,
    navController: NavController,
    cat:String
    ) {

    Box(
        modifier = Modifier
            .background(
                Color.Transparent, RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate("list/$cat")
            })
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

                Image(
                    painter = image,
                    contentDescription = "Image Item",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .background(
                            Color.Transparent
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .size(100.dp)
                )



            Spacer(Modifier.height(5.dp))
            Text(
                text = type,
                color = Color.White,
                fontSize = 13.sp,
                minLines = 1
            )
        }
    }


}

@Composable
fun Item(
    image: AsyncImagePainter,
    type: String,
    Height: Float,
    list: List<String>,

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
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(list[1]) }
        list.forEach{name->
            Row(
                Modifier
                    .selectable(
                        selected = (name == selectedOption),
                        onClick = { onOptionSelected(name) }
                    )
                    .background(color = if (name == selectedOption) colorResource(R.color.icon) else colorResource(R.color.background),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(start = 4.dp)
                    .width(150.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name, color = Color.White, fontSize = 14.sp, maxLines = 1, textAlign = TextAlign.Center
                )
            }

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



//            LazyColumn(
//                state = lazyGridState,
//                modifier = Modifier.padding(start = 8.dp)
//            ) {
//                items(
//                    names_types.value.size,
//                    key = {index ->
//                        names_types.value[index].ifEmpty {
//                            "First_key"
//                        }
//                    }
//                ) { index ->
//                    val names = names_types.value[index]
//
//
//                    LaunchedEffect(Unit) {
//                        scope.launch{
//                            VM.pop(names_types.value[index])
//                        }
//                    }
//
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceAround,
//                        modifier = Modifier.padding(top = 30.dp, bottom = 8.dp)
//                    ) {
//                        Text(
//                            text = names,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 30.sp,
//                            color = Color.White,
//                        )
//
//                        IconButton(
//                            onClick = {},
//                            ) {
//                            Icon(
//                                imageVector = Icons.Default.ChevronRight,
//                                contentDescription = "More",
//                                tint = Color.White,
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .padding(bottom = 8.dp),
//                            )
//                        }
//                    }
//
//
//
//                        LazyRow(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            var l = 0
//                                Log.e("expe", "Home: ${pop.value[names]} +${names}",)
//                                items(
//                                    3,
//                                    key = {index->
//                                        pop.value[names]?.get(index) ?: l++
//                                    }
//                                ) { index ->
//
//
//                                }
//
//
//                        }
//
//
//
//
//                }
//            }