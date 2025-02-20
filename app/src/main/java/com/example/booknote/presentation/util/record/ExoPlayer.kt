package com.example.booknote.presentation.util.record

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExoPlayer(uri:Uri){
    val context = LocalContext.current

    val isPlaying = remember {
        mutableStateOf(false)
    }

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    isPlaying.value = false
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (isPlaying.value) {
                currentPosition.longValue = player.currentPosition
                sliderPosition.longValue = currentPosition.longValue
            }
            delay(100L)
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Slider(
            value = sliderPosition.longValue.toFloat(),
            onValueChange = {
                sliderPosition.longValue = it.toLong()
            },
            onValueChangeFinished = {
                currentPosition.longValue = sliderPosition.longValue
                player.seekTo(sliderPosition.longValue)
            },
            valueRange = 0f..totalDuration.longValue.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.DarkGray,
                inactiveTrackColor = Color.Gray,
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(75.dp),
                onClick = {
                    val newPos = (player.currentPosition - 5000L).coerceAtLeast(0L)
                    player.seekTo(newPos)
                    sliderPosition.longValue = newPos
                    currentPosition.longValue = newPos
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Replay5,
                    contentDescription = "5 saniye geri sar"
                )
            }

            Spacer(modifier = Modifier.width(0.dp))

            IconButton(onClick = {
                isPlaying.value = !isPlaying.value
                if (player.playbackState == Player.STATE_ENDED) {
                    player.seekTo(0)
                    player.play()
                } else if (isPlaying.value){
                    player.play()
                } else player.pause()
            }) {
                AnimatedContent(
                    targetState = isPlaying.value,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                    },
                    label = ""
                ) { targetState ->
                    if (targetState) {
                        Icon(
                            imageVector = Icons.Filled.PauseCircle,
                            contentDescription = "Pause",
                            modifier = Modifier
                                .size(100.dp),
                            tint = Color.Black
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Record",
                            modifier = Modifier
                                .size(100.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(0.dp))

            IconButton(
                modifier = Modifier
                    .size(75.dp),
                onClick = {
                    val newPos = (player.currentPosition + 5000L).coerceAtMost(totalDuration.longValue)
                    player.seekTo(newPos)
                    sliderPosition.longValue = newPos
                    currentPosition.longValue = newPos
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Forward5,
                    contentDescription = "5 saniye ileri sar"
                )
            }
        }
    }
}
