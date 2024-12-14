package online.song.onlinesong.LoginWithGoogle

data class SignResult(
    val data:UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val ProfilePicUri: String?
)
