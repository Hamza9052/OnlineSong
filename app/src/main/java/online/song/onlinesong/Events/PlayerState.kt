package online.song.onlinesong.Events

import android.os.Parcelable
import androidx.credentials.provider.Action
import kotlinx.parcelize.Parcelize


data class PlayerState(
    val id : Long? = null,
    val music: MusicData? = null,
    var progress: Long? = null,
    var action: PlayerAction = PlayerAction.PLAY
)

enum class PlayerAction{
    PLAY,
    PAUSE,
    NEXT,
    PREVIOUS
}