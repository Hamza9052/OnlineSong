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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import online.song.onlinesong.Events.dataSong
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


    val names_types = VM.Categories.observeAsState(emptyList<String>())
    var isLoading = VM.isLoading.observeAsState(Boolean)
    var CatisLoading = VM.CatisLoading.observeAsState(Boolean)
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
    val scope = rememberCoroutineScope()


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

        if (isLoading.value == true && names_types.value.isEmpty() ) {
            Spacer(modifier = Modifier.weight(0.3f))
               LazyRow(
                   horizontalArrangement = Arrangement.spacedBy(15.dp)
               ) {
                   items(10) { index ->
                       Process()
                   }

               }

               Spacer(modifier = Modifier.weight(1f))
                   Process()
            Spacer(modifier = Modifier.weight(1f))



        }
        else if (names_types.value.isNotEmpty()) {
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


                Spacer(modifier = Modifier.width(8.dp))


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
                            .crossfade(true).error(R.drawable.error).placeholder(R.drawable.logo)
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

                                Item(image, nameType, test,names_types.value)


                        }
                    }

                }
            }
            }


            LazyColumn(
                state = lazyGridState,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                items(
                    names_types.value.size,
                    key = {index ->
                        names_types.value[index].ifEmpty {
                            "First_key"
                        }
                    }
                ) { index ->
                    val names = names_types.value[index]


                    LaunchedEffect(Unit) {
                        scope.launch{
                            VM.pop(names_types.value[index])
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.padding(top = 30.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = names,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color.White,
                        )

                        IconButton(
                            onClick = {},
                            ) {


                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(bottom = 8.dp),
                            )
                        }
                    }



                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var l = 0
                                Log.e("expe", "Home: ${pop.value[names]} +${names}",)
                                items(
                                    3,
                                    key = {index->
                                        pop.value[names]?.get(index) ?: l++
                                    }
                                ) { index ->

                                    val nameType = pop.value[names]?.get(index) ?: "Loading.."
                                    val uriImag =
                                        VM.getImage(nameType.toString(), navController.context)
                                    val image = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(navController.context)
                                            .data(uriImag)
                                            .crossfade(true)
                                            .error(R.drawable.error)
                                            .placeholder(R.drawable.logo)
                                            .build()
                                    )
                                    val scale by animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = tween(500)
                                    )


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

                                            Item2(image, nameType.toString(), navController, names)

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
            .padding(0.dp)
            .clickable(onClick = {
                navController.navigate("list/$type/$cat")
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
                    modifier = Modifier
                        .background(
                            Color.Transparent
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .size(120.dp)
                )



            Spacer(Modifier.height(5.dp))
            Text(
                text = type,
                color = Color.White,
                fontSize = 19.sp,
                minLines = 1
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