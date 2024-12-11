package online.song.onlinesong.Screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import online.song.onlinesong.Events.dataSong
import online.song.onlinesong.R
import online.song.onlinesong.ViewModel.songVM

@Composable
fun Search(
    navController: NavController,
    VM: songVM
){
    val active by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = colorResource(R.color.background))
    ){
        Box(){
            Searchbar(active,VM,navController)
        }


    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Searchbar(
    active: Boolean,
    viewModel: songVM,
    navController: NavController,
//    click:()->Unit
) {
    val data = dataSong()
    var actives by remember { mutableStateOf(active) }
    var search by remember { mutableStateOf(data.search) }

    // Animated padding and visibility
    val animatedPaddingH by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500
            , easing = FastOutSlowInEasing) // Smooth easing
    )
    val animatedPaddingV by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing) // Smooth easing
    )


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = animatedPaddingH,
                vertical = animatedPaddingV
            )
            .background(
                color = Color.Transparent,
                RoundedCornerShape(15.dp)
            )
    ) {

        SearchBar(
            query = search,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (actives) 0.dp else 14.dp , start = if (actives) 0.dp else 14.dp)
                .background(
                    color = Color.Transparent,
                    RoundedCornerShape(15.dp)
                ),
            colors = SearchBarDefaults.colors(
                containerColor = colorResource(R.color.test),
                dividerColor = colorResource(R.color.icon),
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.White,
                )
            ),
            onQueryChange = { search = it },
            onSearch = {},
            active = actives,
            onActiveChange = { actives = it },
            placeholder = {
                Text(
                    text = "Search",
                    color = colorResource(R.color.unfocus),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                if (actives) {
                    AnimatedVisibility(
                        visible = actives,
                        exit = fadeOut(animationSpec = tween(50)) + slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        ),
                        enter = fadeIn(animationSpec = tween(50)) + slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        )
                    ) {
                        IconButton(
                            onClick = { actives = false }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Close",
                                modifier = Modifier.size(20.dp),
                                tint = colorResource(R.color.unfocus)
                            )
                        }
                    }
                }
                else{
                    search = ""
                }
            },
            trailingIcon = {
                if (!actives){
                    AnimatedVisibility(
                        visible = !actives,
                        exit = fadeOut(animationSpec = tween(50)) + slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        ),
                        enter = fadeIn(animationSpec = tween(50)) + slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(50, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.unfocus)
                        )
                    }
                }

            },

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.background))
            ) {

            }
        }
}


@Composable
fun searchName(
    navController: NavController,
    name: String,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardColors(
            contentColor = colorResource(R.color.DarkSlateBlue),
            containerColor = colorResource(R.color.DarkSlateBlue),
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ), onClick = {
            navController.navigate("message/$name")
        }
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            color = colorResource(R.color.BurlyWood),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}}