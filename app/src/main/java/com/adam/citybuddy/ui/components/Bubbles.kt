package com.adam.citybuddy.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloatingBubble(color: Color, size: Float, duration: Int, delay: Int) {
    val transition = rememberInfiniteTransition(label = "bubble")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, delayMillis = delay, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Canvas(modifier = Modifier.size(size.dp)) {
        drawCircle(color = color, alpha = 0.3f, center = center.copy(y = center.y + offset))
    }
}