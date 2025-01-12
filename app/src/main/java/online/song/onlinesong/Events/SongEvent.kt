package online.song.onlinesong.Events

import androidx.media3.exoplayer.ExoPlayer
import online.song.onlinesong.LoginWithGoogle.SignResult
import online.song.onlinesong.LoginWithGoogle.UserData

interface SongEvent {
    data object PLAY: SongEvent
    data object PAUSE: SongEvent
    data object NEXT: SongEvent
    data object PREV: SongEvent
    data class Favorit(val name: String, val result: UserData,val singer:String): SongEvent
    data class checkFavoriteSong(val result: UserData): SongEvent
    data class ListSong(val list: List<String>): SongEvent
}