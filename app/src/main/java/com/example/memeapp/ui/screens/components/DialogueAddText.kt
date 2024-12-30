package com.example.memeapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memeapp.R
import com.example.memeapp.ui.theme.paddingExtraLarge

@Composable
fun EditableTextDialog(
    title: String = "Text",
    initialText: String = "Tap twice to edit",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    val context = LocalContext.current
    val customCursorColor = colorResource(R.color.white)
    val textFieldBackgroundColor = colorResource(R.color.tap_twice_edit_color) // New background color

    val customTextSelectionColors = TextSelectionColors(
        handleColor = customCursorColor,
        backgroundColor = customCursorColor.copy(alpha = 0.4f)
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.choose_temp_color)
            )
        },
        text = {
            Column {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(color = colorResource(R.color.topbar_bg)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(textFieldBackgroundColor),
                        cursorBrush = SolidColor(colorResource(R.color.white))
                    )
                }
                Spacer(modifier = Modifier.height(paddingExtraLarge))
                HorizontalDivider(color = colorResource(R.color.topbar_bg), thickness = 1.dp)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(R.color.ok_cancel)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("OK")
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
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun PreviewEditableTextDialog() {
    EditableTextDialog(
        title = "Text",
        initialText = "Tap twice to edit",
        onDismiss = {},
        onConfirm = { editedText -> println("Edited Text: $editedText") }
    )
}