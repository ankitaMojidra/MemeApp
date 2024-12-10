package com.example.memeapp.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.memeapp.R
import com.example.memeapp.ui.theme.cardCornerRadius
import com.example.memeapp.ui.theme.defaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewMeme(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.new_meme),
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = colorResource(R.color.topbar_bg)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .padding(defaultPadding)
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // Placeholder for bottom panel content
                    Image(
                        painter = painterResource(R.drawable.epic_handshake_2),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Box(
                    modifier = Modifier
                        .background(color = colorResource(R.color.topbar_bg))
                        .fillMaxWidth()
                        .padding(defaultPadding),
                ) {
                    ElevatedButton(
                        onClick = {},
                        shape = RoundedCornerShape(cardCornerRadius),
                        modifier = Modifier.align(Alignment.Center).wrapContentSize()
                    ) {
                        Text(
                            text = context.getString(R.string.add_text),
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(cardCornerRadius),
                        modifier = Modifier.align(Alignment.BottomEnd).wrapContentSize()
                    ) {
                        Text(
                            text = context.getString(R.string.save_meme),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview
fun AddNewMemePreview() {
    val navController = rememberNavController()
    AddNewMeme(navController = navController)
}