package online.song.onlinesong.Events

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicData(
    val id: Long? = null,
    val name: String? =null,
    val duration: Long? =null,
    val filePath: String? = null,
) : Parcelable
