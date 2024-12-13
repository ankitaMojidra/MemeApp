package com.example.memeapp.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.memeapp.R
import com.example.memeapp.ui.theme.cardCornerRadius
import com.example.memeapp.ui.theme.defaultPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewMeme(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    var showDraggableText by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenWidthDp.dp
    var image by remember { mutableStateOf(R.drawable.i_bet_hes_thinking_about_other_women_10) }
    var memeText by remember { mutableStateOf("") }
    var textOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var shouldSaveMeme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val boxRef = remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Add permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with saving the meme
            shouldSaveMeme = true
        } else {
            // Permission denied
            Toast.makeText(context, "Permission denied to save image", Toast.LENGTH_SHORT).show()
        }
    }

    // Use LaunchedEffect to delay the save operation
    LaunchedEffect(shouldSaveMeme) {
        if (shouldSaveMeme) {
            saveMemeToGallery(boxRef.value, context, image, memeText, textOffset, coroutineScope)
            shouldSaveMeme = false // Reset the flag
        }
    }


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
                        .onGloballyPositioned { coordinates ->
                            boxRef.value = coordinates
                        }
                        .padding(defaultPadding)
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // Placeholder for bottom panel content
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    if (memeText.isNotEmpty()) {
                        Text(
                            text = memeText,
                            modifier = Modifier.offset { textOffset },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }

                    if (showDraggableText)
                        DraggableTextOverlay(
                            screenWidth,
                            screenHeight,
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
                        onClick = {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    shouldSaveMeme = true
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            } else {
                                shouldSaveMeme = true
                            }
                        },
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

fun saveMemeToGallery(
    layoutCoordinates: LayoutCoordinates?,
    context: Context,
    @DrawableRes imageResId: Int,
    memeText: String,
    textOffset: IntOffset,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        val bitmap = captureScreenshot(layoutCoordinates, context, imageResId, memeText, textOffset)
        saveImageToGallery(context, bitmap)
    }
}

private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "meme-${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        resolver.openOutputStream(uri)?.use { outputStream ->
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    } ?: run {
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}

private suspend fun captureScreenshot(
    layoutCoordinates: LayoutCoordinates?,
    context: Context,
    @DrawableRes imageResId: Int,
    memeText: String,
    textOffset: IntOffset
): Bitmap {
    return suspendCoroutine { continuation ->
        layoutCoordinates?.let { coordinates ->
            val bounds = coordinates.boundsInWindow()
            val size = bounds.size
            val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Draw the background image
            val imagePaint = Paint()
            val imageBitmap = BitmapFactory.decodeResource(context.resources, imageResId)
            canvas.drawBitmap(imageBitmap, null, Rect(0, 0, size.width.toInt(), size.height.toInt()), imagePaint)

            // Draw the text on the canvas using native Canvas
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK // Use Android graphics Color
                textSize = 40f // Adjust as needed
                textAlign = android.graphics.Paint.Align.LEFT // Default, adjust as needed
            }

            canvas.drawText(memeText, textOffset.x.toFloat(), textOffset.y.toFloat(), textPaint)

            continuation.resume(bitmap)
        } ?: run {
            continuation.resumeWithException(IllegalStateException("LayoutCoordinates not available"))
        }
    }
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