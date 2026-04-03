package com.adam.citybuddy.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adam.citybuddy.ml.TFLiteHelper
import kotlinx.coroutines.delay

@Composable
fun SurveyScreen(
    tfliteHelper: TFLiteHelper, // We pass the shared AI helper here
    onMatchFound: (String) -> Unit
) {
    var currentQuestion by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val userAnswers = remember { mutableStateListOf<String>() }

    val questions = listOf(
        "How has your sleep been lately?",
        "Are you feeling overwhelmed by studies or personal matters?",
        "Would you prefer talking to a peer or a professional?"
    )

    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            val fullContext = userAnswers.joinToString(separator = " ")
            delay(2000) // Aesthetic delay
            val finalMatch = tfliteHelper.predictMatch(fullContext)
            onMatchFound(finalMatch)
        }
    }

    Crossfade(targetState = isAnalyzing, label = "SurveyTransition") { analyzing ->
        if (analyzing) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F4FF)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF7B1FA2), strokeWidth = 6.dp)
                    Spacer(Modifier.height(24.dp))
                    Text("CityBuddy AI is analyzing...", color = Color(0xFF4A148C), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        } else {
            Box(Modifier.fillMaxSize().background(Color(0xFFF8F4FF))) {
                Column(modifier = Modifier.padding(32.dp).align(Alignment.Center)) {
                    LinearProgressIndicator(
                        progress = { (currentQuestion + 1) / questions.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = Color(0xFF7B1FA2)
                    )
                    Spacer(Modifier.height(48.dp))
                    Text(text = questions[currentQuestion], style = MaterialTheme.typography.headlineSmall, color = Color(0xFF4A148C), fontWeight = FontWeight.ExtraBold)
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("Type your answer...") }
                    )
                    Button(
                        onClick = {
                            userAnswers.add(userInput)
                            if (currentQuestion < questions.size - 1) {
                                currentQuestion++; userInput = ""
                            } else {
                                isAnalyzing = true
                            }
                        },
                        modifier = Modifier.align(Alignment.End).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C)),
                        enabled = userInput.isNotBlank()
                    ) {
                        Text(if (currentQuestion == questions.size - 1) "Find My Match" else "Next")
                    }
                }
            }
        }
    }
}