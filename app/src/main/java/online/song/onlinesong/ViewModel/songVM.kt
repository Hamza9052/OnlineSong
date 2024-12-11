package online.song.onlinesong.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class songVM(): ViewModel() {





    private val _Categories = MutableLiveData<MutableList<String>>(mutableListOf())

    // Expose the LiveData as a read-only List<String> to the observers.
    val Categories: LiveData<List<String>> get() = _Categories.map { it.toList() }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

        @SuppressLint("SuspiciousIndentation")
    fun getSongs(publicId: String,context: Context): String? {
        val config = mutableMapOf<String, Any>()
        config["cloud_name"] = "dayyltanu"
        config["api_key"] = "275544193634372"
        config["api_secret"] = "3qyjZKD3o43_PQCIr9xoM91Lgs0"
        MediaManager.init(context, config)
        val cloudinary = Cloudinary(config)
        val signedUrl = cloudinary.url()
            .secure(true)  // Ensure the URL is HTTPS
            .signed(true) // Private song
            .resourcType("video")
            .generate(publicId)


        // Return the signed URL for the image
        return signedUrl
    }

    @SuppressLint("SuspiciousIndentation")
    fun getImage(publicId: String,context: Context): String? {
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

    fun getname(){
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("Songs")
            .get()
            .addOnSuccessListener{rsult ->
             rsult.documents.size
                for (d in rsult){

                    if (d != null ){
                        var doc = d.reference.collection("Categories")
                        doc.get()
                            .addOnSuccessListener{rsult ->
                                val currentList = _Categories.value ?: mutableListOf()
                                for (doc in rsult){
                                    var name_type = doc.get("name_type")
                                    if (name_type != null && name_type != 0){
                                        Log.d("name_type", "getname: ${_Categories.value}")

                                        for (i in 0 until Categories.value!!.size){
                                            if (Categories.value?.get(i) == name_type){
                                               name_type = ""
                                                break
                                            }
                                        }
                                        if (name_type != ""){
                                            currentList.add(name_type.toString())
                                        }

                                    }
                                }

                                _Categories.value = currentList
                                _isLoading.value = false
                            }
                            .addOnFailureListener{
                                _isLoading.value = false
                            }
                    }
                }

                Log.d("name_type", "getname: ${_Categories.value?.size}")

            }
            .addOnFailureListener{
                _isLoading.value = false
            }

    }



}