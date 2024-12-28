package online.song.onlinesong.Screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.R
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
    cat: String,
    userData: UserData?
){
    val songs = viewModel.ListSongs.observeAsState(emptyMap<String, List<String>>())
    val n_s = songs.value[name]
    val n = songs.value[name]?.size

    var exoPlayer = remember { ExoPlayer.Builder(navController.context).build() }
    val shuffleMode = remember { mutableStateOf(exoPlayer.shuffleModeEnabled) }
    val repeatMod = remember { mutableIntStateOf(Player.REPEAT_MODE_OFF) }
    var songList = remember { mutableListOf<MediaItem>() }
    var isPlaying =  remember { mutableStateOf(exoPlayer.isPlaying) }
    var isAdd = viewModel.isAdd.observeAsState(Boolean)


    val PS = remember { mutableLongStateOf(0L) }





    var totalTime = remember { mutableStateOf("00:00") }
    var nam = remember { mutableStateOf("Loading...") }
    var currentTime= remember { mutableStateOf("00:00") }
    val valueSlider = remember { mutableFloatStateOf(0f) }

    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(viewModel.getImage(name,navController.context))
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.logo)
            .build()
    )
    val totalDuration = remember { mutableLongStateOf(0L) }
    var o = remember{ mutableIntStateOf(0) }
    for (l in 0 until n_s!!.size){
        if (n_s[l] == nameS){
            o.intValue = l
            break
        }
    }
    LaunchedEffect(Unit) {
        if (n != null){
            for (index in 0 until songs.value[name]!!.size){
                val elemnet = viewModel.getSongs(n_s[index],navController.context)
                if (songList.contains(MediaItem.fromUri(elemnet)) == false){
                    songList.add(index,MediaItem.fromUri(elemnet))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
       viewModel.listSongs(cat,name)


   }


DisposableEffect(Unit) {
    onDispose {
        exoPlayer.release()
    }
}
    LaunchedEffect (exoPlayer,o.intValue,songList,userData) {
        exoPlayer.setMediaItems(songList)
        exoPlayer.seekToDefaultPosition(o.intValue)
        exoPlayer.prepare()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        totalTime.value = TotalTime(exoPlayer.duration)
                        totalDuration.longValue = exoPlayer.duration
                        PS.longValue = exoPlayer.currentPosition
                        isPlaying.value = exoPlayer.isPlaying
                        nam.value = n_s[exoPlayer.currentMediaItemIndex].toString()
                        if (userData != null){
                            viewModel.check(n_s[exoPlayer.currentMediaItemIndex],userData)
                        }
                    }

                    Player.STATE_ENDED -> {
                        // Handle end of playback, if needed
                        isPlaying.value = false
                        exoPlayer.seekToNext()
                    }




                }

            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                repeatMod.intValue = repeatMode
            }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                shuffleMode.value = shuffleModeEnabled
            }


        })
        songList.forEach {name ->
            Log.d("List inside of LaunchEffect", "playSong:$name ${o.intValue}")
        }

    }

      viewModel.viewModelScope.launch{
          while (isPlaying.value) {
              valueSlider.floatValue = exoPlayer.currentPosition.toFloat()
              currentTime.value = TotalTime(exoPlayer.currentPosition)
              delay(1000L) // Update every second
              Log.e("exoplayer-Error1", "playSong:${valueSlider.floatValue}  ${currentTime.value}    ${PS.longValue} ", )
          }
          Log.e("exoplayer-Error1", "playSong: ${exoPlayer.isPlaying}")

      }





    Log.d("durection","${exoPlayer.bufferedPosition}")
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
                        navController.navigate("list/$name/$cat")
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
                        viewModel.Action(SongEvent.Favorit(n_s[o.intValue],userData!!),navController.context)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Localized description",
                            tint = colorResource(if (isAdd.value != true) R.color.unfocus else R.color.icon)   ,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                }

            )
        }
    ) {padding ->
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

            ){




                    Slider(
                        value = valueSlider.floatValue,
                        onValueChange = {
                            exoPlayer.seekTo(it.toLong())
                        },
                        valueRange = 0f..(totalDuration.longValue ?: 1L).toFloat(),
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
                    text = currentTime.value ,
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
            ){





                Spacer(modifier = Modifier.weight(0.05f))
                IconButton(onClick = {
                    exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
                }) {
                    Icon(
                        imageVector = if (shuffleMode.value == false)
                            Icons.Default.Shuffle
                        else Icons.Default.ShuffleOn,
                        contentDescription = "SkipPrevious",
                        tint =if (exoPlayer.shuffleModeEnabled == false)
                            colorResource(R.color.White)
                        else colorResource(R.color.icon)
                        ,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))


                IconButton(onClick = {
                    if (exoPlayer.currentMediaItemIndex > 0) {
                        exoPlayer.seekToPrevious()
                    } else {
                        exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0) // Loop to the last item
                    }

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

                        if (isPlaying.value) {
                            exoPlayer.pause() // Pause playback
                        } else {
                            exoPlayer.play() // Resume or start playback
                            songList.forEach{name->
                                Log.d("ExoPlayer", "List song: ${name}")
                            }
                        }
                        // Update the state to reflect the current playback state
                        isPlaying.value = exoPlayer.isPlaying


                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.icon)),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector =if(isPlaying.value == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = colorResource(R.color.White),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                IconButton(onClick = {

                    if (exoPlayer.currentMediaItemIndex < exoPlayer.mediaItemCount - 1) {
                        exoPlayer.seekToNext()
                    } else {
                        exoPlayer.seekTo(0, 0) // Loop to the first item
                    }

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
                    if (exoPlayer.repeatMode == repeatMod.intValue) {
                        repeatMod.intValue = Player.REPEAT_MODE_ONE
                        exoPlayer.repeatMode = repeatMod.intValue
                    } else {
                        repeatMod.intValue = Player.REPEAT_MODE_OFF
                        exoPlayer.repeatMode =  repeatMod.intValue
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.RepeatOne,
                        contentDescription = "Repeat Song",
                        tint = if (exoPlayer.repeatMode == Player.REPEAT_MODE_OFF)
                            colorResource(R.color.White)
                        else colorResource(R.color.icon)
                        ,
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
fun TotalTime(lon: Long):String{
    val sec = lon/1000
    val min = sec / 60
    val seconds = sec % 60
    return String.format("%02d:%02d", min, seconds)
}

