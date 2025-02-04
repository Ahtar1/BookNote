package com.example.booknote.presentation.util.record

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun ExoPlayer(uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val duration = exoPlayer.duration.coerceAtLeast(1L) // Süre 0 olmasın

    LaunchedEffect(exoPlayer) {
        while (true) {
            progress = exoPlayer.currentPosition.toFloat() / duration
            delay(5)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = progress,
            onValueChange = { newValue ->
                progress = newValue
                exoPlayer.seekTo((newValue * duration).toLong()) // Topu hareket ettirince ses ileri sarılsın
            }
        )

        // **Çalma/Durdurma Butonu**
        Button(onClick = {
            isPlaying = !isPlaying
            if (isPlaying) exoPlayer.play() else exoPlayer.pause()
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}
