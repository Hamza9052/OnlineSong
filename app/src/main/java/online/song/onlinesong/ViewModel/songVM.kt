package online.song.onlinesong.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.cloudinary.Cloudinary
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import online.song.onlinesong.Events.SongEvent
import online.song.onlinesong.LoginWithGoogle.SignInState
import online.song.onlinesong.LoginWithGoogle.SignResult
import online.song.onlinesong.LoginWithGoogle.UserData
import kotlin.collections.contains
import kotlin.math.log
import kotlin.toString

class songVM() : ViewModel() {


    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun Action(event: SongEvent, context: Context) {
        when (event) {
            is SongEvent.Play -> play(event.list, context)
            is SongEvent.Pause -> pause(event.list, context)
            is SongEvent.Stop -> stop(event.list, context)
            is SongEvent.Next -> next(event.list, context)
            is SongEvent.Prev -> prev(event.list, context)
            is SongEvent.Favorit -> favorit(event.name,event.result,event.singer,context)
            is SongEvent.checkFavoriteSong  -> checkForFavorite(event.result)
        }
    }




    private fun play(list: List<String>, context: Context) {

    }

    private fun pause(list: List<String>, context: Context) {

    }

    private fun stop(list: List<String>, context: Context) {

    }

    private fun next(list: List<String>, context: Context) {

    }

    private fun prev(list: List<String>, context: Context) {

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



    private val _Categories = MutableLiveData<MutableList<String>>(mutableListOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val Categories: LiveData<List<String>> get() = _Categories.map { it.toList() }

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

    @SuppressLint("SuspiciousIndentation")
    fun getSongs(publicId: String, context: Context): Uri {
        val config = mutableMapOf<String, Any>()
        config["cloud_name"] = "dayyltanu"
        config["api_key"] = "275544193634372"
        config["api_secret"] = "3qyjZKD3o43_PQCIr9xoM91Lgs0"
        val cloudinary = Cloudinary(config)
        val signedUrl = cloudinary.url()
            .secure(true)  // Ensure the URL is HTTPS
            .signed(true) // Private song
            .resourcType("video")
            .generate(publicId)
            .toUri()


        // Return the signed URL for the image
        return signedUrl
    }

    @SuppressLint("SuspiciousIndentation")
    fun getImage(publicId: String, context: Context): String? {
        val config = mutableMapOf<String, Any>()
        config["cloud_name"] = "dayyltanu"
        config["api_key"] = "275544193634372"
        config["api_secret"] = "3qyjZKD3o43_PQCIr9xoM91Lgs0"

        val cloudinary = Cloudinary(config)
        val signedUrl = cloudinary.url()
            .secure(true)  // Ensure the URL is HTTPS
            .signed(true) // Private image
            .generate(publicId)


        // Return the signed URL for the image
        return signedUrl
    }

    fun getname() {
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("Songs")
            .get()
            .addOnSuccessListener { rsult ->
                rsult.documents.size
                for (d in rsult) {

                    if (d != null) {
                        var doc = d.reference.collection("Categories")
                        doc.get()
                            .addOnSuccessListener { rsult ->
                                val currentList = _Categories.value ?: mutableListOf()
                                for (doc in rsult) {
                                    var name_type = doc.get("name_type")
                                    if (name_type != null && name_type != 0) {
                                        Log.d("name_type", "getname: ${_Categories.value}")

                                        for (i in 0 until Categories.value!!.size) {
                                            if (Categories.value?.get(i) == name_type) {
                                                name_type = ""
                                                break
                                            }
                                        }
                                        if (name_type != "") {
                                            currentList.add(name_type.toString())
                                        }

                                    }
                                }

                                _Categories.value = currentList
                                _isLoading.value = false
                            }
                            .addOnFailureListener {
                                _isLoading.value = false
                            }
                    }
                }

                Log.d("name_type", "getname: ${_Categories.value?.size}")

            }
            .addOnFailureListener {
                _isLoading.value = false
            }

    }

    suspend fun pop(test: String) {
        _CatisLoading.value = true
        FirebaseFirestore.getInstance().collection("Songs")
            .get()
            .addOnSuccessListener { rsult ->
                rsult.documents.size
                for (d in rsult) {

                    if (d != null) {
                        var doc = d.reference.collection("Categories")
                        doc.get()
                            .addOnSuccessListener { rsult ->
                                for (doc in rsult) {
                                    var pop = doc.reference.collection(test)
                                    Log.d("pop1", "pop:${test} ")
                                    pop.get()
                                        .addOnSuccessListener { reslt ->
                                            val currentList = _ListSingerCata.value
                                                ?: mutableMapOf<String, List<String>>()
                                            val list = currentList[test]?.toMutableList()
                                                ?: mutableListOf()
                                            if (reslt.documents.isNotEmpty()) {
                                                for (doc in reslt) {
                                                    var name_singer = doc.get("name_singer")
                                                    if (name_singer != null) {
                                                        Log.d(
                                                            "pop1",
                                                            "getname: ${name_singer} + ${test}"
                                                        )

                                                        if (!ListSingerCata.value.isNullOrEmpty() && !ListSingerCata.value!![test].isNullOrEmpty()) {
                                                            val existingList =
                                                                ListSingerCata.value!![test]
                                                                    ?: emptyList()
                                                            if (name_singer in existingList) {
                                                                name_singer = ""
                                                            }
                                                        } else {
                                                            Log.e(
                                                                "empty",
                                                                "pop: ListSingerCata or test list is empty"
                                                            )
                                                        }

                                                        if (name_singer != "") {
                                                            list.add(name_singer.toString())
                                                            currentList[test] = list

                                                        }
                                                    }
                                                }


                                            }
                                            _ListSingerCata.value = currentList
                                            _CatisLoading.value = false

                                        }
                                }


                            }
                            .addOnFailureListener { e ->
                                Log.e("poperror", "pop: $e")

                            }
                    }
                }

                Log.d("name_type", "getname: s")

            }
            .addOnFailureListener {
                _CatisLoading.value = false
            }.await()

    }


    private val _SongsisLoading = MutableLiveData<Boolean>()
    val SongsisLoading: LiveData<Boolean> get() = _SongsisLoading

    @SuppressLint("SuspiciousIndentation")
    fun listSongs(cat: String, Singer: String) {
        _SongsisLoading.value = true
        FirebaseFirestore.getInstance().collection("Songs")
            .get()
            .addOnSuccessListener { rsult ->
                rsult.documents.size

                for (d in rsult) {
                    if (d != null) {
                        var doc = d.reference.collection("Categories")
                        doc.get()
                            .addOnSuccessListener { rsult ->
                                for (doc in rsult) {
                                    val pop = doc.reference.collection(cat)
                                    pop.get()
                                        .addOnSuccessListener { res ->

                                            for (doc in res) {
                                                val singer = doc.get("name_singer")
                                                Log.e("songs", "Singer: ${singer}+${Singer}")
                                                if (singer.toString() == Singer) {
                                                    val sing = doc.reference.collection(Singer)
                                                    Log.e("songs", "Singer: ${sing}")
                                                    sing.get()
                                                        .addOnSuccessListener { docs ->
                                                            val currentList = _ListSongs.value
                                                                ?: mutableMapOf<String, List<String>>()
                                                            val list =
                                                                currentList[Singer]?.toMutableList()
                                                                    ?: mutableListOf()

                                                            Log.e(
                                                                "songs",
                                                                "Singer: ${docs.documents.size}"
                                                            )
                                                            for (doc in docs) {
                                                                Log.e("songs", "Singer: $doc")
                                                                var song = doc.get("song_name")

                                                                if (!ListSongs.value?.get(Singer)
                                                                        .isNullOrEmpty()
                                                                ) {
                                                                    val existingList =
                                                                        ListSongs.value!![Singer]
                                                                            ?: emptyList()
                                                                    if (song in existingList) {
                                                                        song = ""
                                                                    }

                                                                }
                                                                if (song.toString().isNotEmpty()) {
                                                                    list.add(song.toString())
                                                                    currentList[Singer] = list
                                                                }

                                                            }
                                                            _ListSongs.value = currentList
                                                            _SongsisLoading.value = false


                                                        }.addOnFailureListener {
                                                            _SongsisLoading.value = false
                                                        }
                                                }


                                            }

                                        }
                                }


                            }
                            .addOnFailureListener { e ->
                                _SongsisLoading.value = false
                            }
                    }
                }
            }
            .addOnFailureListener {
                _SongsisLoading.value = false
            }
    }

    suspend fun T_Time(navController: NavController, name: String) {
        var exoPlayer = ExoPlayer.Builder(navController.context).build()

        var uriSong = getSongs(name, navController.context)
        var mediaItem = MediaItem.fromUri(uriSong)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    // Retrieve and format the total time
                    var formattedTime = TTime(exoPlayer.duration)
                    val currentList = _totalTime.value ?: mutableMapOf<String, String>()


                    if (totalTime.value?.get(name)?.contains(formattedTime) == true) {
                        formattedTime = ""

                    } else {
                        Log.e("Total-Time", "${formattedTime} ${name}")
                        currentList[name] = formattedTime
                    }
                    if (formattedTime != "") {
                        _totalTime.value = currentList // Add the time to the list
                    }
                    exoPlayer.release()
                }
            }
        })


    }



    private val _isAdd = MutableLiveData<Boolean>()
    val isAdd: LiveData<Boolean> get() = _isAdd
    private fun favorit(
        nameSong: String,
        result: UserData,
        nameSinger:String,
        context: Context,
        ) {
        val info = mapOf(
            "F_song" to nameSong,
            "Singer" to nameSinger,
        )
        if (result.userId != null && _isAdd.value != true){
            Firebase.firestore.collection("FavoriteSongs")
                .document(result.userId.toString())
                .set(info)
                .addOnSuccessListener{
                    _isAdd.value = true
                    Log.d("Add favorit songs", "favorit: it's add ${result.userId} ${_isAdd.value}", )
                }
                .addOnFailureListener{
                    _isAdd.value = false
                    Log.e("Add favorit songs", "favorit: it's not add ", )
                }


        }else{
            if (result.userId != null && _isAdd.value == true) {
                Firebase.firestore.collection("FavoriteSongs")
                    .document(result.userId.toString())
                    .get()
                    .addOnSuccessListener { result ->

                        if (result.get("F_song") == nameSong) {
                            //i want to update this result.get("F_song") or delete it
                            Firebase.firestore.collection("FavoriteSongs")
                                .document(result.id)
                                .update(
                                    mapOf(
                                        "F_song" to FieldValue.delete(),
                                        "Singer" to FieldValue.delete(),
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

            }else{
               return  Toast.makeText(context,"Please SignIn", Toast.LENGTH_LONG).show()
           }




        }


    }

     fun check(
        nameSong: String,
        result: UserData,
    ){

         Log.e("Add favorit songs", "check: ${result.userId}",)
        Firebase.firestore.collection("FavoriteSongs")
            .document(result.userId.toString())
            .get()
            .addOnSuccessListener{resul->


                        _isAdd.value = resul.get("F_song").toString() == nameSong

                    Log.e("Add favorit songs", "check: ${result.userId} ${resul.get("F_song")}",)
                }


            }


    private val _listOfFavorite = MutableLiveData<MutableMap<String, String>>(mutableMapOf())


    val listOfFavorite: LiveData<Map<String,String>> get() = _listOfFavorite.map { it.toMap() }
    private val _isFavoriteLoading = MutableLiveData<Boolean>()
    val isFavoriteLoading: LiveData<Boolean> get() = _isFavoriteLoading
    private fun checkForFavorite(data: UserData) {
        if (data.userId != null){
            _isFavoriteLoading.value = true
            Firebase.firestore.collection("FavoriteSongs")
                .get()
                .addOnSuccessListener{docs->
                    if (docs != null){
                        var list = _listOfFavorite.value ?: mutableMapOf<String,String>()
                        for (doc in docs){
                            val singer = doc.get("Singer").toString()
                            val songName =doc.get("F_song").toString()


                            if (doc.id.toString() == data.userId.toString()){
                                Log.d("test list VM","${doc.get("Singer").toString()} ${doc.get("F_song").toString()}")
                                if (listOfFavorite.value?.get(singer)?.contains(songName) != true ) {
                                    list[doc.get("Singer").toString()] = doc.get("F_song").toString()
                                    Log.d("test list VM","${doc.get("Singer").toString()} ${doc.get("F_song").toString()} ${listOfFavorite.value?.get(singer)?.contains(songName)}")
                                }
                            }
                        }
                        _listOfFavorite.value = list
                        _isFavoriteLoading.value = false
                    }
                }
                .addOnFailureListener{
                    _isFavoriteLoading.value = false
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



// I need current index of song



