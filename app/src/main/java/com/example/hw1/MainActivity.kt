package com.example.hw1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val generatedPassword = generatePassword()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "first") {

                    composable("first") {
                        FirstScreen(
                            onNavigateNext = { _, password ->
                                navController.navigate("second/$password")
                            }
                        )
                    }

                    composable("second/{password}") { backStackEntry ->
                        val password = backStackEntry.arguments?.getString("password").orEmpty()

                        SecondScreen(
                            enteredPassword = password,
                            generatedPassword = generatedPassword,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}