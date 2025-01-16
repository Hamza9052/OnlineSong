package online.song.onlinesong

import okhttp3.Request
import com.cloudinary.android.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.json.JSONObject
import android.util.Base64

class Api {
    private val apiUrl = "https://accounts.spotify.com/api/token"

    // Function to fetch access token from Spotify using client credentials
    suspend fun getAccessToken(): Result<String> {
        val clientId = R.string.SPOTIFY_CLIENT_ID
        val clientSecret = R.string.SPOTIFY_CLIENT_SECRET
        

        // Prepare authorization header
        val authHeader = "Basic " + Base64.encodeToString(
            "$clientId:$clientSecret".toByteArray(),
            Base64.NO_WRAP
        )

        // Prepare request parameters
        val params = HashMap<String, String>().apply {
            put("grant_type", "client_credentials")
        }

        // Make the API call to get the access token
        return try {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(apiUrl)
                    .post(FormBody.Builder().apply {
                        params.forEach { (key, value) -> add(key, value) }
                    }.build())
                    .addHeader("Authorization", authHeader)
                    .build()

                val client = OkHttpClient()
                val call = client.newCall(request)
                val response = call.execute()

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                    val accessToken = jsonResponse.optString("access_token", null)

                    if (accessToken != null) {
                        Result.success(accessToken)
                    } else {
                        Result.failure(Exception("Access token not found"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch access token: ${response.message}"))
                }
            }
            response
        } catch (e: Exception) {
            Result.failure(e) // Return failure result on exception
        }
    }
}