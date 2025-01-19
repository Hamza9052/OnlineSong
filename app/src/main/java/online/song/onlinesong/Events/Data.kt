package online.song.onlinesong.Events


data class Playlist(
    val id: String,                     // Playlist ID
    val name: String,                   // Playlist name
    val description: String?,           // Playlist description (nullable)
    val images: List<Image>,            // Playlist images
    val tracks: PlaylistTracks          // Tracks in the playlist
)


data class PlaylistTracks(
    val items: List<PlaylistTrackItem>, // List of track items
    val total: Int                      // Total number of tracks
)

data class PlaylistTrackItem(
    val track: Track                    // Track details
)

data class Track(
    val id: String,                     // Track ID
    val name: String,                   // Track name
    val preview_url: String?,           // URL to a 30-second preview of the track
    val album: Album,                   // Album details
    val artists: List<Artist>           // List of artists for the track
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
