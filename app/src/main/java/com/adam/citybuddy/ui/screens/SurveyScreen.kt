package com.adam.citybuddy.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.ml.TFLiteHelper
import com.adam.citybuddy.ui.theme.AccentGold
import com.adam.citybuddy.ui.theme.ButtonPurple
import com.adam.citybuddy.ui.theme.DeepPurple
import kotlinx.coroutines.delay

// The last question index — used to show special UI
private const val LAST_QUESTION = 2

@Composable
fun SurveyScreen(
    tfliteHelper: TFLiteHelper,
    onMatchFound: (String) -> Unit
) {
    var currentQuestion by remember { mutableIntStateOf(0) }
    var userInput       by remember { mutableStateOf("") }
    var isAnalyzing     by remember { mutableStateOf(false) }
    val userAnswers     = remember { mutableStateListOf<String>() }

    // Last question choice state
    var preferenceChoice by remember { mutableStateOf("") } // "peer", "professional", or "other"
    var otherText        by remember { mutableStateOf("") }

    val questions = listOf(
        "How has your sleep been lately?",
        "Are you feeling overwhelmed by studies or personal matters?",
        "Would you prefer talking to a peer or a professional?"
    )

    // Determines if the current answer is filled in enough to proceed
    val canProceed = when {
        currentQuestion == LAST_QUESTION -> when (preferenceChoice) {
            "other"        -> otherText.isNotBlank()
            "peer",
            "professional" -> true
            else           -> false
        }
        else -> userInput.isNotBlank()
    }

    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            val textContext = userAnswers.take(2).joinToString(separator = ". ")
            delay(2500)

            // 1. Get the probability scores for ALL 8 people (e.g., Azib: 0.8, Firdaus: 0.1...)
            // Note: We will need to update TFLiteHelper to support this function next!
            val allProbabilities = tfliteHelper.getAllProbabilities(textContext)

            // 2. Define the exact groups
            val professionals = listOf("Sir Firdaus", "Madam Wani")
            val peers = listOf("Naqeeb", "Azib", "Aidil", "Aamily", "Qaisy", "Ain")

            // 3. THE BAN SYSTEM
            val finalMatch = when (preferenceChoice) {
                "peer" -> {
                    // BAN professionals: Keep only peers, then find the highest AI score among them
                    allProbabilities.filterKeys { it in peers }
                        .maxByOrNull { it.value }?.key ?: "Naqeeb"
                }
                "professional" -> {
                    // BAN peers: Keep only professionals, find the highest AI score
                    allProbabilities.filterKeys { it in professionals }
                        .maxByOrNull { it.value }?.key ?: "Sir Firdaus"
                }
                else -> {
                    // "Other" / no choice: Pick the absolute highest score with no bans
                    allProbabilities.maxByOrNull { it.value }?.key ?: "Sir Firdaus"
                }
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

            // ── Progress dots ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                questions.forEachIndexed { index, _ ->
                    val isActive = index == currentQuestion
                    val isDone   = index < currentQuestion
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

            // ── Question card ───────────────────────────────────────────────
            AnimatedContent(
                targetState = currentQuestion,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                        .togetherWith(fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 })
                },
                label = "questionTransition"
            ) { qIndex ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    // Step indicator
                    Text(
                        "Question ${qIndex + 1} of ${questions.size}",
                        color = Color.White.copy(0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        questions[qIndex],
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 28.sp
                    )
                    Spacer(Modifier.height(24.dp))

                    if (qIndex == LAST_QUESTION) {
                        // ── Special last question UI ───────────────────────
                        LastQuestionInput(
                            preferenceChoice = preferenceChoice,
                            otherText        = otherText,
                            onChoiceChange   = { preferenceChoice = it },
                            onOtherTextChange = { otherText = it }
                        )
                    } else {
                        // ── Regular text input ─────────────────────────────
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            placeholder = {
                                Text(
                                    "Type your answer…",
                                    color = Color.White.copy(0.4f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor   = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor   = AccentGold,
                                unfocusedBorderColor = Color.White.copy(0.25f),
                                cursorColor          = AccentGold
                            ),
                            minLines = 3
                        )
                    }
                }
            }

            // ── Next / Submit button ────────────────────────────────────────
            Column {
                Button(
                    onClick = {
                        val answer = when {
                            currentQuestion == LAST_QUESTION -> when (preferenceChoice) {
                                "peer"         -> "peer"
                                "professional" -> "professional"
                                else           -> otherText
                            }
                            else -> userInput
                        }
                        userAnswers.add(answer)

                        if (currentQuestion < questions.size - 1) {
                            currentQuestion++
                            userInput = ""
                        } else {
                            isAnalyzing = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canProceed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        disabledContainerColor = Color.White.copy(0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (currentQuestion == questions.size - 1) "Find My Match ✨" else "Next →",
                        color = if (canProceed) DeepPurple else Color.White.copy(0.4f),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Last Question: Peer / Professional / Other ────────────────────────────────
@Composable
private fun LastQuestionInput(
    preferenceChoice: String,
    otherText: String,
    onChoiceChange: (String) -> Unit,
    onOtherTextChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Peer button
        PreferenceButton(
            emoji    = "🙋",
            label    = "Peer",
            sublabel = "Talk to a trained student helper",
            selected = preferenceChoice == "peer",
            color    = Color(0xFFFF9800),
            onClick  = { onChoiceChange("peer") }
        )

        // Counselor button
        PreferenceButton(
            emoji    = "👩‍💼",
            label    = "Counselor",
            sublabel = "Speak with a professional",
            selected = preferenceChoice == "professional",
            color    = Color(0xFF2196F3),
            onClick  = { onChoiceChange("professional") }
        )

        // Other option
        PreferenceButton(
            emoji    = "✏️",
            label    = "Other",
            sublabel = "Something else on your mind",
            selected = preferenceChoice == "other",
            color    = Color(0xFF9E9E9E),
            onClick  = { onChoiceChange("other") }
        )

        // Text box appears only when "other" is selected
        AnimatedVisibility(visible = preferenceChoice == "other") {
            OutlinedTextField(
                value = otherText,
                onValueChange = onOtherTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(14.dp),
                placeholder = {
                    Text("Tell us more…", color = Color.White.copy(0.4f))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White,
                    focusedBorderColor   = AccentGold,
                    unfocusedBorderColor = Color.White.copy(0.25f),
                    cursorColor          = AccentGold
                ),
                minLines = 2
            )
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
                if (selected) color.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.05f)
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
                    color = if (selected) color else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    sublabel,
                    color = Color.White.copy(0.5f),
                    fontSize = 12.sp
                )
            }
            if (selected) {
                Box(
                    Modifier
                        .size(20.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
        initialValue = 0.95f,
        targetValue  = 1.05f,
        label        = "pulse",
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
        // Glowing orb behind spinner
        Box(
            Modifier
                .size(200.dp)
                .scale(pulse)
                .background(Color(0x33CE93D8), CircleShape)
                .blur(40.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            CircularProgressIndicator(
                color       = AccentGold,
                strokeWidth = 5.dp,
                modifier    = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                "✨",
                fontSize = 36.sp,
                modifier = Modifier.scale(pulse)
            )
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
                "Analyzing your responses",
                color    = Color.White.copy(0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}