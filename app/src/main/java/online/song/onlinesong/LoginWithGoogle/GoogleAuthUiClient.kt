package online.song.onlinesong.LoginWithGoogle

import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import online.song.onlinesong.R

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    suspend fun signIn(): IntentSender?{
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).addOnSuccessListener{
                Toast.makeText(context,"Login Success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context,"Login Failed", Toast.LENGTH_SHORT).show()
            }.await()

        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun SignInWithIntent(intent:Intent):SignResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignResult(
                data =user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName,
                        ProfilePicUri = photoUrl.toString()
                    )
                },
                errorMessage = null
            )

        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signout(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

     fun getSignedInUser(): UserData? = auth.currentUser?.run{
         UserData(
             userId =uid ,
             userName = displayName,
             ProfilePicUri = photoUrl?.toString()
         )
     }

    private fun buildSignInRequest(): BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.Web_client_ID))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}