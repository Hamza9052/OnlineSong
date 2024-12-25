package online.song.onlinesong.Screens

sealed class Screen(val route:String) {
    object Favorite:Screen("favorite")
    object Home:Screen("home")
    object Search:Screen("search")
    object Test: Screen("test")
}