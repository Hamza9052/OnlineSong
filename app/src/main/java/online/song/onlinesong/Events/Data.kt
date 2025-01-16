package online.song.onlinesong.Events

data class Playlist(
    val id: String,
    val name: String,
    val uri: String
)

// Model class for Track
data class Track(
    val id: String,
    val name: String,
    val uri: String,
    val artists: List<Artist>,
    val album: Album
)

// Model class for Artist
data class Artist(
    val name: String,
    val id: String
)

// Model class for Album
data class Album(
    val name: String,
    val images: List<Image>
)

// Model class for Image
data class Image(
    val url: String,
    val height: Int,
    val width: Int
)

// Model class for TrackDetails
data class TrackDetails(
    val id: String,
    val name: String,
    val duration_ms: Int,  // Track duration in milliseconds
    val artists: List<Artist>,
    val album: Album
)
