package online.song.onlinesong.Service
import android.R.attr.action
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadService
import online.song.onlinesong.CHANNEL_ID
import online.song.onlinesong.CHANNEL_NAME
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.MainActivity
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM
import java.util.Collections.list


const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"
class Service:Service() {


    lateinit var exoPlayer: ExoPlayer
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY // Ensures the service keeps running if killed by the system
    }
    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(application).build()
        val context: Context = applicationContext
        createNotificationChannel(context)

        val filter = IntentFilter().apply {
            addAction(Util.ACTION_PLAY)
            addAction(Util.ACTION_PAUSE)
            addAction(Util.ACTION_NEXT)
            addAction(Util.ACTION_PREV)
            addAction(Util.SLIDER_STATE_CHANNEL)
            addAction(Util.PLAYER_STATE_CHANNEL)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(MusicService(), filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(MusicService(), filter!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(MusicService())

        stopSelf()

    }





    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    fun showNotification(Song: String, Artist: String, context: Context, exoPlayer: ExoPlayer,isPlaying: Boolean):Notification {

        // Build the notification
        val playPauseIntent = Intent(context, MusicService::class.java).apply {
            action = if (isPlaying) Util.ACTION_PAUSE else Util.ACTION_PLAY
        }
        val playPausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Next Action
        val nextIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_NEXT
        }
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Previous Action
        val prevIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_PREV
        }
        val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(context)


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(Song)
            .setContentText(Artist)
            .setSmallIcon(R.drawable.logo)
            .addAction(
                if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent) // Add next action
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", prevPendingIntent) // Add previous action
            .setOngoing(exoPlayer.isPlaying) // Set the ongoing flag based on player state
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getPendingIntent(context, Util.PLAYER_STATE_CHANNEL))
            .build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        return notification

    }
    fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(action)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}