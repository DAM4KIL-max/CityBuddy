package com.adam.citybuddy.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.adam.citybuddy.ml.TFLiteHelper
import com.adam.citybuddy.ui.screens.BookingScreen
import com.adam.citybuddy.ui.screens.CounselorSelectionScreen
import com.adam.citybuddy.ui.screens.MatchResultScreen
import com.adam.citybuddy.ui.screens.PRSSelectionScreen
import com.adam.citybuddy.ui.screens.SupportPerson
import com.adam.citybuddy.ui.screens.AuthChoiceScreen
import com.adam.citybuddy.ui.screens.ChatScreen
import com.adam.citybuddy.ui.screens.LoginScreen
import com.adam.citybuddy.ui.screens.SignUpScreen
import com.adam.citybuddy.ui.screens.SupportHubScreen
import com.adam.citybuddy.ui.screens.SurveyScreen
import com.adam.citybuddy.ui.screens.WelcomeScreen
import com.adam.citybuddy.ui.screens.WellbeingScreen  // ← added
import com.adam.citybuddy.ui.screens.prsList
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val tfliteHelper: TFLiteHelper = remember { TFLiteHelper(context) }

    val auth = FirebaseAuth.getInstance()
    val startPage = if (auth.currentUser != null) -5 else -2  // your change kept

    var page by remember { mutableIntStateOf(startPage) }
    var matchedName by remember { mutableStateOf("") }
    var bookingPerson by remember { mutableStateOf<SupportPerson?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try { tfliteHelper.close() } catch (_: Exception) {}
        }
    }

    // Booking screen overlays everything when triggered
    if (bookingPerson != null) {
        BookingScreen(
            person = bookingPerson!!,
            onBack = { bookingPerson = null }
        )
        return  // ← added: prevents the when block from rendering behind BookingScreen
    }

    when (page) {

        // ── Auth flow ──────────────────────────────────────────────────────
        -2 -> WelcomeScreen(onStart = { page = -3 })

        -3 -> AuthChoiceScreen(
            onLoginSelected  = { page = -1 },
            onSignUpSelected = { page = -4 }
        )

        -1 -> LoginScreen(
            onLoginSuccess = { page = -5 },
            onBack         = { page = -3 }
        )

        -4 -> SignUpScreen(
            onSignUpSuccess = { page = -1 },
            onBack          = { page = -3 }
        )

        // ── Survey & AI Match ──────────────────────────────────────────────
        -5 -> SurveyScreen(
            tfliteHelper = tfliteHelper,
            onMatchFound = { name ->
                matchedName = name
                page = -6
            }
        )

        -6 -> MatchResultScreen(
            matchedName = matchedName,
            onGoToHub   = { page = 0 },
            onBook      = { person -> bookingPerson = person }
        )

        // ── Support Hub ────────────────────────────────────────────────────
        0 -> SupportHubScreen(
            onNav = { destination: Int ->
                when (destination) {
                    1 -> page = 1   // PRS Students
                    2 -> page = 2   // Counselors
                    3 -> page = 3   // Community Chat
                    4 -> page = 4   // Well-being Tips  ← added
                    5 -> page = -6  // AI Match result
                    else -> page = destination
                }
            },
            onLogout = { page = -3 }
        )

        // ── Sub-screens ────────────────────────────────────────────────────
        1 -> PRSSelectionScreen(
            onBack      = { page = 0 },
            onSelectPRS = { name ->
                val person = prsList.firstOrNull { it.name == name }
                if (person != null) bookingPerson = person
            }
        )

        2 -> CounselorSelectionScreen(
            onBack = { page = 0 }
        )

        3 -> ChatScreen(
            buddyName = "Community Chat",
            onBack    = { page = 0 }
        )

        4 -> WellbeingScreen(           // ← added
            onBack = { page = 0 }
        )
    }
}