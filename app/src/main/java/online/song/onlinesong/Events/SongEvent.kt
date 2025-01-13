package online.song.onlinesong.Events

import android.os.Parcelable
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.android.parcel.Parcelize
import online.song.onlinesong.LoginWithGoogle.SignResult
import online.song.onlinesong.LoginWithGoogle.UserData
import java.io.Serializable

@Parcelize
sealed class SongEvent : Parcelable {
    @Parcelize
    object PLAY : SongEvent()

    @Parcelize
    object PAUSE : SongEvent()

    @Parcelize
    object NEXT : SongEvent()

    @Parcelize
    object PREV : SongEvent()

    @Parcelize
    data class Favorit(val name: String, val result: UserData, val singer: String) : SongEvent()

    @Parcelize
    data class CheckFavoriteSong(val result: UserData) : SongEvent()

    @Parcelize
    data class ListSong(val list: List<String>) : SongEvent()
}