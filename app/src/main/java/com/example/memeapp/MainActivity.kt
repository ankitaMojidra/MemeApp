package com.example.memeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memeapp.ui.screens.AddNewMeme
import com.example.memeapp.ui.screens.HomeScreen
import com.example.memeapp.ui.theme.MemeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            // Use WindowCompat for backward compatibility
            val window = this.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            // Set status bar color using the recommended approach
            window.statusBarColor = colorResource(id = R.color.topbar_bg).toArgb() // Change to your desired color
            // Control status bar icons/text appearance
            insetsController.isAppearanceLightStatusBars = false  // Force white icons/text

            MemeAppTheme {
                val navController = rememberNavController() // Create navController here

                NavHost(navController = navController, startDestination = "HomeScreen") {
                    composable("HomeScreen") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                    }
                    composable(
                        "AddNewMeme/{imageResId}", // Define the route with an argument placeholder
                        arguments = listOf(navArgument("imageResId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        // Extract the imageResId from the back stack entry
                        val imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0 // Provide a default value (e.g., 0) if the argument is not found
                        AddNewMeme(navController = navController, imageResId = imageResId)
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MemeAppTheme {
        Greeting("Android")
    }
}