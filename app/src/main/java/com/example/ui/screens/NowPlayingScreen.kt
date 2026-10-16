package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.RepeatMode
import com.example.model.Track
import com.example.ui.theme.HiResGold
import com.example.ui.theme.HiResTagBackground
import com.example.ui.theme.SonoraCyan
import com.example.ui.theme.SonoraGold

@Composable
fun NowPlayingScreen(
    currentTrack: Track?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    isShuffled: Boolean,
    repeatMode: RepeatMode,
    spectrumAmplitudes: List<Float>,
    sleepTimerSecondsLeft: Long,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onShowTrackDetails: (Track) -> Unit,
    onShareInstagram: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentTrack == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No track currently playing",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var sliderPosition by remember { mutableFloatStateOf(-1f) }
    val displayProgress = if (sliderPosition >= 0) sliderPosition else {
        if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }

    // Vinyl slow rotation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = AnimRepeatMode.Restart
        ),
        label = "angle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PLAYING FROM LIBRARY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentTrack.album,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Hi-Res Badge tap for specs
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onShowTrackDetails(currentTrack) },
                color = HiResTagBackground,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentTrack.hiResBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = HiResGold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Vinyl Artwork Centerpiece with glowing shadow
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .shadow(elevation = 20.dp, shape = CircleShape, spotColor = SonoraGold)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(3.dp, SonoraGold.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val artModifier = Modifier
                .fillMaxSize()
                .rotate(if (isPlaying) angle else 0f)

            if (currentTrack.drawableResId != null) {
                Image(
                    painter = painterResource(id = currentTrack.drawableResId),
                    contentDescription = currentTrack.title,
                    modifier = artModifier,
                    contentScale = ContentScale.Crop
                )
            } else if (!currentTrack.albumArtUri.isNullOrEmpty()) {
                AsyncImage(
                    model = currentTrack.albumArtUri,
                    contentDescription = currentTrack.title,
                    modifier = artModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                    contentDescription = currentTrack.title,
                    modifier = artModifier,
                    contentScale = ContentScale.Crop
                )
            }

            // Center spindle hole
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .border(2.dp, SonoraGold, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spectrum Visualizer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            spectrumAmplitudes.forEach { amp ->
                val barHeight = (amp * 28).dp.coerceIn(3.dp, 28.dp)
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(SonoraGold, SonoraCyan)
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Track Title & Artist
        Text(
            text = currentTrack.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = currentTrack.artist,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Seekbar & Durations
        Slider(
            value = displayProgress,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                val targetMs = (sliderPosition * durationMs).toLong()
                onSeekTo(targetMs)
                sliderPosition = -1f
            },
            colors = SliderDefaults.colors(
                thumbColor = SonoraGold,
                activeTrackColor = SonoraGold,
                inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("now_playing_seekbar")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentSecs = (currentPositionMs / 1000).coerceAtLeast(0)
            val elapsed = "%d:%02d".format(currentSecs / 60, currentSecs % 60)
            val durationSecs = (durationMs / 1000).coerceAtLeast(0)
            val total = "%d:%02d".format(durationSecs / 60, durationSecs % 60)

            Text(
                text = elapsed,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = total,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playback Controls Row: Shuffle, Prev, Play/Pause, Next, Repeat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_shuffle")
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffled) SonoraGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Previous
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_previous")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Big Play/Pause FAB
            FilledIconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier
                    .size(72.dp)
                    .testTag("btn_play_pause_main"),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = SonoraGold,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(40.dp)
                )
            }

            // Next
            IconButton(
                onClick = onSkipNext,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_next")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Repeat
            IconButton(
                onClick = onCycleRepeat,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_repeat")
            ) {
                val icon = when (repeatMode) {
                    RepeatMode.REPEAT_ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.OFF) SonoraGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Secondary Action Icons: Favorite, Equalizer, Sleep Timer, Instagram Share, Specs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorite
            IconButton(
                onClick = { onToggleFavorite(currentTrack) },
                modifier = Modifier.testTag("btn_now_playing_fav")
            ) {
                Icon(
                    imageVector = if (currentTrack.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (currentTrack.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Equalizer
            IconButton(
                onClick = onOpenEqualizer,
                modifier = Modifier.testTag("btn_now_playing_eq")
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Equalizer",
                    tint = SonoraGold
                )
            }

            // Sleep Timer (Shows remaining countdown if active!)
            IconButton(
                onClick = onOpenSleepTimer,
                modifier = Modifier.testTag("btn_now_playing_sleep_timer")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerSecondsLeft > 0) SonoraGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (sleepTimerSecondsLeft > 0) {
                        Text(
                            text = "${sleepTimerSecondsLeft / 60}m",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = SonoraGold
                        )
                    }
                }
            }

            // Instagram Share
            IconButton(
                onClick = { onShareInstagram(currentTrack) },
                modifier = Modifier.testTag("btn_now_playing_insta")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share to Instagram",
                    tint = Color(0xFFE1306C)
                )
            }

            // Specs Inspector
            IconButton(
                onClick = { onShowTrackDetails(currentTrack) },
                modifier = Modifier.testTag("btn_now_playing_info")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Track Specifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
