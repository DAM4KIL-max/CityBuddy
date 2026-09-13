package com.adam.citybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Root package imports ──
import com.adam.citybuddy.ui.screens.JournalScreen
import com.adam.citybuddy.ProfileScreen
import com.adam.citybuddy.data.DailyMoodRecord
import com.adam.citybuddy.data.MoodRepository
import com.adam.citybuddy.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

// Helper models for Home navigation cards
data class HubCard(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val page: Int,
    val accentColor: Color
)

data class MoodOption(
    val emoji: String,
    val label: String,
    val responses: List<String>
)

/**
 * Main Shell containing Bottom Navigation and Tab switching logic
 */
@Composable
fun MainTabContainer(
    onNavToPage: (Int) -> Unit,
    onRetakeSurvey: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurple,
                        selectedTextColor = DeepPurple,
                        indicatorColor = DeepPurple.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Spa, contentDescription = "Well-being") },
                    label = { Text("Well-being") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurple,
                        selectedTextColor = DeepPurple,
                        indicatorColor = DeepPurple.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Book, contentDescription = "Journal") },
                    label = { Text("Journal") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurple,
                        selectedTextColor = DeepPurple,
                        indicatorColor = DeepPurple.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurple,
                        selectedTextColor = DeepPurple,
                        indicatorColor = DeepPurple.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> SupportHubScreen(onNav = onNavToPage)
                1 -> WellbeingScreen(onBack = { selectedTab = 0 })
                2 -> JournalScreen(
                    onNewEntry = {
                        onNavToPage(6)
                    },
                    onOpenEntry = { entryId ->
                        onNavToPage(6)
                    }
                )
                3 -> ProfileScreen(onRetakeSurvey = onRetakeSurvey, onLogout = onLogout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportHubScreen(onNav: (Int) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userName = FirebaseAuth.getInstance().currentUser?.email
        ?.substringBefore("@")
        ?.replaceFirstChar { it.uppercase() }
        ?: "Friend"

    val moods = listOf(
        MoodOption("😊", "Great", listOf(
            "That's wonderful! Keep spreading that positive energy! ☀️",
            "Love to hear it! You're doing amazing today.",
            "Great days like this are worth remembering. Keep it up! 🌟"
        )),
        MoodOption("😐", "Okay", listOf(
            "That's alright. Not every day has to be perfect.",
            "Okay is perfectly fine. We're here if you need anything.",
            "Some days are just... okay, and that's okay. 🙂"
        )),
        MoodOption("😔", "Down", listOf(
            "I'm sorry you're feeling this way. You're not alone. 💙",
            "It's okay to feel down sometimes. Consider talking to someone.",
            "Tough days don't last. Reach out to a counselor if you need support."
        )),
        MoodOption("😰", "Stressed", listOf(
            "Take a deep breath. One thing at a time. 🌬️",
            "Stress is manageable. Let's find you some support today.",
            "Remember: asking for help is a sign of strength, not weakness."
        )),
        MoodOption("😡", "Frustrated", listOf(
            "It's okay to feel frustrated. Want to talk about it?",
            "Take a moment. Your feelings are valid and heard. 💬",
            "Step back, breathe. This feeling will pass. You've got this."
        ))
    )

    val savedTodayMoodStr = MoodRepository.getTodayMood(context)
    val initialSelectedMood = moods.find { it.label.equals(savedTodayMoodStr, ignoreCase = true) }

    var selectedMood by remember { mutableStateOf<MoodOption?>(initialSelectedMood) }
    var currentResponse by remember { mutableStateOf("") }
    var showMoodReply by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }
    var headerVisible by remember { mutableStateOf(false) }

    var recentRecords by remember { mutableStateOf(MoodRepository.getRecentRecords(context, 7)) }
    var streak by remember { mutableIntStateOf(MoodRepository.getStreak(context)) }
    var trend by remember { mutableStateOf(MoodRepository.getMoodTrend(context)) }

    LaunchedEffect(Unit) {
        headerVisible = true
        kotlinx.coroutines.delay(150)
        cardsVisible = true
    }

    LaunchedEffect(selectedMood) {
        if (selectedMood != null) {
            currentResponse = selectedMood!!.responses.random()
            showMoodReply = true

            MoodRepository.saveMood(context, selectedMood!!.label)
            recentRecords = MoodRepository.getRecentRecords(context, 7)
            streak = MoodRepository.getStreak(context)
            trend = MoodRepository.getMoodTrend(context)

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
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DeepPurple, ButtonPurple)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
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
                        enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { -it / 2 }
                    ) {
                        Column {
                            Text(
                                "Hello, $userName! 👋",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "How are you feeling today?",
                                color = Color.White.copy(0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(18.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                moods.forEach { mood ->
                                    val isSelected = selectedMood?.label == mood.label
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

                            AnimatedVisibility(
                                visible = showMoodReply,
                                enter = fadeIn() + expandVertically(),
                                exit  = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    Spacer(Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(0.18f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            currentResponse,
                                            color = Color.White,
                                            modifier = Modifier.padding(12.dp),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(500, delayMillis = 100))
            ) {
                ExpandedMoodJourneyCard(
                    recentRecords = recentRecords,
                    streak = streak,
                    trend = trend,
                    avgScore = MoodRepository.getAverageScore(context)
                )
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(500, delayMillis = 150))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌿", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Recommended for you",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DeepPurple
                            )
                            Text(
                                "5-minute stress reset based on your recent mood.",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "  What do you need?",
                color = DeepPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))

            val cards = listOf(
                HubCard(Icons.Filled.MenuBook,   "Well-being Tips", "Mental health library",  4, Color(0xFF4CAF50)),
                HubCard(Icons.Filled.Psychology, "Counselors",      "Professional support",   2, Color(0xFF2196F3)),
                HubCard(Icons.Filled.People,     "PRS Students",    "Peer resource students", 1, Color(0xFFFF9800)),
            )

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

            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNav(5) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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

@Composable
fun ExpandedMoodJourneyCard(
    recentRecords: List<DailyMoodRecord>,
    streak: Int,
    trend: Int?,
    avgScore: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌱", fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Your Mood Journey",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepPurple
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$streak day streak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            if (avgScore != -1) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        val animatedScore by animateIntAsState(
                            targetValue = avgScore,
                            animationSpec = tween(durationMillis = 800),
                            label = "scoreAnim"
                        )
                        Text(
                            text = "$animatedScore%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepPurple
                        )
                        Text(
                            text = "Recent average mood",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (trend != null) {
                        val isPositive = trend >= 0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = if (isPositive) "↗ $trend% better" else "↘ ${kotlin.math.abs(trend)}% lower",
                                color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Keep checking in for trends",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                MoodGraphView(recentRecords)
                Spacer(Modifier.height(16.dp))
                WeeklyEmojiRow(recentRecords)

            } else {
                Text(
                    text = "Keep checking in each day to see your progress.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun MoodGraphView(records: List<DailyMoodRecord>) {
    val scores = remember(records) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val result = FloatArray(7) { 0f }

        for (i in 6 downTo 0) {
            val dateStr = sdf.format(cal.time)
            val match = records.find { it.dateString == dateStr }
            result[i] = match?.score?.toFloat() ?: 0f
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        result
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val spacing = width / 6f

            val path = Path()
            var firstPoint = true

            for (i in 0 until 7) {
                val score = scores[i]
                if (score > 0f) {
                    val x = i * spacing
                    val y = height - ((score / 100f) * height)

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                    drawCircle(
                        color = ButtonPurple,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            drawPath(
                path = path,
                color = DeepPurple,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
fun WeeklyEmojiRow(records: List<DailyMoodRecord>) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayLabelFormat = SimpleDateFormat("E", Locale.getDefault())

    val weekData = remember(records) {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            val dateStr = sdf.format(cal.time)
            val dayLabel = dayLabelFormat.format(cal.time).take(1)
            val match = records.find { it.dateString == dateStr }
            val emoji = when (match?.moodLabel?.lowercase()) {
                "great" -> "😊"
                "okay" -> "🙂"
                "down" -> "😔"
                "stressed" -> "😰"
                "frustrated" -> "😡"
                else -> "—"
            }
            list.add(0, Pair(dayLabel, emoji))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        list
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekData.forEach { (label, emoji) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = label, fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Text(text = emoji, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun HubNavCard(
    card: HubCard,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = card.accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        card.icon,
                        contentDescription = card.title,
                        tint = card.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                card.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DeepPurple
            )
            Spacer(Modifier.height(2.dp))
            Text(
                card.subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}