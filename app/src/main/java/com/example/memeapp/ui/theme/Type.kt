package com.example.memeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.memeapp.R

val Manrope = FontFamily(
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        lineHeight = 24.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    titleSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    ),

    labelSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
)



