package com.adam.citybuddy.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.adam.citybuddy.*
import com.adam.citybuddy.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

// 1. Defined an Enum to replace all the magic numbers
enum class Destination {
    Welcome, AuthChoice, Login, SignUp, Survey,
    MatchResult, MainHub, PRSSelection, CounselorSelection, Chat, Wellbeing
}

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val prMatcher = remember { PRMatcher(context) }

    // remember the auth instance so it doesn't re-fetch on every recomposition
    val auth = remember { FirebaseAuth.getInstance() }

    // Set the starting destination using the Enum
    val startDestination = if (auth.currentUser != null) Destination.Survey else Destination.Welcome
    var currentDestination by remember { mutableStateOf(startDestination) }

    var matchedPersona by remember { mutableStateOf<PRPersona?>(null) }
    var bookingPerson by remember { mutableStateOf<PRPersona?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try { prMatcher.close() } catch (_: Exception) {}
        }
    }

    // 2. Replaced the early 'return' with an if/else block
    if (bookingPerson != null) {
        // 3. Added BackHandler so the system back button dismisses the booking overlay
        BackHandler { bookingPerson = null }

        BookingScreen(
            person = bookingPerson!!,
            onBack = { bookingPerson = null }
        )
    } else {
        // Main Navigation Switch using our Enum
        when (currentDestination) {
            Destination.Welcome -> {
                WelcomeScreen(onStart = { currentDestination = Destination.AuthChoice })
            }

            Destination.AuthChoice -> {
                AuthChoiceScreen(
                    onLoginSelected = { currentDestination = Destination.Login },
                    onSignUpSelected = { currentDestination = Destination.SignUp }
                )
            }

            Destination.Login -> {
                // BackHandler allows the user to go back to AuthChoice safely
                BackHandler { currentDestination = Destination.AuthChoice }
                LoginScreen(
                    onLoginSuccess = { currentDestination = Destination.Survey },
                    onBack = { currentDestination = Destination.AuthChoice }
                )
            }

            Destination.SignUp -> {
                BackHandler { currentDestination = Destination.AuthChoice }
                SignUpScreen(
                    onSignUpSuccess = { currentDestination = Destination.Login },
                    onBack = { currentDestination = Destination.AuthChoice }
                )
            }

            Destination.Survey -> {
                SurveyScreen(
                    prMatcher = prMatcher,
                    onMatchFound = { persona ->
                        matchedPersona = persona
                        currentDestination = Destination.MatchResult
                    }
                )
            }

            Destination.MatchResult -> {
                // If they press back on the result, let's take them to the Main Hub
                BackHandler { currentDestination = Destination.MainHub }
                MatchResultScreen(
                    matchedPersona = matchedPersona,
                    onGoToHub = { currentDestination = Destination.MainHub },
                    onBook = { person -> bookingPerson = person }
                )
            }

            // ── Main Dashboard Container with Bottom Navigation ──
            Destination.MainHub -> {
                MainTabContainer(
                    onNavToPage = { destinationIndex: Int ->
                        // Map your bottom navigation indices to the Enum
                        currentDestination = when (destinationIndex) {
                            1 -> Destination.PRSSelection
                            2 -> Destination.CounselorSelection
                            3 -> Destination.Chat
                            4 -> Destination.Wellbeing
                            5 -> Destination.MatchResult
                            else -> Destination.MainHub
                        }
                    },
                    onRetakeSurvey = { currentDestination = Destination.Survey },
                    onLogout = {
                        auth.signOut() // Added sign out to ensure Firebase clears the user
                        currentDestination = Destination.AuthChoice
                    }
                )
            }

            Destination.PRSSelection -> {
                BackHandler { currentDestination = Destination.MainHub }
                PRSSelectionScreen(
                    prList = prMatcher.prList,
                    onBack = { currentDestination = Destination.MainHub },
                    onSelectPRS = { persona -> bookingPerson = persona }
                )
            }

            Destination.CounselorSelection -> {
                BackHandler { currentDestination = Destination.MainHub }
                CounselorSelectionScreen(onBack = { currentDestination = Destination.MainHub })
            }

            Destination.Chat -> {
                BackHandler { currentDestination = Destination.MainHub }
                ChatScreen(buddyName = "Community Chat", onBack = { currentDestination = Destination.MainHub })
            }

            Destination.Wellbeing -> {
                BackHandler { currentDestination = Destination.MainHub }
                WellbeingScreen(onBack = { currentDestination = Destination.MainHub })
            }
        }
    }
}