@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.memeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.memeapp.R
import com.example.memeapp.ui.theme.MemeAppTheme
import com.example.memeapp.ui.theme.defaultPadding
import kotlinx.coroutines.launch

val imageList = listOf(
    R.drawable.disaster_girl_1,
    R.drawable.epic_handshake_2,
    R.drawable.left_exit_12_off_ramp_3,
    R.drawable.sad_pablo_escobar_4,
    R.drawable.change_my_mind_5,
    R.drawable.two_buttons_6,
    R.drawable.hide_the_pain_harold_7,
    R.drawable.the_rock_driving_8,
    R.drawable.grus_plan_9,
    R.drawable.i_bet_hes_thinking_about_other_women_10
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
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
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.group_8),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(defaultPadding))

                Text(
                    text = context.getString(R.string.tap_button),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorResource(R.color.tap_color)
                )
            }

            FloatingActionButton(
                onClick = {
                  /*  coroutineScope.launch {
                        showBottomSheet = true
                    }*/
                 },
                modifier = Modifier
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
            sheetState = sheetState
        ) {

            Text(
                text = context.getString(R.string.choose_template),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                text = context.getString(R.string.choose_template_next_meme),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(defaultPadding)
            ) {
                items(imageList) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes), contentDescription = null,
                        modifier = Modifier
                            .padding(defaultPadding)
                            .fillMaxWidth()
                            .aspectRatio(1f)
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
        HomeScreen()
    }
}