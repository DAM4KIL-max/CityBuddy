package com.adam.citybuddy.navigation

import androidx.compose.runtime.*
// These imports are CRITICAL to fix the "Unresolved reference" errors
import com.adam.citybuddy.ui.screens.*
import com.adam.citybuddy.ml.TFLiteHelper
import androidx.compose.ui.platform.LocalContext

@Composable
fun NavGraph() {
    val context = LocalContext.current
    // We initialize the AI helper once here to share it across all screens
    val tfliteHelper = remember { TFLiteHelper(context) }

    var page by remember { mutableIntStateOf(-2) }

    when (page) {
        -2 -> WelcomeScreen { page = -3 }

        -3 -> AuthChoiceScreen(
            onLoginSelected = { page = -1 },
            onSignUpSelected = { page = -4 }
        )

        -1 -> LoginScreen(
            onLoginSuccess = { page = -5 },
            onBack = { page = -3 }
        )

        -4 -> SignUpScreen(
            // We removed tfliteHelper from here!
            onSignUpSuccess = { page = -1 }, // Goes to Login after making account
            onBack = { page = -3 }
        )

        -5 -> SurveyScreen(
            tfliteHelper = tfliteHelper, // FIX: Passing the AI helper here
            onMatchFound = { matchedName ->
                page = 0
            }
        )

        0 -> SupportHubScreen(
            onNav = { page = it },
            onLogout = { page = -3 }
        )

        1 -> PRSSelectionScreen(
            onBack = { page = 0 },
            onSelectPRS = { name ->
                println("Selected PRS: $name")
            }
        )

        2 -> CounselorSelectionScreen(
            onBack = { page = 0 }
        )
    }
}