package online.song.onlinesong.Service
import android.app.Application
import android.app.PendingIntent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import androidx.media3.exoplayer.ExoPlayer
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.Service.Service
import online.song.onlinesong.ViewModel.songVM
import kotlin.math.roundToLong



class MusicService : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {nonNullContext->
            val serviceIntent = Intent(nonNullContext, Service::class.java).apply {
                action = intent?.action
            }
            nonNullContext.startService(serviceIntent)
            val applicationContext = nonNullContext.applicationContext as Application
            when (intent?.action) {
                Util.ACTION_PLAY -> {

                    val service = nonNullContext.applicationContext as Service
                    service.getViewModel().Action(SongEvent.PLAY, nonNullContext)
                }
                Util.ACTION_PAUSE -> {
                    val service = nonNullContext.applicationContext as Service
                    service.getViewModel().Action(SongEvent.PAUSE, nonNullContext)
                }
                Util.ACTION_NEXT -> {
                    val service = nonNullContext.applicationContext as Service
                    service.getViewModel().Action(SongEvent.NEXT, nonNullContext)
                }
                Util.ACTION_PREV -> {
                    val service = nonNullContext.applicationContext as Service
                    service.getViewModel().Action(SongEvent.PREV, nonNullContext)
                }
            }
        }


    }



}
