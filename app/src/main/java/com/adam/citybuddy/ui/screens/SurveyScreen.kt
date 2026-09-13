package com.adam.citybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.PRMatcher
import com.adam.citybuddy.PRPersona
import com.adam.citybuddy.ui.theme.AccentGold
import com.adam.citybuddy.ui.theme.ButtonPurple
import com.adam.citybuddy.ui.theme.DeepPurple
import kotlinx.coroutines.delay

// ── Counselor List ────────────────────────────────────────────────────────────
val counselors = listOf(
    PRPersona(
        name = "Sir Firdaus",
        role = "School Counselor",
        email = "firdaus@school.edu.my",
        image = "",
        keywords = listOf("Counseling", "Guidance", "Academic Support"),
        language = "English / Malay",
        phrases = emptyList()
    ),
    PRPersona(
        name = "Madam Wani",
        role = "School Counselor",
        email = "wani@school.edu.my",
        image = "",
        keywords = listOf("Counseling", "Guidance", "Emotional Wellbeing"),
        language = "English / Malay",
        phrases = emptyList()
    )
)

@Composable
fun SurveyScreen(
    prMatcher: PRMatcher,
    onMatchFound: (PRPersona) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var problemText by remember { mutableStateOf("") }
    var preferenceChoice by remember { mutableStateOf("") }
    var otherText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }

    val canProceedStep0 = problemText.isNotBlank()
    val canProceedStep1 = when (preferenceChoice) {
        "peer", "professional" -> true
        "other" -> otherText.isNotBlank()
        else -> false
    }

    // ── AI logic with TFLite Model ───────────────────────────────────────────
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            val (matchedPR, _) = prMatcher.match(problemText)
            delay(2500)

            // If user explicitly wants a counselor, give them one.
            // Otherwise, give them the AI-matched PR (or fallback to first PR / counselor)
            val finalMatch = if (preferenceChoice == "professional") {
                counselors.first()
            } else {
                matchedPR ?: prMatcher.prList.firstOrNull() ?: counselors.first()
            }

            onMatchFound(finalMatch)
        }
    }

    if (isAnalyzing) {
        AnalyzingScreen()
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple, ButtonPurple))
            )
    ) {
        // Decorative orbs
        Box(
            Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(60.dp, (-40).dp)
                .background(Color(0x22CE93D8), CircleShape)
                .blur(50.dp)
        )
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset((-40).dp, 40.dp)
                .background(Color(0x22F48FB1), CircleShape)
                .blur(40.dp)
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Column {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        null,
                        tint = AccentGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "AI Matching",
                        color = AccentGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Help us find your\nbest support match",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                )
            }

            // ── Progress dots ──────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                repeat(2) { index ->
                    val isActive = index == currentStep
                    val isDone   = index < currentStep
                    Box(
                        Modifier
                            .height(6.dp)
                            .width(if (isActive) 32.dp else 16.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isActive -> AccentGold
                                    isDone   -> AccentGold.copy(alpha = 0.5f)
                                    else     -> Color.White.copy(alpha = 0.2f)
                                }
                            )
                    )
                }
            }

            // ── Step content ───────────────────────────────────────────────
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                        .togetherWith(
                            fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 }
                        )
                },
                label = "stepTransition"
            ) { step ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        "Step ${step + 1} of 2",
                        color = Color.White.copy(0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))

                    when (step) {
                        // ── Step 0: Open problem description ───────────────
                        0 -> {
                            Text(
                                "What's been on your mind?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 28.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Describe what you're going through. The more detail you share, the better your match will be.",
                                color = Color.White.copy(0.6f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(20.dp))
                            OutlinedTextField(
                                value         = problemText,
                                onValueChange = { problemText = it },
                                modifier      = Modifier.fillMaxWidth(),
                                shape         = RoundedCornerShape(14.dp),
                                placeholder   = {
                                    Text(
                                        "e.g. I've been feeling really anxious about my exams and can't sleep…",
                                        color = Color.White.copy(0.35f),
                                        fontSize = 13.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor     = Color.White,
                                    unfocusedTextColor   = Color.White,
                                    focusedBorderColor   = AccentGold,
                                    unfocusedBorderColor = Color.White.copy(0.25f),
                                    cursorColor          = AccentGold
                                ),
                                minLines = 5
                            )
                        }

                        // ── Step 1: Preference picker ──────────────────────
                        1 -> {
                            Text(
                                "Who would you prefer to talk to?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 28.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "CityBuddy will use your answer to narrow down the best match.",
                                color = Color.White.copy(0.6f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(20.dp))

                            PreferenceButton(
                                emoji    = "🙋",
                                label    = "A Peer (PRS Student)",
                                sublabel = "A trained student helper my age",
                                selected = preferenceChoice == "peer",
                                color    = Color(0xFFFF9800),
                                onClick  = { preferenceChoice = "peer" }
                            )
                            Spacer(Modifier.height(10.dp))
                            PreferenceButton(
                                emoji    = "👩‍💼",
                                label    = "A Counselor",
                                sublabel = "A professional staff member",
                                selected = preferenceChoice == "professional",
                                color    = Color(0xFF2196F3),
                                onClick  = { preferenceChoice = "professional" }
                            )
                            Spacer(Modifier.height(10.dp))
                            PreferenceButton(
                                emoji    = "✏️",
                                label    = "No Preference",
                                sublabel = "Let the AI decide for me",
                                selected = preferenceChoice == "other",
                                color    = Color(0xFF9E9E9E),
                                onClick  = { preferenceChoice = "other" }
                            )
                        }
                    }
                }
            }

            // ── Button ─────────────────────────────────────────────────────
            Column {
                val canProceed = if (currentStep == 0) canProceedStep0 else canProceedStep1
                Button(
                    onClick = {
                        if (currentStep == 0) {
                            currentStep = 1
                        } else {
                            isAnalyzing = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canProceed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = AccentGold,
                        disabledContainerColor = Color.White.copy(0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (currentStep == 1) "Find My Match ✨" else "Next →",
                        color      = if (canProceed) DeepPurple else Color.White.copy(0.4f),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 16.sp
                    )
                }
                Spacer(Modifier.height(16.dp))

                // Back button on step 1
                if (currentStep == 1) {
                    Button(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "← Back",
                            color      = Color.White.copy(0.7f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ── Preference Button ─────────────────────────────────────────────────────────
@Composable
private fun PreferenceButton(
    emoji: String,
    label: String,
    sublabel: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) color else Color.White.copy(0.2f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    label,
                    color      = if (selected) color else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Text(sublabel, color = Color.White.copy(0.5f), fontSize = 12.sp)
            }
            if (selected) {
                Box(
                    Modifier
                        .size(20.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "✓",
                        color      = Color.White,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── Analyzing Screen ──────────────────────────────────────────────────────────
@Composable
private fun AnalyzingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 0.95f,
        targetValue   = 1.05f,
        label         = "pulse",
        animationSpec = infiniteRepeatable(
            tween(900, easing = EaseInOutSine),
            RepeatMode.Reverse
        )
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1A0035), DeepPurple, ButtonPurple))
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(200.dp)
                .scale(pulse)
                .background(Color(0x33CE93D8), CircleShape)
                .blur(40.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(40.dp)
        ) {
            CircularProgressIndicator(
                color       = AccentGold,
                strokeWidth = 5.dp,
                modifier    = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(32.dp))
            Text("✨", fontSize = 36.sp, modifier = Modifier.scale(pulse))
            Spacer(Modifier.height(16.dp))
            Text(
                "CityBuddy AI\nis finding your match…",
                color      = Color.White,
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign  = TextAlign.Center,
                lineHeight = 30.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Analyzing your response",
                color     = Color.White.copy(0.6f),
                fontSize  = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}