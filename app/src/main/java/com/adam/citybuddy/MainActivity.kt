package com.adam.citybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adam.citybuddy.ml.TFLiteHelper
import com.adam.citybuddy.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var tfliteHelper: TFLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your AI helper
        tfliteHelper = TFLiteHelper(this)
        val auth = FirebaseAuth.getInstance()

        setContent {
            // Surface provides the background for your app
            Surface(modifier = Modifier.fillMaxSize()) {
                val navController = rememberNavController()

                // Logic to skip login if already signed in
                val start = if (auth.currentUser != null) "support_hub" else "welcome"

                NavHost(
                    navController = navController,
                    startDestination = start
                ) {
                    composable("welcome") {
                        WelcomeScreen(onStart = { navController.navigate("auth_choice") })
                    }

                    composable("auth_choice") {
                        AuthChoiceScreen(
                            onLoginSelected = { navController.navigate("login") },
                            onSignUpSelected = { navController.navigate("signup") }
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("support_hub") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("signup") {
                        SignUpScreen(
                            // Notice I removed the tfliteHelper line here!
                            onSignUpSuccess = {
                                navController.navigate("support_hub") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("support_hub") {
                        SupportHubScreen(
                            onNav = { index ->
                                if (index == 2) navController.navigate("chat")
                            },
                            onLogout = {
                                navController.navigate("welcome") {
                                    popUpTo("support_hub") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("chat") {
                        ChatScreen(
                            buddyName = "Madam Wani",
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tfliteHelper.close() // Release ML memory
    }
}