package com.example.booknote.presentation.focus.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedTimer(
    elapsedSeconds: Long,
    isRunning: Boolean,
    fontSize: Int = 48,
    onTick: () -> Unit
) {
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            onTick()
        }
    }

    val hours = (elapsedSeconds / 3600).toInt()
    val minutes = ((elapsedSeconds % 3600) / 60).toInt()
    val seconds = (elapsedSeconds % 60).toInt()

    TimeSegment(hours, minutes, seconds, fontSize)
}

@Composable
fun TimeSegment(hours: Int, minutes: Int, seconds: Int, fontSize: Int) {
    Row {
        TimeDigitGroup(hours, fontSize)
        Text(":", fontSize = fontSize.sp, modifier = Modifier.alignByBaseline())
        TimeDigitGroup(minutes, fontSize)
        Text(":", fontSize = fontSize.sp, modifier = Modifier.alignByBaseline())
        TimeDigitGroup(seconds, fontSize)
    }
}

@Composable
fun TimeDigitGroup(value: Int, fontSize: Int) {
    val str = String.format("%02d", value)
    Row {
        Digit(digit = str[0], fontSize = fontSize)
        Digit(digit = str[1], fontSize = fontSize)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Digit(digit: Char, fontSize: Int) {
    var previousDigit by remember { mutableStateOf(digit) }

    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            (slideInVertically { height -> height } + fadeIn()) with
                    (slideOutVertically { height -> -height } + fadeOut())
        },
        label = "DigitSlide"
    ) { currentDigit ->
        Text(
            text = currentDigit.toString(),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }

    LaunchedEffect(digit) {
        previousDigit = digit
    }
}