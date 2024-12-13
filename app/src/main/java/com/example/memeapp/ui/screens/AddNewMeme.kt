package com.example.memeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.memeapp.R
import com.example.memeapp.ui.theme.cardCornerRadius
import com.example.memeapp.ui.theme.defaultPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewMeme(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    var showDraggableText by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenWidthDp.dp
    var memeText by remember { mutableStateOf("") }
    var textOffset by remember { mutableStateOf(IntOffset(0, 0)) }

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
                        painter = painterResource(R.drawable.i_bet_hes_thinking_about_other_women_10),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    if (memeText.isNotEmpty()) {
                        Text(
                            text = memeText, modifier = Modifier.offset { textOffset },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    if (showDraggableText)
                        DraggableTextOverlay(
                            screenWidth, screenHeight,
                            onTextChange = { text, offset ->
                                memeText = text
                                textOffset = offset
                            },
                            onEditDone = {
                                showDraggableText = false
                            },
                        )
                }

                Box(
                    modifier = Modifier
                        .background(color = colorResource(R.color.topbar_bg))
                        .fillMaxWidth()
                        .padding(defaultPadding),
                ) {
                    ElevatedButton(
                        onClick = {
                            showDraggableText = true

                        },
                        shape = RoundedCornerShape(cardCornerRadius),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .wrapContentSize()
                    ) {
                        Text(
                            text = context.getString(R.string.add_text),
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(cardCornerRadius),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .wrapContentSize()
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
fun DraggableTextOverlay(
    screenWidth: Dp,
    screenHeight: Dp,
    onTextChange: (String, IntOffset) -> Unit,
    onEditDone: () -> Unit
) {
    val density = LocalDensity.current
    val imageWidthPx = with(density) { screenWidth.toPx() }
    val imageHeightPx = with(density) { screenHeight.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get the size of the text
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString("TAP TWICE TO EDIT"),
        style = MaterialTheme.typography.bodyLarge
    )
    val textSize = textLayoutResult.size

    // Calculate the initial center position
    val initialOffsetX = (imageWidthPx - textSize.width) / 2
    val initialOffsetY = (imageHeightPx - textSize.height) / 2

    // Update offsetX and offsetY only when not editing
    if (!isEditing) {
        offsetX = initialOffsetX
        offsetY = initialOffsetY
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    if (!isEditing) {
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
            }
    ) {
        if (isEditing) {
            // Your text editing UI, e.g., a TextField

            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onTextChange(text, IntOffset(offsetX.roundToInt(), offsetY.roundToInt()))
                        onEditDone()
                        keyboardController?.hide()
                    }
                )
            )
            // Close button
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clickable {
                        isEditing = false
                        onEditDone()
                    },
                tint = Color.Black
            )
        } else {
            // Display the "Tap twice to edit" text
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.8f)) // Semi-transparent background
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = { isEditing = true })
            ) {
                Text(
                    "TAP TWICE TO EDIT",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
@Preview
fun AddNewMemePreview() {
    val navController = rememberNavController()
    AddNewMeme(navController = navController)
}