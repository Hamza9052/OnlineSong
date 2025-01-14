package online.song.onlinesong.Screens

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.exoplayer2.Player
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.R
import online.song.onlinesong.Service.Util
import online.song.onlinesong.ViewModel.songVM



/**
 * @author Hamza Ouaissa
 */
@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun playSong(
    viewModel: songVM,
    navController: NavController,
    name: String,
    nameS: String,
    list: List<String>,
    userData: UserData?
) {


    var exoPlayer = viewModel.exoPlayer
    val shuffleMode =viewModel.shuffleMode.observeAsState(false)
    val repeatMod = viewModel.repeatMod.observeAsState(0)
    var songList = viewModel.items.observeAsState(emptyList())
    var isPlaying = viewModel.isPlaying.observeAsState(false)
    var isAdd = viewModel.isAdd.observeAsState(Boolean)

    val PS = viewModel.PS.observeAsState(0L)
    val lists = remember { mutableListOf<String>() }

    val totalDuration = viewModel.totalDuration.observeAsState(0L)
    var totalTime = viewModel.CurrentT.observeAsState("00:00")
    var nam = viewModel.nam.observeAsState("Loading...")
    var currentTime = viewModel.currentTime.observeAsState(initial = "")
    val valueSlider = viewModel.valueSlider.observeAsState(initial = 0f)



    LaunchedEffect(list) {
        viewModel.Action(SongEvent.ListSong(list),navController.context)
    }
    LaunchedEffect(exoPlayer,isPlaying.value) {
        viewModel.slider(isPlaying.value,exoPlayer)
    }






//    var permission = android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//        permission = android.Manifest.permission.READ_MEDIA_AUDIO
//    } else {
//        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
//    }
//

    // Example: Slider value change broadcast
    LaunchedEffect(valueSlider.value) {
        navController.context.sendBroadcast(Intent(Util.SLIDER_STATE_CHANNEL).apply {
            putExtra(Util.SLIDER_CHANGE_VALUE, valueSlider.value)
        })
    }
    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(viewModel.getImage(name, navController.context))
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.logo)
            .build()
    )

    var o = remember { mutableIntStateOf(0) }
    for (l in 0 until list.size) {
        if (list[l] == nameS) {
            o.intValue = l
            break
        }
    }


    LaunchedEffect(songList.value) {
        songList.value.forEach() {
            lists.add(it.toString())
        }

    }





    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    LaunchedEffect(exoPlayer, o.intValue, songList, userData,navController) {

        viewModel.preparePlayer(navController.context,songList.value,userData,list,o.intValue)
    }






    Log.d("durection", "${exoPlayer.bufferedPosition}")
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.background),
                    titleContentColor = colorResource(R.color.White),
                ),
                title = {
                    Text(
                        name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(R.color.White),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        o.intValue = 0
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Localized description",
                            tint = colorResource(R.color.icon),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                actions = {

                    IconButton(onClick = {
                        viewModel.Action(
                            SongEvent.Favorit(
                                list[o.intValue],
                                userData!!,
                                name
                            ), navController.context
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Localized description",
                            tint = colorResource(if (isAdd.value != true) R.color.unfocus else R.color.icon),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                }

            )
        }
    ) { padding ->
        padding.calculateTopPadding()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(color = colorResource(R.color.background))
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(30.dp)),
                painter = image,
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,

                )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                nam.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorResource(R.color.White),
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,

                ) {


                Slider(
                    value = valueSlider.value,
                    onValueChange = {
                        exoPlayer.seekTo(it.toLong())
                    },
                    valueRange = 0f..(totalDuration.value ?: 1L).toFloat(),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                // Thumb size
                                .background(Color.White, CircleShape),
                        )

                    },
                    colors = SliderDefaults.colors(
                        activeTrackColor = colorResource(R.color.icon),
                        inactiveTrackColor = Color.DarkGray,
                        thumbColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .weight(1f)
                )


            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = currentTime.value,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.unfocus),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = totalTime.value,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.unfocus),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {


                Spacer(modifier = Modifier.weight(0.05f))
                IconButton(
                    onClick = {
                    exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
                }) {
                    Icon(
                        imageVector = if (shuffleMode.value == false)
                            Icons.Default.Shuffle
                        else Icons.Default.ShuffleOn,
                        contentDescription = "shuffleMode",
                        tint = if (exoPlayer.shuffleModeEnabled == false)
                            colorResource(R.color.White)
                        else colorResource(R.color.icon),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))


                IconButton(
                    onClick = {

//                        viewModel.Action(SongEvent.PREV,navController.context)
                        viewModel.Action(SongEvent.PREV,navController.context)
                         // Update with the current song info
                })
                {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "SkipPrevious",
                        tint = colorResource(R.color.White),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Button(
                    onClick = {
                        if (isPlaying.value == true){
                            viewModel.Action(SongEvent.PAUSE,navController.context)
                        }else{
                            viewModel.Action(SongEvent.PLAY,navController.context)
                        }

                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.icon)),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying.value == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = colorResource(R.color.White),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                IconButton(onClick = {
                viewModel.Action(SongEvent.NEXT,navController.context)

                })
                {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "SkipNext",
                        tint = colorResource(R.color.White),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))

                IconButton(onClick = {
                    if (exoPlayer.repeatMode == repeatMod.value) {
//                        repeatMod.value = Player.REPEAT_MODE_ONE
                        exoPlayer.repeatMode = repeatMod.value
                    } else {
//                        repeatMod.value = Player.REPEAT_MODE_OFF
                        exoPlayer.repeatMode = repeatMod.value
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.RepeatOne,
                        contentDescription = "Repeat Song",
                        tint = if (exoPlayer.repeatMode == Player.REPEAT_MODE_OFF)
                            colorResource(R.color.White)
                        else colorResource(R.color.icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(0.05f))

            }
            Spacer(modifier = Modifier.weight(0.8f))


        }
    }

}


@SuppressLint("DefaultLocale")
fun TotalTime(lon: Long): String {
    val sec = lon / 1000
    val min = sec / 60
    val seconds = sec % 60
    return String.format("%02d:%02d", min, seconds)
}

