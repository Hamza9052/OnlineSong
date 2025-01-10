package online.song.onlinesong.Events

import android.os.Parcelable
import androidx.credentials.provider.Action
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerState(
    var action: PlayerAction = PlayerAction.PLAY
): Parcelable


enum class PlayerAction{
    PLAY,
    PAUSE,
    NEXT,
    PREV
}