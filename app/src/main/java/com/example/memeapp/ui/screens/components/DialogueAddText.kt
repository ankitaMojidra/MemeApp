package com.example.memeapp.ui.screens.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeapp.R
import com.example.memeapp.ui.theme.paddingExtraLarge
import kotlinx.coroutines.delay

@Composable
fun DialogueAddText(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    title: String = stringResource(R.string.add_text)
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = colorResource(R.color.topbar_bg),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.choose_temp_color)
            )
        },
        text = {
            Column {
                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()
                var cursorVisible by remember { mutableStateOf(true) }

                // Blinking cursor animation
                LaunchedEffect(isFocused) {
                    if (isFocused) {
                        while (true) {
                            cursorVisible = !cursorVisible
                            delay(500) // Adjust blinking speed here (in milliseconds)
                        }
                    } else {
                        cursorVisible = true // Keep cursor visible when not focused (optional)
                    }
                }

                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(color = colorResource(R.color.white)),
                    modifier = Modifier
                        .wrapContentSize()
                        .drawBehind {
                            if (isFocused && cursorVisible) {
                                // Draw the cursor as a vertical line
                                drawLine(
                                    color = Color.White,
                                    start = Offset(text.length * 15.sp.toPx(), 0f), // Adjust 15.sp if needed
                                    end = Offset(text.length * 15.sp.toPx(), size.height),
                                    strokeWidth = 2.dp.toPx() // Adjust thickness as needed
                                )
                            }
                        },
                    interactionSource = interactionSource,
                    cursorBrush = SolidColor(Color.Transparent) // Hide the default cursor
                )

                Spacer(modifier = Modifier.height(paddingExtraLarge))
                HorizontalDivider(
                    color = colorResource(R.color.tap_twice_edit_color),
                    thickness = 1.dp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(R.color.ok_cancel)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text =
                    context.getString(R.string.ok),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(R.color.ok_cancel)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = context.getString(R.string.cancel),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewEditableTextDialog() {
    DialogueAddText(
        title = "Text",
        onDismiss = {},
        onConfirm = { editedText -> println("Edited Text: $editedText") }
    )
}