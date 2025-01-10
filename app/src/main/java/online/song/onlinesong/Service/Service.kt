package online.song.onlinesong.Service
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
import androidx.core.app.NotificationCompat

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadService
import online.song.onlinesong.CHANNEL_ID
import online.song.onlinesong.CHANNEL_NAME
import online.song.onlinesong.MainActivity
import online.song.onlinesong.R


const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"
class Service:Service() {

    lateinit var exoPlayer: ExoPlayer
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = showNotification("Song Name", "Artist Name", this, exoPlayer)

        // Start the service in the foreground with the notification

            startForeground(1, notification)  // Start the service as a foreground service with the notification


        return START_STICKY // Ensures the service keeps running if killed by the system
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext
        createNotificationChannel(context)
        val notification = showNotification("Song Name", "Artist Name", this, exoPlayer)

        // Start the service in the foreground

            startForeground(1, notification)  // Start the service as a foreground service with the notification


        exoPlayer = ExoPlayer.Builder(this).build()
        val filter = IntentFilter().apply {
            addAction(Util.ACTION_PLAY)
            addAction(Util.ACTION_PAUSE)
            addAction(Util.ACTION_NEXT)
            addAction(Util.ACTION_PREV)
            addAction(Util.SLIDER_STATE_CHANNEL)
            addAction(Util.PLAYER_STATE_CHANNEL)
        }

        registerReceiver(MusicService() ,filter!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(MusicService())
        exoPlayer.release()
        if (exoPlayer.isPlaying){
            exoPlayer.stop()
        }
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    fun showNotification(Song: String, Artist: String, context: Context, exoPlayer: ExoPlayer):Notification {

        // Build the notification
        val playIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_PLAY
        }
        val playPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_PAUSE
        }
        val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_NEXT
        }
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(context, MusicService::class.java).apply {
            action = Util.ACTION_PREV
        }
        val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE
        )
        createNotificationChannel(context)


        val notification = NotificationCompat.Builder(context, "your_channel_id")
            .setContentTitle(Song)
            .setContentText(Artist)
            .setSmallIcon(R.drawable.logo)
            .addAction(
                if (exoPlayer.isPlaying) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_play_arrow_24,
                if (exoPlayer.isPlaying) "Pause" else "Play",
                if (exoPlayer.isPlaying)pausePendingIntent else playPendingIntent
            ) // Add pause action
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
                    "your_channel_id", "Music Service Channel", NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}