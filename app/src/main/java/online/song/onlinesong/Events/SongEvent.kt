package online.song.onlinesong.Events

interface SongEvent {
    data class Play(val list:List<String> ): SongEvent
    data class Pause(val list:List<String> ): SongEvent
    data class Stop(val list:List<String> ): SongEvent
    data class Next(val list:List<String> ): SongEvent
    data class Prev(val list:List<String> ): SongEvent
}