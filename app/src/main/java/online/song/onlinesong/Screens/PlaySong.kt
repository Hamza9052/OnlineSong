package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieClipSpec.Progress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import kotlin.math.log

/**
 * @author Hamza Ouaissa
 */
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun playSong(
    viewModel: songVM,
    navController: NavController,
    name:String,
    nameS:String,
    cat:String
){
    val songs = viewModel.ListSongs.observeAsState(emptyMap<String, List<String>>())
    val n_s = songs.value[name]
    val n = songs.value[name]?.size

    var exoPlayer = ExoPlayer.Builder(navController.context).build()
    val shuffleMode = remember { mutableStateOf(exoPlayer.shuffleModeEnabled) }
    val repeatMod = remember { mutableIntStateOf(Player.REPEAT_MODE_OFF) }
    val songList = remember { mutableListOf<MediaItem>() }



    val isPlaying = remember { mutableStateOf(false) }
    val currentPosition = remember { mutableStateOf(0L) }



    var scope = rememberCoroutineScope()
    var totalTime = remember { mutableStateOf("00:00") }
    val sliderValue = remember { mutableStateOf(0f) }
    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(viewModel.getImage(name,navController.context))
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.error)
            .build()
    )
    val totalDuration = remember { mutableStateOf(0L) }
    var tim = remember{ mutableStateOf("00:00") }
    var o = remember{ mutableIntStateOf(0) }
    for (l in 0 until n_s!!.size){
        if (n_s[l] == nameS){
            o.intValue = l
            break
        }
    }
    LaunchedEffect(isPlaying.value) {
        while (isPlaying.value) {
            currentPosition.value = exoPlayer.currentPosition
            sliderValue.value = currentPosition.value.toFloat() / totalDuration.value
            delay(500L)
        }
    }
    if (n != null){
        for (index in 0 until songs.value[name]!!.size){
            val elemnet = viewModel.getSongs(n_s[index],navController.context)
            if (songList.contains(MediaItem.fromUri(elemnet)) == false){
                songList.add(index,MediaItem.fromUri(elemnet))
            }

            Log.d("index", "playSong: ${n_s[index]}   ->${index}")
        }
    }

    LaunchedEffect(Unit) {
       viewModel.listSongs(cat,name)
   }






    Log.d("durection","${exoPlayer.currentPosition}")
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

                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Localized description",
                            tint = colorResource(R.color.unfocus),
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
                n_s[o.intValue],
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorResource(R.color.White),
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,

            ){

                Slider(
                    value = sliderValue.value,
                    onValueChange = {
                        sliderValue.value = it
                        exoPlayer.seekTo((it * totalDuration.value).toLong())
                                    },
                    valueRange = 0f..1f,

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
                        disabledThumbColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                scope.launch{
                  tim.value =  time(exoPlayer.currentPosition,exoPlayer.isPlaying)
                }
                Text(
                    text =tim.value,
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

                exoPlayer.setMediaItem(songList[o.intValue])
                exoPlayer.prepare()
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            // Retrieve and format the total time
                            totalTime.value  = TotalTime(exoPlayer.duration)
                            totalDuration.value = exoPlayer.duration
                            Log.d("TotalTime", totalTime.toString())
                        }

                    }

                    override fun onIsPlayingChanged(isPlayin: Boolean) {
                        isPlaying.value = isPlayin
                    }
                    override fun onRepeatModeChanged(repeatMode: Int) {
                        repeatMod.intValue = repeatMode
                    }
                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        shuffleMode.value = shuffleModeEnabled
                    }

                })
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
                        else colorResource(R.color.DeepPink)
                        ,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))


                IconButton(onClick = {

                    if (exoPlayer.currentMediaItemIndex == 0) {
                        o.intValue = songList.size - 1
                    } else {
                        exoPlayer.seekToPrevious()
                        o.intValue =- 1
                    }
                    Log.d("index", "playSong: ${o}   ->${exoPlayer.currentMediaItemIndex}")
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

                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.icon)),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector =if(exoPlayer.isPlaying == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = colorResource(R.color.White),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                IconButton(onClick = {
                    if (exoPlayer.currentMediaItemIndex == exoPlayer.mediaItemCount - 1) {
                        exoPlayer.seekTo(0)
                        o.intValue = 0
                    } else {
                        exoPlayer.seekToNext()
                        o.value += 1
                    }
                    Log.d("index", "playSong: ${o}   ->${exoPlayer.currentMediaItemIndex}")
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
                        else colorResource(R.color.DeepPink)
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

fun TotalTime(lon: Long):String{
    val sec = lon/1000
    val min = sec / 60
    val seconds = sec % 60
    val minutesString = if (min < 10) {
        "0$min"
    } else {
        min.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}

suspend fun time(lon: Long,boolean: Boolean):String{
    val sec = lon/1000
    var min:Long = 0
    var seconds:Long = 0
    while (boolean == true){
        seconds += sec % 60
        if (seconds.toInt() == 60){
            min += 1
            seconds = 0
        }else{
            seconds += sec % 60
        }
        delay(1000)
    }
    val minutesString = if (min < 10) {
        "0$min"
    } else {
        min.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "minutesString:secondsString"
}