package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Track
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SonoraGold

@Composable
fun MiniPlayerBar(
    currentTrack: Track?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onBarClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = currentTrack != null,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        if (currentTrack == null) return@AnimatedVisibility

        val progress = if (durationMs > 0) {
            (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onBarClick() }
                .testTag("mini_player_bar"),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column {
                // Top Slim Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = SonoraGold,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Album Artwork
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (currentTrack.drawableResId != null) {
                            Image(
                                painter = painterResource(id = currentTrack.drawableResId),
                                contentDescription = currentTrack.title,
                                modifier = Modifier.size(46.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!currentTrack.albumArtUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = currentTrack.albumArtUri,
                                contentDescription = currentTrack.title,
                                modifier = Modifier.size(46.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                                contentDescription = currentTrack.title,
                                modifier = Modifier.size(46.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Track Title and Artist & Format
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentTrack.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentTrack.artist,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${currentTrack.format.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentTrack.format.isHiRes) HiResGold else SonoraGold
                                )
                            )
                        }
                    }

                    // Play/Pause Button
                    FilledIconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("mini_player_play_pause"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SonoraGold,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Next Button
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_next")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
