package online.song.onlinesong.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import online.song.onlinesong.R


@Composable
fun Favorite(
    navController: NavController
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ){  }
}