package com.adam.citybuddy.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.foundation.BorderStroke as M3BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.ui.components.FloatingBubble
import com.adam.citybuddy.ui.theme.AccentGold
import com.adam.citybuddy.ui.theme.ButtonPurple
import com.adam.citybuddy.ui.theme.CardSurface
import com.adam.citybuddy.ui.theme.DeepPurple
import com.adam.citybuddy.ui.theme.SoftBg
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

// ── Data classes ──────────────────────────────────────────────────────────────
data class MoodOption(
    val emoji: String,
    val label: String,
    val responses: List<String>   // multiple responses, one picked randomly
)

data class HubCard(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val page: Int,
    val accent: Color
)

// ── Welcome ───────────────────────────────────────────────────────────────────
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        label = "pulse",
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine),
            RepeatMode.Reverse
        )
    )
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple, ButtonPurple))
            )
    ) {
        Box(
            Modifier
                .size(300.dp)
                .offset((-60).dp, (-40).dp)
                .background(Color(0x33CE93D8), CircleShape)
                .blur(60.dp)
        )
        Box(
            Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(40.dp, 60.dp)
                .background(Color(0x33F48FB1), CircleShape)
                .blur(50.dp)
        )
        FloatingBubble(Color(0x44CE93D8), 120f, 6000, 0)
        FloatingBubble(Color(0x33F48FB1), 80f, 4500, 800)

        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "City",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 52.sp
                    )
                    Text(
                        "Buddy",
                        color = AccentGold,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 64.sp,
                        modifier = Modifier.scale(pulseScale)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Your student support companion",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(64.dp))
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Get Started",
                            color = DeepPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Auth Choice ───────────────────────────────────────────────────────────────
@Composable
fun AuthChoiceScreen(onLoginSelected: () -> Unit, onSignUpSelected: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple)))
    ) {
        FloatingBubble(Color(0x33CE93D8), 100f, 5000, 0)
        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 3 }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome Back", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("or create a new account", color = Color.White.copy(0.6f), fontSize = 16.sp)
                    Spacer(Modifier.height(48.dp))
                    Button(
                        onClick = onLoginSelected,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Login", color = DeepPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onSignUpSelected,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        border = BorderStroke(2.dp, Color.White.copy(0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Sign Up", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ── Login ─────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple)))
    ) {
        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(700)) { it / 2 }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(40.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Sign In", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Good to see you again", color = Color.White.copy(0.6f), fontSize = 16.sp)
                    Spacer(Modifier.height(40.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.White.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.White.copy(0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) onLoginSuccess()
                                    else Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Login", color = DeepPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    TextButton(onClick = onBack) {
                        Text("← Back", color = Color.White.copy(0.7f))
                    }
                }
            }
        }
    }
}

// ── Sign Up ───────────────────────────────────────────────────────────────────
@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple)))
    ) {
        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(700)) { it / 2 }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(40.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Create Account", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Join CityBuddy today", color = Color.White.copy(0.6f), fontSize = 16.sp)
                    Spacer(Modifier.height(40.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.White.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.White.copy(0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) onSignUpSuccess()
                                        else Toast.makeText(
                                            context,
                                            "Sign Up Failed: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Create Account", color = DeepPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    TextButton(onClick = onBack) {
                        Text("← Back", color = Color.White.copy(0.7f))
                    }
                }
            }
        }
    }
}

// ── Support Hub ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportHubScreen(onNav: (Int) -> Unit, onLogout: () -> Unit) {
    val userName = FirebaseAuth.getInstance().currentUser?.email
        ?.substringBefore("@")
        ?.replaceFirstChar { it.uppercase() }
        ?: "Friend"

    // Each mood now has multiple responses that rotate randomly
    val moods = listOf(
        MoodOption("😊", "Great", listOf(
            "That's wonderful! Keep spreading that positive energy! ☀️",
            "Love to hear it! You're doing amazing today.",
            "Great days like this are worth remembering. Keep it up! 🌟",
            "Your energy is contagious! Hope it lasts all day.",
            "Brilliant! Make the most of this great feeling. 💪"
        )),
        MoodOption("😐", "Okay", listOf(
            "That's alright. Not every day has to be perfect.",
            "Okay is perfectly fine. We're here if you need anything.",
            "Some days are just... okay, and that's okay. 🙂",
            "Take it easy. Even slow days have their value.",
            "You're doing fine. Small steps still move you forward."
        )),
        MoodOption("😔", "Down", listOf(
            "I'm sorry you're feeling this way. You're not alone. 💙",
            "It's okay to feel down sometimes. Consider talking to someone.",
            "Tough days don't last. Reach out to a counselor if you need support.",
            "Your feelings are valid. We're here to help — always.",
            "Being down doesn't mean being stuck. Let us help you through it."
        )),
        MoodOption("😰", "Stressed", listOf(
            "Take a deep breath. One thing at a time. 🌬️",
            "Stress is manageable. Let's find you some support today.",
            "You've handled tough things before — you've got this.",
            "Try a short break. Even 5 minutes of fresh air helps. 🌿",
            "Remember: asking for help is a sign of strength, not weakness."
        )),
        MoodOption("😡", "Frustrated", listOf(
            "It's okay to feel frustrated. Want to talk about it?",
            "Take a moment. Your feelings are valid and heard. 💬",
            "Frustration often means you care deeply. Let's channel that.",
            "Step back, breathe. This feeling will pass. You've got this.",
            "We hear you. A counselor can help you work through this."
        )),
    )

    // Only 3 cards — Community removed
    val cards = listOf(
        HubCard(Icons.Filled.MenuBook,   "Well-being Tips", "Mental health library",  4, Color(0xFF4CAF50)),
        HubCard(Icons.Filled.Psychology, "Counselors",      "Professional support",   2, Color(0xFF2196F3)),
        HubCard(Icons.Filled.People,     "PRS Students",    "Peer resource students", 1, Color(0xFFFF9800)),
    )

    var selectedMood  by remember { mutableStateOf<MoodOption?>(null) }
    var currentResponse by remember { mutableStateOf("") }
    var showMoodReply by remember { mutableStateOf(false) }
    var cardsVisible  by remember { mutableStateOf(false) }
    var headerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        kotlinx.coroutines.delay(200)
        cardsVisible = true
    }

    LaunchedEffect(selectedMood) {
        if (selectedMood != null) {
            // Pick a random response from the list each time
            currentResponse = selectedMood!!.responses.random()
            showMoodReply = true
            kotlinx.coroutines.delay(4000)
            showMoodReply = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Support Hub",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }) {
                        Icon(Icons.Filled.Logout, "Logout", tint = Color.White.copy(0.8f))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepPurple)
            )
        },
        containerColor = SoftBg
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header banner ──────────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DeepPurple, ButtonPurple)))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Box(
                    Modifier
                        .size(120.dp)
                        .align(Alignment.TopEnd)
                        .offset(20.dp, (-20).dp)
                        .background(Color(0x22FFFFFF), CircleShape)
                        .blur(30.dp)
                )
                Column {
                    AnimatedVisibility(
                        visible = headerVisible,
                        enter = fadeIn(tween(600)) + slideInHorizontally(tween(600)) { -it / 2 }
                    ) {
                        Column {
                            Text(
                                "Hello, $userName! 👋",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "How are you feeling today?",
                                color = Color.White.copy(0.75f),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(20.dp))

                            // Mood emoji row
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                moods.forEach { mood ->
                                    val isSelected = selectedMood == mood
                                    val moodScale by animateFloatAsState(
                                        targetValue = if (isSelected) 1.2f else 1f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "moodScale"
                                    )
                                    Box(
                                        Modifier
                                            .scale(moodScale)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White
                                                else Color.White.copy(0.2f)
                                            )
                                            .clickable { selectedMood = mood }
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(mood.emoji, fontSize = 22.sp)
                                    }
                                }
                            }

                            // Mood response bubble
                            AnimatedVisibility(
                                visible = showMoodReply,
                                enter = fadeIn() + expandVertically(),
                                exit  = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    Spacer(Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            currentResponse,
                                            color = Color.White,
                                            modifier = Modifier.padding(12.dp),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "  What do you need?",
                color = DeepPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))

            // ── Card row (3 cards, single row) ────────────────────────────
            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cards.forEach { card ->
                        HubNavCard(
                            card = card,
                            modifier = Modifier.weight(1f),
                            onClick = { onNav(card.page) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── AI Match banner ────────────────────────────────────────────
            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(700, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNav(5) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = M3BorderStroke(0.dp, Color.Transparent)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFFAD1457))),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨", fontSize = 32.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Your AI Match",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "See who CityBuddy matched you with",
                                    color = Color.White.copy(0.75f),
                                    fontSize = 13.sp
                                )
                            }
                            Icon(Icons.Filled.ChevronRight, null, tint = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Hub Nav Card ──────────────────────────────────────────────────────────────
@Composable
fun HubNavCard(card: HubCard, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    Card(
        modifier = modifier
            .scale(cardScale)
            .aspectRatio(1f)
            .clickable { pressed = true; onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(card.accent.copy(0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(card.icon, null, tint = card.accent, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(card.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepPurple)
                Text(card.subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// ── Chat Screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(buddyName: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Pair<String, Boolean>>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(buddyName, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Online", fontSize = 12.sp, color = Color.White.copy(0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepPurple)
            )
        },
        containerColor = SoftBg
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize()) {
            if (messages.isEmpty()) {
                Box(
                    Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👋", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Say hello to $buddyName", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    messages.forEach { (text, isUser) ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp, topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd   = if (isUser) 4.dp  else 16.dp
                                ),
                                color = if (isUser) DeepPurple else Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text,
                                    color = if (isUser) Color.White else Color.Black,
                                    modifier = Modifier.padding(12.dp, 10.dp),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Surface(shadowElevation = 8.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Type a message…") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonPurple,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (message.isNotBlank()) {
                                messages.add(message to true)
                                message = ""
                            }
                        },
                        containerColor = DeepPurple,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}