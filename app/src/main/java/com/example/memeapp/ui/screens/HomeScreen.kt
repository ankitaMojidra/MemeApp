@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.memeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.memeapp.R
import com.example.memeapp.ui.theme.MemeAppTheme
import com.example.memeapp.ui.theme.bottomSheetSize
import com.example.memeapp.ui.theme.cardCornerRadius
import com.example.memeapp.ui.theme.defaultPadding
import com.example.memeapp.viewmodel.MemeViewModel
import kotlinx.coroutines.launch

val imageList = listOf(
    R.drawable.left_exit_12_off_ramp_3,
    R.drawable.sad_pablo_escobar_4,
    R.drawable.eqjd8_12,
    R.drawable.eb198_32,
    R.drawable.two_buttons_6,
    R.drawable.third_world_skeptical_kid_11,
    R.drawable.hide_the_pain_harold_7,
    R.drawable.the_rock_driving_8,
    R.drawable.grus_plan_9,
    R.drawable.running_away_balloon_14,
    R.drawable.rcrc1_36,
    R.drawable.otri4_40
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val viewModel: MemeViewModel = hiltViewModel()
    val memes by viewModel.memes.collectAsState(initial = emptyList())
    var dragAmount by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = context.getString(R.string.your_memes),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.white)
                )
            },
            colors = topAppBarColors(
                containerColor = colorResource(R.color.topbar_bg),
            ),
        )


        Box(
            modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background)),
            contentAlignment = Alignment.Center
        ) {
            if (memes.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier
                        .padding(defaultPadding)
                        .fillMaxSize()
                ) {
                    items(memes) { meme ->
                        Image(
                            painter = rememberAsyncImagePainter(model = meme.image),
                            contentDescription = null,
                            modifier
                                .padding(cardCornerRadius)
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(colorResource(R.color.topbar_bg))
                                .clip(RoundedCornerShape(cardCornerRadius))
                                .clipToBounds(), // Add clipToBounds
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                Column(
                    modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Content of your screen
                    Image(
                        painter = painterResource(R.drawable.group_8),
                        contentDescription = null
                    )
                    Spacer(modifier.padding(defaultPadding))

                    Text(
                        text = context.getString(R.string.tap_button),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorResource(R.color.tap_color)
                    )
                }
            }


            // Clickable area at the bottom
            Box(
                modifier
                    .fillMaxWidth()
                    .height(bottomSheetSize)
                    .align(Alignment.BottomCenter)
                    .combinedClickable(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.show()
                                showBottomSheet = true
                            }
                        },
                        onLongClick = {
                            coroutineScope.launch {
                                sheetState.show()
                                showBottomSheet = true
                            }
                        }
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown()

                            do {
                                val event = awaitPointerEvent()
                                event.changes.forEach { change ->
                                    if (change.pressed) {
                                        dragAmount += change.position - change.previousPosition
                                        if (dragAmount.y < -50) {
                                            coroutineScope.launch {
                                                sheetState.show()
                                                showBottomSheet = true
                                            }
                                        }
                                        change.consume()
                                    }
                                }
                            } while (event.changes.any { it.pressed })

                            dragAmount = Offset.Zero
                        }
                    },
            ) {
                // Optional: Add a visual indicator like a small line or handle
            }

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        showBottomSheet = true
                    }
                },
                modifier
                    .align(Alignment.BottomEnd)
                    .padding(defaultPadding),
                containerColor = colorResource(R.color.background)
            ) {
                Image(painter = painterResource(id = R.drawable.button), contentDescription = null)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorResource(R.color.topbar_bg)
        ) {
            Text(
                text = context.getString(R.string.choose_template),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.white),
                modifier = Modifier.padding(start = cardCornerRadius)
            )
            Text(
                text = context.getString(R.string.choose_template_next_meme),
                style = MaterialTheme.typography.labelMedium,
                color = colorResource(R.color.white),
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(imageList) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(cardCornerRadius)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(cardCornerRadius))
                            .clickable {
                                navController.navigate("AddNewMeme/$imageRes")
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MemeAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}