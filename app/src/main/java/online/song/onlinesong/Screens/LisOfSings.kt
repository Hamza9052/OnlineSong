package online.song.onlinesong.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM

@Composable
fun listOfSongs(
    navController: NavController,
    name:String?,
    viewModel: songVM
){

    val image = rememberAsyncImagePainter(
        model = ImageRequest.Builder(navController.context)
            .data(viewModel.getImage(name!!,navController.context))
            .crossfade(true)
            .error(R.drawable.error)
            .placeholder(R.drawable.error)
            .build()
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = image,
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.inverseOnSurface
                            )
                        )
                    )
                    .padding(top = 25.dp, bottom = 6.dp)
                    .padding(horizontal = 16.dp),
                text = name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
            )
        }
        VerticalDivider(modifier = Modifier.height(20.dp))
    }




}