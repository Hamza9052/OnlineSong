package online.song.onlinesong.Events

import androidx.media3.exoplayer.ExoPlayer
import online.song.onlinesong.LoginWithGoogle.SignResult
import online.song.onlinesong.LoginWithGoogle.UserData

interface SongEvent {
    data class Play(val list:List<String> ): SongEvent
    data class Pause(val list:List<String> ): SongEvent
    data class Stop(val list:List<String> ): SongEvent
    data class Next(val list:List<String> ): SongEvent
    data class Prev(val list:List<String> ): SongEvent
    data class Favorit(val name: String, val result: UserData,val singer:String): SongEvent
    data class checkFavoriteSong(val result: UserData): SongEvent
}