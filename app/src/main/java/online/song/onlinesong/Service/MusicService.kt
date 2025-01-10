package online.song.onlinesong.Service
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

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var viewModel: songVM
    private fun updateDuration(value: Float) {
        if (value != 0f){
            exoPlayer.seekTo(value.roundToLong())
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!::exoPlayer.isInitialized) {
            exoPlayer = ExoPlayer.Builder(context!!).build()
        }
        viewModel = ViewModelProvider(context as AppCompatActivity).get(songVM::class.java)
        when (intent?.action) {
            Util.ACTION_PLAY -> {
                // Trigger the play action in the ViewModel
                viewModel.Action(SongEvent.PLAY(exoPlayer),context)
            }
            Util.ACTION_PAUSE -> {
                // Trigger the pause action in the ViewModel
                viewModel.Action(SongEvent.PAUSE(exoPlayer),context)
            }
            Util.ACTION_NEXT -> {
                // Trigger the next action in the ViewModel
                viewModel.Action(SongEvent.NEXT(exoPlayer),context)
            }
            Util.ACTION_PREV -> {
                // Trigger the previous action in the ViewModel
                viewModel.Action(SongEvent.PREV(exoPlayer),context)
            }
        }
    }



}
