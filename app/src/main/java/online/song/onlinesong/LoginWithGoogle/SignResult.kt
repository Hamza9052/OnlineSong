package online.song.onlinesong.LoginWithGoogle

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SignResult(
    val data:UserData?,
    val errorMessage: String?
)
@Parcelize
data class UserData(
    val userId: String?,
    val userName: String?,
    val ProfilePicUri: String?
) : Parcelable
