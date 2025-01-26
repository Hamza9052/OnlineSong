package online.song.onlinesong.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import androidx.navigation.NavController
import com.cloudinary.Cloudinary
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import online.song.onlinesong.Api
import online.song.onlinesong.Events.Album
import online.song.onlinesong.Events.Playlist
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.Events.Track
import online.song.onlinesong.LoginWithGoogle.SignInState
import online.song.onlinesong.LoginWithGoogle.SignResult
import online.song.onlinesong.LoginWithGoogle.UserData
import online.song.onlinesong.Screens.TotalTime
import online.song.onlinesong.Service.MediaNotificationManager
import online.song.onlinesong.SpotifyService
import javax.inject.Inject
import kotlin.collections.MutableList
import kotlin.collections.contains
import kotlin.Result
import kotlin.toString
import androidx.compose.runtime.setValue
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import online.song.onlinesong.Events.TrackDetails
import kotlin.collections.forEach
import kotlin.collections.isNullOrEmpty
import kotlin.collections.map

const val SESSION_INTENT_REQUEST_CODE = 1001

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class songVM @Inject constructor(application: Application) : ViewModel() {


    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    fun setIsPlaying(isPlaying: Boolean) {
        _isPlaying.postValue(isPlaying)
    }


    private var _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _items = MutableLiveData<MutableList<MediaItem>>(mutableListOf<MediaItem>())
    val exoPlayer = ExoPlayer.Builder(application).build()

    // Expose the LiveData as a read-only List<String> to the observers.
    val items: LiveData<List<MediaItem>> get() = _items.map { it.toList() }


    private lateinit var notificationManager: MediaNotificationManager
    protected lateinit var mediaSession: MediaSession
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var isStarted = false
    private var _CurrentT = MutableLiveData<String>()
    val CurrentT: LiveData<String> get() = _CurrentT
    private var _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> get() = _totalDuration
    private var _PS = MutableLiveData<Long>()
    val PS: LiveData<Long> get() = _PS
    private var _nam = MutableLiveData<String>()
    val nam: LiveData<String> get() = _nam
    private var _repeatMod = MutableLiveData<Int>()
    val repeatMod: LiveData<Int> get() = _repeatMod
    private var _shuffleMode = MutableLiveData<Boolean>()
    val shuffleMode: LiveData<Boolean> get() = _shuffleMode
    private val _categories = MutableLiveData<MutableList<String>>(mutableListOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val Categories: LiveData<List<String>> get() = _categories.map { it.toList() }
    private val _ListSongs = MutableLiveData<MutableMap<String, List<String>>>(mutableMapOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val ListSongs: LiveData<Map<String, List<String>>> get() = _ListSongs.map { it.toMap() }
    private val _totalTime = MutableLiveData<MutableMap<String, String>>(mutableMapOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val totalTime: LiveData<Map<String, String>> get() = _totalTime.map { it.toMap() }
    private val _ListSingerCata = MutableLiveData<MutableMap<String, List<String>>>(mutableMapOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val ListSingerCata: LiveData<Map<String, List<String>>> get() = _ListSingerCata.map { it.toMap() }
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _CatisLoading = MutableLiveData<Boolean>()
    val CatisLoading: LiveData<Boolean> get() = _CatisLoading
    private val _isAdd = MutableLiveData<Boolean>()
    val isAdd: LiveData<Boolean> get() = _isAdd
    private val _SongsisLoading = MutableLiveData<Boolean>()
    val SongsisLoading: LiveData<Boolean> get() = _SongsisLoading
    private var _valueSlider = MutableLiveData<Float>()
    val valueSlider: LiveData<Float> get() = _valueSlider
    private var _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime
    val listOfFavorite: LiveData<Map<String, String>> get() = _listOfFavorite.map { it.toMap() }
    private val _isFavoriteLoading = MutableLiveData<Boolean>()
    val isFavoriteLoading: LiveData<Boolean> get() = _isFavoriteLoading
    private val _albumDetails = MutableStateFlow<Map<String, Playlist?>>(emptyMap())
    val albumDetails: StateFlow<Map<String, Playlist?>> get() = _albumDetails
    private val _TrackDe = MutableLiveData<List<Track>>(mutableListOf<Track>())
    val TrackDe: LiveData<List<Track>> get() = _TrackDe.map { it.toList() }
    private val _Tracke = MutableLiveData<List<TrackDetails>>(mutableListOf<TrackDetails>())
    val Tracke: LiveData<List<TrackDetails>> get() = _Tracke.map { it.toList() }
    private val _TrackFavorite = MutableLiveData<List<TrackDetails>>(mutableListOf<TrackDetails>())
    val TrackFavorite: LiveData<List<TrackDetails>> get() = _TrackFavorite.map { it.toList() }

    private val _TrackSearch = MutableLiveData<List<Track>>(mutableListOf<Track>())
    val TrackSearch: LiveData<List<Track>> get() = _TrackSearch.map { it.toList() }
    var PlayLists = mutableListOf<String>(
        "47gSXaPgD5Ln7fs8YkPJEW",
        "519II5E4pZryeyySl2YLgn",
        "4lbTi1TTzjvYmOGh1x4IIx",
        "4Hzg3lPUjWjMvlKmXHBRL9",
        "2SM6rniZl84fEyMCB5KMQB",
        "0eGfpDb1TTvxIXdMk5DyyD",
        "6ZNoF0voOucfax96hu1lRm",
        "4zQDKb9McbC7T7yiJT6Oqy",
        "1AyxSpveGEJsBaGZubIKo8",
        "6fqaB0I1gHgNy4TaPphgro",
        "3GQvSbCBLWctClpYZXSYUG",
        "3mgNDhdzgwoAxlAI29VdzN"
    )

    fun Action(event: SongEvent, context: Context) {
        when (event) {
            is SongEvent.PLAY -> play()
            is SongEvent.PAUSE -> pause()
            is SongEvent.NEXT -> next()
            is SongEvent.PREV -> prev()
            is SongEvent.Favorit -> favorit(event.name, event.result, event.singer, context)
            is SongEvent.CheckFavoriteSong -> checkForFavorite(event.result)
            is SongEvent.ListSong -> listS(event.list, context)

        }
    }

    private fun listS(list: List<String>, context: Context) {

//        val currentList = _items.value ?: mutableListOf<MediaItem>()
//        for (index in 0 until list.size) {
//            val item = getSongs(list[index], context)
//            if (items.value?.contains(MediaItem.fromUri(item)) == false) {
//                currentList.add(index, MediaItem.fromUri(item))
//
//            }
//        }
//
//        _items.value = currentList

    }

    private var _tok = MutableLiveData<String>()
    val tok: LiveData<String> get() = _tok
    fun fetchTracksAsync() {
        viewModelScope.launch {
            val result = async(Dispatchers.IO) { Api().getAccessToken().getOrNull() }
            val token = result.await() ?: ""
            PlayLists.forEach { playlistId ->
                try {
                    val responseTracks =
                        SpotifyService.create().getPlaylistTracks("Bearer $token", playlistId)
                    val tracksResponse = responseTracks.body()
                    if (tracksResponse != null) {
                        val tracks =
                            tracksResponse.items.map { it.track }  // Get the list of tracks
                        // Make sure currentList is mutable
                        val currentList = _TrackSearch.value?.toMutableList() ?: mutableListOf()

                        // Check if each track is already in the list, and add if not
                        tracks.forEach { item ->
                            if (currentList.none { existingTrack -> existingTrack.id == item.id }) {
                                currentList.add(item)  // Add new track if not already in the list
                                Log.d("Hamza tRack", "Added new track: ${item.name}")
                            } else {
                                Log.e("Hamza tRack", "Track already exists: ${item.name}")
                            }
                        }
                        _TrackSearch.value = currentList
                        // Update the LiveData with the new list

                    } else {
                        Log.e("Hamza tRack", "Response body is null")
                    }
                } catch (e: Exception) {

                }

            }
        }
    }

    fun fetchPlaylistAsync() {
        viewModelScope.launch {
            // Use async to fetch playlists concurrently
            _isLoading.value = true
            val result = async(Dispatchers.IO) { Api().getAccessToken().getOrNull() }
            val token = result.await() ?: ""
            PlayLists.forEach { playlistId ->
                try {

                    val response = SpotifyService.create().getPlaylists("Bearer $token", playlistId)
                    if (response.isSuccessful) {
                        val playlist = response.body()
                        if (playlist != null) {
                            _albumDetails.update { currentMap ->
                                if (currentMap.containsKey(playlistId)) currentMap
                                else currentMap + (playlistId to playlist)

                            }


                            Log.e("Album", "Error fetching playlist: $playlist")
                        }

                    }


                } catch (e: Exception) {
                    Log.e("Album", "Error fetching playlist: $e")
                }

            }

            _isLoading.value = false
        }


        // Indicate loading finished
    }


    fun fetchPlaylistTracks(playlistId: String) {
        _SongsisLoading.value = true
        viewModelScope.launch {
            val result = Api().getAccessToken().getOrNull()  // Get the access token
            _tok.value = result ?: ""

            try {
                Log.e("Hamza tRack", "Access Token: ${result}")
                val response =
                    SpotifyService.create().getPlaylistTracks("Bearer $result", playlistId)
                Log.e("Hamza tRack", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val tracksResponse = response.body()
                    if (tracksResponse != null) {
                        val tracks =
                            tracksResponse.items.map { it.track }  // Get the list of tracks
                        // Make sure currentList is mutable
                        val currentList = _TrackDe.value?.toMutableList() ?: mutableListOf()

                        // Check if each track is already in the list, and add if not
                        tracks.forEach { item ->
                            if (currentList.none { existingTrack -> existingTrack.id == item.id }) {
                                currentList.add(item)  // Add new track if not already in the list
                                Log.d("Hamza tRack", "Added new track: ${item.name}")
                            } else {
                                Log.e("Hamza tRack", "Track already exists: ${item.name}")
                            }
                        }

                        // Update the LiveData with the new list
                        _TrackDe.value = currentList
                        _SongsisLoading.value = false
                    } else {
                        Log.e("Hamza tRack", "Response body is null")
                    }
                } else {
                    Log.e(
                        "Hamza tRack",
                        "Failed to fetch playlist tracks: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("Hamza tRack", "Error: $e")
                _SongsisLoading.value = false
            }
        }

    }

    fun fetchTrack(trackId: String) {
        viewModelScope.launch {
            val result = Api().getAccessToken().getOrNull()
            try {
                val response = SpotifyService.create().getTrackDetails("Bearer $result", trackId)
                if (response.isSuccessful) {
                    val tracksResponse = response.body()
                    if (tracksResponse != null) {

                        if (Tracke.value!!.contains(tracksResponse)) {
                            Log.e("HamtRack", "Track is existed${tracksResponse}")
                        } else {
                            _Tracke.value = listOf(tracksResponse)
                        }

                    } else {
                        Log.e("HamtRack", "Response body is null")
                    }
                } else {
                    Log.e(
                        "HamtRack",
                        "Failed to fetch playlist tracks: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("HamtRack", "Error: $e")
            }


        }
    }
    fun fetchFavoriteTrack(trackId: String) {
        _isFavoriteLoading.value = true
        viewModelScope.launch {
            val result = Api().getAccessToken().getOrNull()
            try {
                val response = SpotifyService.create().getTrackDetails("Bearer $result", trackId)
                if (response.isSuccessful) {
                    val tracksResponse = response.body()
                    if (tracksResponse != null) {

                        if (TrackFavorite.value!!.contains(tracksResponse)) {
                            Log.e("HamtRack", "Track is existed${tracksResponse}")
                        } else {
                            _TrackFavorite.value = listOf(tracksResponse)
                        }

                    } else {
                        Log.e("HamtRack", "Response body is null")
                    }
                    _isFavoriteLoading.value = false
                } else {
                    Log.e(
                        "HamtRack",
                        "Failed to fetch playlist tracks: ${response.code()} - ${response.message()}"
                    )
                    _isFavoriteLoading.value = false
                }

            } catch (e: Exception) {
                Log.e("HamtRack", "Error: $e")
                _isFavoriteLoading.value = false
            }


        }
    }

    fun clearTracks() {
        _TrackDe.value = emptyList() // Clears the current list of tracks
    }

    private fun play() {
        exoPlayer.play()
        // Update the state to reflect the current playback state
        _isPlaying.value = exoPlayer.isPlaying


    }

    private fun pause() {
        exoPlayer.pause()
        // Update the state to reflect the current playback state
        _isPlaying.value = exoPlayer.isPlaying

    }

    private fun next() {
        if (exoPlayer.currentMediaItemIndex < exoPlayer.mediaItemCount - 1) {
            exoPlayer.seekToNext()
        } else {
            exoPlayer.seekTo(0, 0) // Loop to the first item
        }

    }

    private fun prev() {
        if (exoPlayer.currentMediaItemIndex > 0) {
            exoPlayer.seekToPrevious()
        } else {
            exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0) // Loop to the last item
        }

    }

    private val accessToken = MutableStateFlow("")


    fun preparePlayer(
        context: Context,
        songList: List<MediaItem>,
        userData: UserData?,
        list: List<String>,
        o: Int,
    ) {
        exoPlayer.setMediaItems(songList)
        exoPlayer.seekToDefaultPosition(o)
        exoPlayer.prepare()
        val playerListener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _CurrentT.value = TotalTime(exoPlayer.duration)
                        _totalDuration.value = exoPlayer.duration
                        _PS.value = exoPlayer.currentPosition


                        _nam.value = list[exoPlayer.currentMediaItemIndex].toString()
                        if (userData != null) {
                            check(list[exoPlayer.currentMediaItemIndex], userData)
                        }

                        notificationManager.showNotificationForPlayer(exoPlayer)
                    }

                    Player.STATE_ENDED -> {
                        // Handle end of playback, if needed
                        exoPlayer.seekToNext()
                    }

                    else -> {
                        notificationManager.hideNotification()
                    }


                }

            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMod.value = repeatMode
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleMode.value = shuffleModeEnabled
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("Error", "onIsPlayingChanged: ${isPlaying}")
                super.onIsPlayingChanged(isPlaying)
                _isPlaying.value = isPlaying
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e("Error", "Error: ${error.message}")
            }
        }
        exoPlayer.addListener(playerListener)

        onStart(context, list[exoPlayer.currentMediaItemIndex].toString())


    }


    fun onStart(context: Context, name: String) {
        if (isStarted) return

        isStarted = true

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            context.packageManager?.getLaunchIntentForPackage(context.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        context,
                        SESSION_INTENT_REQUEST_CODE,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

        // Create a new MediaSession.
        mediaSession = MediaSession.Builder(context, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent!!).build()

        notificationManager = MediaNotificationManager(
            context,
            mediaSession.token,
            exoPlayer,
            PlayerNotificationListener(),
            name
        )


        notificationManager.showNotificationForPlayer(exoPlayer)
    }


    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean,
        ) {

        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

        }
    }

    fun onSignInResult(result: SignResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                SignInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update {
            SignInState()
        }
    }





    private fun favorit(
        TrackId: String,
        result: UserData,
        Artist: String,
        context: Context,
    ) {
        val info = mapOf(
            "track_id" to TrackId,
            "artist" to Artist,
        )
        if (result.userId != null && _isAdd.value != true) {
            Firebase.firestore.collection("FavoriteSongs")
                .document(result.userId.toString())
                .set(info)
                .addOnSuccessListener {
                    _isAdd.value = true
                    Log.d(
                        "Add favorit songs",
                        "favorit: it's add ${result.userId} ${_isAdd.value}",
                    )
                }
                .addOnFailureListener {
                    _isAdd.value = false
                    Log.e("Add favorit songs", "favorit: it's not add ")
                }


        } else {
            if (result.userId != null && _isAdd.value == true) {
                Firebase.firestore.collection("FavoriteSongs")
                    .document(result.userId.toString())
                    .get()
                    .addOnSuccessListener { result ->

                        if (result.get("track_id") == TrackId) {
                            //i want to update this result.get("F_song") or delete it
                            Firebase.firestore.collection("FavoriteSongs")
                                .document(result.id)
                                .update(
                                    mapOf(
                                        "track_id" to FieldValue.delete(),
                                        "artist" to FieldValue.delete(),
                                    )
                                )
                                .addOnSuccessListener {
                                    _isAdd.value = false
                                }
                                .addOnFailureListener {
                                    _isAdd.value = true
                                }

                        }

                        _isAdd.value = false
                        Log.d("Add favorit songs", "favorit: it's delete ")
                    }
                    .addOnFailureListener {
                        _isAdd.value = true
                        Log.e("Add favorit songs", "favorit: it's not delete ")
                    }

            } else {
                return Toast.makeText(context, "Please SignIn", Toast.LENGTH_LONG).show()
            }


        }


    }

    fun check(
        nameSong: String,
        result: UserData,
    ) {

        Log.e("Add favorit songs", "check: ${result.userId}")
        Firebase.firestore.collection("FavoriteSongs")
            .document(result.userId.toString())
            .get()
            .addOnSuccessListener { resul ->
                _isAdd.value = resul.get("track_id").toString() == nameSong

                Log.e("Add favorit songs", "check: ${result.userId} ${resul.get("track_id")}")
            }


    }


    private val _listOfFavorite = MutableLiveData<MutableMap<String, String>>(mutableMapOf())


    private fun checkForFavorite(data: UserData) {
        if (data.userId != null) {

            Firebase.firestore.collection("FavoriteSongs")
                .get()
                .addOnSuccessListener { docs ->
                    if (docs != null) {
                        var list = _listOfFavorite.value ?: mutableMapOf<String, String>()
                        for (doc in docs) {
                            val singer = doc.get("artist").toString()
                            val songName = doc.get("track_id").toString()


                            if (doc.id.toString() == data.userId.toString()) {
                                Log.d(
                                    "test list VM",
                                    "${doc.get("artist").toString()} ${
                                        doc.get("track_id").toString()
                                    }"
                                )
                                if (listOfFavorite.value?.get(singer)?.contains(songName) != true) {
                                    list[doc.get("artist").toString()] =
                                        doc.get("track_id").toString()
                                    Log.d(
                                        "test list VM",
                                        "${doc.get("artist").toString()} ${
                                            doc.get("track_id").toString()
                                        } ${listOfFavorite.value?.get(singer)?.contains(songName)}"
                                    )
                                }
                            }
                        }
                        _listOfFavorite.value = list

                    }
                }
                .addOnFailureListener {

                }
        }
    }


    fun slider(isPlaying: Boolean, exoPlayer: ExoPlayer) {
        viewModelScope.launch {
            while (isPlaying) {
                _valueSlider.value = exoPlayer.currentPosition.toFloat()
                _currentTime.value = TotalTime(exoPlayer.currentPosition)
                delay(1000L) // Update every second

            }

        }

    }


}


private fun TTime(lon: Long): String {
    val sec = lon / 1000
    val min = sec / 60
    val seconds = sec % 60
    val minutesString = if (min < 10) {
        "0$min"
    } else {
        min.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}

class SongViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(songVM::class.java)) {
            songVM(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// I need current index of song


//    @SuppressLint("SuspiciousIndentation")
//    fun getSongs(
//        publicId: String,
//        context: Context
//    ): Uri {
//        val config = mutableMapOf<String, Any>()
//        config["cloud_name"] = "dayyltanu"
//        config["api_key"] = "275544193634372"
//        config["api_secret"] = "3qyjZKD3o43_PQCIr9xoM91Lgs0"
//        val cloudinary = Cloudinary(config)
//        val signedUrl = cloudinary.url()
//            .secure(true)  // Ensure the URL is HTTPS
//            .signed(true) // Private song
//            .resourcType("video")
//            .generate(publicId)
//            .toUri()
//
//
//        // Return the signed URL for the image
//        return signedUrl
//    }
//
//    @SuppressLint("SuspiciousIndentation")
//    fun getImage(
//        publicId: String,
//        context: Context
//    ): String? {
//        val config = mutableMapOf<String, Any>()
//        config["cloud_name"] = "dayyltanu"
//        config["api_key"] = "275544193634372"
//        config["api_secret"] = "3qyjZKD3o43_PQCIr9xoM91Lgs0"
//
//        val cloudinary = Cloudinary(config)
//        val signedUrl = cloudinary.url()
//            .secure(true)  // Ensure the URL is HTTPS
//            .signed(true) // Private image
//            .generate(publicId)
//
//
//        // Return the signed URL for the image
//        return signedUrl
//    }

//    fun getname() {
//        _isLoading.value = true
//        FirebaseFirestore.getInstance().collection("Songs")
//            .get()
//            .addOnSuccessListener { rsult ->
//                rsult.documents.size
//                for (d in rsult) {
//
//                    if (d != null) {
//                        var doc = d.reference.collection("Categories")
//                        doc.get()
//                            .addOnSuccessListener { rsult ->
//                                val currentList = _categories.value ?: mutableListOf()
//                                for (doc in rsult) {
//                                    var name_type = doc.get("name_type")
//                                    if (name_type != null && name_type != 0) {
//                                        Log.d("name_type", "getname: ${_categories.value}")
//
//                                        for (i in 0 until Categories.value!!.size) {
//                                            if (Categories.value?.get(i) == name_type) {
//                                                name_type = ""
//                                                break
//                                            }
//                                        }
//                                        if (name_type != "") {
//                                            currentList.add(name_type.toString())
//                                        }
//
//                                    }
//                                }
//
//                                _categories.value = currentList
//                                _isLoading.value = false
//                            }
//                            .addOnFailureListener {
//                                _isLoading.value = false
//                            }
//                    }
//                }
//
//                Log.d("name_type", "getname: ${_categories.value?.size}")
//
//            }
//            .addOnFailureListener {
//                _isLoading.value = false
//            }
//
//    }
//@SuppressLint("SuspiciousIndentation")
//    fun listSongs(
//        cat: String,
//        Singer: String
//    ) {
//        _SongsisLoading.value = true
//        FirebaseFirestore.getInstance().collection("Songs")
//            .get()
//            .addOnSuccessListener { rsult ->
//                rsult.documents.size
//
//                for (d in rsult) {
//                    if (d != null) {
//                        var doc = d.reference.collection("Categories")
//                        doc.get()
//                            .addOnSuccessListener { rsult ->
//                                for (doc in rsult) {
//                                    val pop = doc.reference.collection(cat)
//                                    pop.get()
//                                        .addOnSuccessListener { res ->
//
//                                            for (doc in res) {
//                                                val singer = doc.get("name_singer")
//                                                Log.e("songs", "Singer: ${singer}+${Singer}")
//                                                if (singer.toString() == Singer) {
//                                                    val sing = doc.reference.collection(Singer)
//                                                    Log.e("songs", "Singer: ${sing}")
//                                                    sing.get()
//                                                        .addOnSuccessListener { docs ->
//                                                            val currentList = _ListSongs.value
//                                                                ?: mutableMapOf<String, List<String>>()
//                                                            val list =
//                                                                currentList[Singer]?.toMutableList()
//                                                                    ?: mutableListOf()
//
//                                                            Log.e(
//                                                                "songs",
//                                                                "Singer: ${docs.documents.size}"
//                                                            )
//                                                            for (doc in docs) {
//                                                                Log.e("songs", "Singer: $doc")
//                                                                var song = doc.get("song_name")
//
//                                                                if (!ListSongs.value?.get(Singer)
//                                                                        .isNullOrEmpty()
//                                                                ) {
//                                                                    val existingList =
//                                                                        ListSongs.value!![Singer]
//                                                                            ?: emptyList()
//                                                                    if (song in existingList) {
//                                                                        song = ""
//                                                                    }
//
//                                                                }
//                                                                if (song.toString().isNotEmpty()) {
//                                                                    list.add(song.toString())
//                                                                    currentList[Singer] = list
//                                                                }
//
//                                                            }
//                                                            _ListSongs.value = currentList
//                                                            _SongsisLoading.value = false
//
//
//                                                        }.addOnFailureListener {
//                                                            _SongsisLoading.value = false
//                                                        }
//                                                }
//
//
//                                            }
//
//                                        }
//                                }
//
//
//                            }
//                            .addOnFailureListener { e ->
//                                _SongsisLoading.value = false
//                            }
//                    }
//                }
//            }
//            .addOnFailureListener {
//                _SongsisLoading.value = false
//            }
//    }


//    suspend fun T_Time(
//        navController: NavController,
//        name: String
//    ) {
//        var exoPlayer = ExoPlayer.Builder(navController.context).build()
//
//        var uriSong = getSongs(name, navController.context)
//        var mediaItem = MediaItem.fromUri(uriSong)
//        exoPlayer.setMediaItem(mediaItem)
//        exoPlayer.prepare()
//        exoPlayer.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(playbackState: Int) {
//                if (playbackState == Player.STATE_READY) {
//                    // Retrieve and format the total time
//                    var formattedTime = TTime(exoPlayer.duration)
//                    val currentList = _totalTime.value ?: mutableMapOf<String, String>()
//
//
//                    if (totalTime.value?.get(name)?.contains(formattedTime) == true) {
//                        formattedTime = ""
//
//                    } else {
//                        Log.e("Total-Time", "${formattedTime} ${name}")
//                        currentList[name] = formattedTime
//                    }
//                    if (formattedTime != "") {
//                        _totalTime.value = currentList // Add the time to the list
//                    }
//                    exoPlayer.release()
//                }
//            }
//        })
//
//
//    }
//suspend fun pop(
//    test: String,
//) {
//    _CatisLoading.value = true
//    FirebaseFirestore.getInstance().collection("Songs")
//        .get()
//        .addOnSuccessListener { rsult ->
//            rsult.documents.size
//            for (d in rsult) {
//
//                if (d != null) {
//                    var doc = d.reference.collection("Categories")
//                    doc.get()
//                        .addOnSuccessListener { rsult ->
//                            for (doc in rsult) {
//                                var pop = doc.reference.collection(test)
//                                Log.d("pop1", "pop:${test} ")
//                                pop.get()
//                                    .addOnSuccessListener { reslt ->
//                                        val currentList = _ListSingerCata.value
//                                            ?: mutableMapOf<String, List<String>>()
//                                        val list = currentList[test]?.toMutableList()
//                                            ?: mutableListOf()
//                                        if (reslt.documents.isNotEmpty()) {
//                                            for (doc in reslt) {
//                                                var name_singer = doc.get("name_singer")
//                                                if (name_singer != null) {
//                                                    Log.d(
//                                                        "pop1",
//                                                        "getname: ${name_singer} + ${test}"
//                                                    )
//
//                                                    if (!ListSingerCata.value.isNullOrEmpty() && !ListSingerCata.value!![test].isNullOrEmpty()) {
//                                                        val existingList =
//                                                            ListSingerCata.value!![test]
//                                                                ?: emptyList()
//                                                        if (name_singer in existingList) {
//                                                            name_singer = ""
//                                                        }
//                                                    } else {
//                                                        Log.e(
//                                                            "empty",
//                                                            "pop: ListSingerCata or test list is empty"
//                                                        )
//                                                    }
//
//                                                    if (name_singer != "") {
//                                                        list.add(name_singer.toString())
//                                                        currentList[test] = list
//
//                                                    }
//                                                }
//                                            }
//
//
//                                        }
//                                        _ListSingerCata.value = currentList
//                                        _CatisLoading.value = false
//
//                                    }
//                            }
//
//
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("poperror", "pop: $e")
//
//                        }
//                }
//            }
//
//            Log.d("name_type", "getname: s")
//
//        }
//        .addOnFailureListener {
//            _CatisLoading.value = false
//        }.await()
//
//}