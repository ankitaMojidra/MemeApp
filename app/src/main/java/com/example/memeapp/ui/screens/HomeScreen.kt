@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.memeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.memeapp.R
import com.example.memeapp.ui.theme.MemeAppTheme
import com.example.memeapp.ui.theme.defaultPadding

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
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

            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.button),
                contentDescription = "Clickable Icon",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable {

                    }
            )
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