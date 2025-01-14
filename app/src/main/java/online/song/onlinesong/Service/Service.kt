package online.song.onlinesong.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat.Token
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import online.song.onlinesong.MainActivity
import online.song.onlinesong.R

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "Music Playback"

class Service : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val notificationId = 1
    override fun onCreate() {
        super.onCreate()

        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(this).build()

        // Initialize MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE))
            .build()

        // Bind MediaSession to ExoPlayer


        // Create Notification Channel
        createNotificationChannel()

        // Add listener to update notifications on state changes
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updateNotification()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return START_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Util.ACTION_PLAY -> exoPlayer.play()
            Util.ACTION_PAUSE -> exoPlayer.pause()
            Util.ACTION_NEXT -> {
                if (exoPlayer.currentMediaItemIndex < exoPlayer.mediaItemCount - 1) {
                    exoPlayer.seekToNext()
                } else {
                    exoPlayer.seekTo(0, 0) // Loop to the first item
                }
            }
            Util.ACTION_PREV -> {
                if (exoPlayer.currentMediaItemIndex > 0) {
                    exoPlayer.seekToPrevious()
                } else {
                    exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0) // Loop to the last item
                }
            }
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(notificationId, notification)
    }

     fun createNotification(): Notification {
        val playPauseAction = if (exoPlayer.isPlaying) {
            NotificationCompat.Action(
                R.drawable.baseline_pause_24, "Pause",
                getPendingIntent(Util.ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.baseline_play_arrow_24, "Play",
                getPendingIntent(Util.ACTION_PLAY)
            )
        }


        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Now Playing")
            .setContentText("Song Name - Artist") // Replace with actual metadata
            .setSmallIcon(R.drawable.logo)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.baseline_skip_previous_24, "Previous",
                    getPendingIntent(Util.ACTION_PREV)
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.baseline_skip_next_24, "Next",
                    getPendingIntent(Util.ACTION_NEXT)
                )
            )
            .setStyle(
                MediaStyle().setMediaSession(mediaSession.token as Token)
            )
            .setOnlyAlertOnce(true)
            .setOngoing(exoPlayer.isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, Service::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        mediaSession.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}