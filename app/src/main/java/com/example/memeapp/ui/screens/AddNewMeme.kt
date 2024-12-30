package com.example.memeapp.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.memeapp.R
import com.example.memeapp.database.Meme
import com.example.memeapp.database.MemeDatabase
import com.example.memeapp.ui.theme.bottomSheetSize
import com.example.memeapp.ui.theme.cardCornerRadius
import com.example.memeapp.ui.theme.cardElevation
import com.example.memeapp.ui.theme.defaultPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddNewMeme(modifier: Modifier = Modifier, navController: NavController, imageResId: Int) {
    val context = LocalContext.current
    val view = LocalView.current
    var showDraggableText by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenWidthDp.dp
    var image by remember { mutableIntStateOf(imageResId) }
    var memeText by remember { mutableStateOf("") }
    var textOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var shouldCaptureMeme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var memeLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var shouldShareMeme by remember { mutableStateOf(false) }
    var shouldSaveToGallery by remember { mutableStateOf(false) }

    // Add permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with saving the meme
            shouldCaptureMeme = true
        } else {
            // Permission denied
            Toast.makeText(context, "Permission denied to save image", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.new_meme),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
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
            MemeContent(
                paddingValues = paddingValues,
                image = image,
                memeText = memeText,
                textOffset = textOffset,
                showDraggableText = showDraggableText,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                onShowDraggableTextChanged = { show ->
                    showDraggableText = show
                },
                onTextChange = { text, offset ->
                    memeText = text
                    textOffset = offset
                },
                onEditDone = {
                    showDraggableText = false
                },
                onMemeLayoutCoordinatesChanged = { coordinates ->
                    memeLayoutCoordinates = coordinates
                }
            )
        },
        bottomBar = {
            BottomBarContent(
                showDraggableText = showDraggableText,
                onShowDraggableTextChanged = { show ->
                    showDraggableText = show
                },
                onSaveMemeClicked = {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            shouldCaptureMeme = true
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    } else {
                        shouldCaptureMeme = true
                    }
                },
                onOpenBottomSheetClicked = {
                    coroutineScope.launch {
                        sheetState.show()
                        showBottomSheet = true
                    }
                }
            )
        },
    )

    // Use LaunchedEffect to trigger screenshot and save
    LaunchedEffect(shouldCaptureMeme) {
        if (shouldCaptureMeme) {
            // Introduce a small delay to ensure UI updates
            delay(100)
            memeLayoutCoordinates?.let {
                val bitmap = captureScreenshot(view, it, context, image, memeText, textOffset)
                saveImageToDatabase(context, bitmap)
            }
            shouldCaptureMeme = false
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                coroutineScope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
            containerColor = colorResource(R.color.topbar_bg)
        ) {
            Column(modifier = Modifier
                .padding(cardCornerRadius)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            // Handle press if needed
                        },
                        onTap = {
                            // Handle tap if needed
                        },
                        onLongPress = {
                            // Handle long press if needed
                        }
                    )
                    detectVerticalDragGestures { change, dragAmount ->
                        // Handle drag within the bottom sheet content if needed
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            shouldSaveToGallery = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.save),
                        contentDescription = "Save Image",
                        modifier = Modifier
                            .height(defaultPadding)
                            .width(defaultPadding)
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(start = cardCornerRadius)
                    ) {
                        Text(
                            text = context.getString(R.string.save_to_device),
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.white),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = context.getString(R.string.save_meme_file),
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.tap_color),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(Modifier.height(defaultPadding))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            shouldShareMeme = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.share), contentDescription = "share",
                        modifier = Modifier
                            .height(defaultPadding)
                            .width(defaultPadding)
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(start = cardCornerRadius)
                    ) {
                        Text(
                            text = context.getString(R.string.share_the_meme),
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.white),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = context.getString(R.string.share_meme_other_file),
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.tap_color),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(shouldShareMeme) {
        if (shouldShareMeme) {
            shareImage(context, view, memeLayoutCoordinates, image, memeText, textOffset)
            shouldShareMeme = false // Reset the trigger
        }
    }

    LaunchedEffect(shouldSaveToGallery) {
        if (shouldSaveToGallery) {
            memeLayoutCoordinates?.let {
                val bitmap = captureScreenshot(view, it, context, image, memeText, textOffset)
                saveImageToGallery(context, bitmap)
            }
            shouldSaveToGallery = false // Reset the trigger
        }
    }
}

private fun shareImage(
    context: Context,
    view: View,
    memeLayoutCoordinates: LayoutCoordinates?,
    image: Int,
    memeText: String,
    textOffset: IntOffset
) {
    CoroutineScope(Dispatchers.Main).launch {
        val imageUri =
            getTempFileUri(context, view, memeLayoutCoordinates, image, memeText, textOffset)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share Meme")
        context.startActivity(chooserIntent)
    }
}

suspend fun getTempFileUri(
    context: Context,
    view: View,
    memeLayoutCoordinates: LayoutCoordinates?,
    image: Int,
    memeText: String,
    textOffset: IntOffset
): Uri {
    val tempFile = withContext(Dispatchers.IO) {
        File.createTempFile("shared_meme_", ".jpg", context.cacheDir).apply {
            deleteOnExit() // Delete the file when the JVM exits
        }
    }

    // Assuming you have the meme image as a Bitmap called 'memeBitmap'
    val bitmap =
        captureScreenshot(view, memeLayoutCoordinates!!, context, image, memeText, textOffset)

    withContext(Dispatchers.IO) {
        val outputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()
    }

    return FileProvider.getUriForFile(
        context,
        "com.example.memeapp.fileprovider", // Replace with your authority
        tempFile
    )
}

private suspend fun saveImageToDatabase(context: Context, bitmap: Bitmap) {
    val byteArray = ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
    }
    val meme = Meme(image = byteArray)
    CoroutineScope(Dispatchers.IO).launch {
        MemeDatabase.getDatabase(context).memeDao().insert(meme)
    }
}

@Composable
private fun MemeContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    @androidx.annotation.DrawableRes image: Int,
    memeText: String,
    textOffset: IntOffset,
    showDraggableText: Boolean,
    screenWidth: Dp,
    screenHeight: Dp,
    onShowDraggableTextChanged: (Boolean) -> Unit,
    onTextChange: (String, IntOffset) -> Unit,
    onEditDone: () -> Unit,
    onMemeLayoutCoordinatesChanged: (LayoutCoordinates) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    onMemeLayoutCoordinatesChanged(coordinates)
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
            // Remove the Text composable here
            if (showDraggableText) {
                DraggableTextOverlay(
                    screenWidth,
                    screenHeight,
                    onTextChange = onTextChange,
                    onEditDone = onEditDone,
                )
            }
        }
    }
}

@Composable
private fun BottomBarContent(
    showDraggableText: Boolean,
    onShowDraggableTextChanged: (Boolean) -> Unit,
    onSaveMemeClicked: () -> Unit,
    onOpenBottomSheetClicked: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(color = colorResource(R.color.topbar_bg))
            .fillMaxWidth()
            .height(bottomSheetSize)
            .padding(defaultPadding)
            .clickable { onOpenBottomSheetClicked() },
    ) {
        ElevatedButton(
            onClick = {
                onShowDraggableTextChanged(!showDraggableText)
            },
            shape = RoundedCornerShape(cardCornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.topbar_bg),
                contentColor = colorResource(R.color.add_meme),
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
                .border(
                    width = 1.dp, color = colorResource(R.color.add_meme),
                    shape = RoundedCornerShape(cardElevation)
                )
        ) {
            Text(
                text = context.getString(R.string.add_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onSaveMemeClicked,
            shape = RoundedCornerShape(cardCornerRadius),
            colors = ButtonDefaults.buttonColors(
                contentColor = colorResource(R.color.save_meme),
                containerColor = colorResource(R.color.add_meme)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .wrapContentSize()
        ) {
            Text(
                text = context.getString(R.string.save_meme),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private suspend fun captureScreenshot(
    view: View,
    layoutCoordinates: LayoutCoordinates,
    context: Context,
    imageResId: Int,
    memeText: String,
    textOffset: IntOffset
): Bitmap = withContext(Dispatchers.IO) {

    val bounds = layoutCoordinates.boundsInWindow()
    val originalSize = bounds.size

    // 1. Get the original image dimensions
    val imageBitmap = BitmapFactory.decodeResource(context.resources, imageResId)
    val originalImageWidth = imageBitmap.width
    val originalImageHeight = imageBitmap.height

    // 2. Calculate the aspect ratio of the captured area
    val canvasWidth = originalSize.width.toInt()
    val canvasHeight = originalSize.height.toInt()
    val canvasAspectRatio = canvasWidth.toFloat() / canvasHeight.toFloat()

    // 3. Calculate target dimensions maintaining aspect ratio
    val targetWidth: Int
    val targetHeight: Int
    if (canvasAspectRatio > originalImageWidth.toFloat() / originalImageHeight.toFloat()) {
        // Canvas is wider than the image
        targetHeight = canvasHeight
        targetWidth =
            (targetHeight * originalImageWidth / originalImageHeight.toFloat()).toInt()
    } else {
        // Canvas is taller than or equal to the image in aspect ratio
        targetWidth = canvasWidth
        targetHeight =
            (targetWidth * originalImageHeight / originalImageWidth.toFloat()).toInt()
    }

    // 4. Create a bitmap with the TARGET dimensions
    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 5. Draw a white background
    canvas.drawColor(android.graphics.Color.WHITE)

    // 6. Calculate offsets to center the CAPTURED AREA within the target dimensions
    val offsetX = (targetWidth - canvasWidth) / 2
    val offsetY = (targetHeight - canvasHeight) / 2

    // Use View.drawToBitmap to capture the content
    val contentBitmap = view.drawToBitmap()

    // 9. Draw the temporary bitmap onto the main canvas with the calculated offsets
    canvas.drawBitmap(
        contentBitmap,
        android.graphics.Rect(
            bounds.left.roundToInt(),
            bounds.top.roundToInt(),
            bounds.right.roundToInt(),
            bounds.bottom.roundToInt()
        ),
        android.graphics.Rect(
            offsetX,
            offsetY,
            offsetX + canvasWidth,
            offsetY + canvasHeight
        ),
        null
    )

    // 10. Draw the text on the canvas with adjusted position
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 40f
        textAlign = android.graphics.Paint.Align.LEFT
    }

    // Draw text only if it's not empty
    if (memeText.isNotEmpty()) {
        canvas.drawText(
            memeText,
            textOffset.x.toFloat() + offsetX,
            textOffset.y.toFloat() + offsetY,
            textPaint
        )
    }

    bitmap
}

private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val folderName = "MasterMeme"
    val filename = "meme-${System.currentTimeMillis()}.jpg"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$folderName"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    Toast.makeText(context, "Image saved to $folderName folder", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    } else {
        val imagesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            folderName
        )
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val image = File(imagesDir, filename)
        image.outputStream().use { outputStream ->
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                Toast.makeText(context, "Image saved to $folderName folder", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
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
    AddNewMeme(navController = navController, imageResId = 0)
}

