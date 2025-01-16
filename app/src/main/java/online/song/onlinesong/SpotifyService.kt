package online.song.onlinesong

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import online.song.onlinesong.Events.Playlist
import online.song.onlinesong.Events.Track
import online.song.onlinesong.Events.TrackDetails
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SpotifyService {

    // Fetch the current user's playlists
    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") authorization: String
    ): Response<List<Playlist>>

    // Fetch tracks from a specific playlist
    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String
    ): Response<List<Track>>

    // Fetch track details (e.g., duration, name, etc.)
    @GET("v1/tracks/{track_id}")
    suspend fun getTrackDetails(
        @Header("Authorization") authorization: String,
        @Path("track_id") trackId: String
    ): Response<TrackDetails>

    // Add more API calls as needed (e.g., search, get album details, etc.)

    companion object {
        // Base URL for Spotify Web API
        const val BASE_URL = "https://api.spotify.com/"

        fun create(): SpotifyService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(SpotifyService::class.java)
        }
    }
}