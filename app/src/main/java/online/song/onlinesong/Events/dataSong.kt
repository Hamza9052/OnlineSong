package online.song.onlinesong.Events

import androidx.compose.runtime.Immutable

@Immutable
data class dataSong(
    val name: String ="",
    val search:String = "",
    var list: List<String> = listOf("")
)
