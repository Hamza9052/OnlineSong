package online.song.onlinesong.Service
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import online.song.onlinesong.Events.PlayerAction
import online.song.onlinesong.Events.PlayerState
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.MainActivity

import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import kotlin.math.roundToInt
import kotlin.math.roundToLong

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"

class MusicService : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private var scope = CoroutineScope(Dispatchers.Default)
    private lateinit var musicViewModel: songVM
    val playerStateReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let{
                val playerState = it.getParcelableExtra<PlayerState>(Util.PLAYER)
                playerState?.let{play ->
                    onPlayerAction(play)
                }
            }
        }

    }

    val sliderStateReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let{
                val value = it.getFloatExtra(Util.SLIDER_CHANGE_VALUE,0f)
                updateDuration(value)
            }
        }

    }

    private fun updateDuration(value: Float) {
        if (value != 0f){
            exoPlayer.seekTo(value.roundToLong())
        }
    }

    fun onPlayerAction(event: PlayerState){
        when(event.action){
            PlayerAction.PLAY -> {
                exoPlayer.play()
            }
            PlayerAction.PAUSE -> {
                exoPlayer.pause()
            }
            PlayerAction.NEXT -> {
                if (exoPlayer.currentMediaItemIndex < exoPlayer.mediaItemCount - 1) {
                    exoPlayer.seekToNext()
                } else {
                    exoPlayer.seekTo(0, 0) // Loop to the first item
                }
            }
            PlayerAction.PREV -> {
                if (exoPlayer.currentMediaItemIndex > 0) {
                    exoPlayer.seekToPrevious()
                } else {
                    exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0) // Loop to the last item
                }
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()

        // Initialize ViewModel (adjust for your ViewModel implementation)
        musicViewModel = ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[songVM::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()

        registerReceiver(playerStateReceiver, IntentFilter(Util.PLAYER_STATE_CHANNEL),
            Context.RECEIVER_NOT_EXPORTED)
        registerReceiver(sliderStateReceiver, IntentFilter(Util.SLIDER_STATE_CHANNEL),
            Context.RECEIVER_NOT_EXPORTED)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer.isPlaying){
            exoPlayer.stop()
        }
        exoPlayer.release()
        stopSelf()
        scope.cancel()
        unregisterReceiver(playerStateReceiver)
        unregisterReceiver(sliderStateReceiver)
    }


    private fun startNotification(){
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Song Title") // Use your dynamic title here
            .setContentText("Artist Name") // Use your dynamic artist name here
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // To prevent the notification from being swiped away
            .build()

        startForeground(1,notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
