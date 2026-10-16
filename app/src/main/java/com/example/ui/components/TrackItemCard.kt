package com.example.ui.components

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.HiResTagBackground
import com.example.ui.theme.LosslessBadgeBg
import com.example.ui.theme.LosslessBadgeText
import com.example.ui.theme.SonoraGold

@Composable
fun TrackItemCard(
    track: Track,
    isCurrentPlaying: Boolean,
    onTrackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onTrackClick() }
            .testTag("track_item_${track.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art Thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (track.drawableResId != null) {
                    Image(
                        painter = painterResource(id = track.drawableResId),
                        contentDescription = track.title,
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Crop
                    )
                } else if (!track.albumArtUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = track.albumArtUri,
                        contentDescription = track.title,
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                        contentDescription = track.title,
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isCurrentPlaying) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrentPlaying) SonoraGold else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${track.artist} • ${track.album}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hi-Res Badge
                    val isHiRes = track.format.isHiRes
                    Surface(
                        color = if (isHiRes) HiResTagBackground else LosslessBadgeBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = track.format.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isHiRes) HiResGold else LosslessBadgeText
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${track.bitDepth}b/${track.sampleRateHz / 1000}kHz",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "• ${track.year}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = track.formattedDuration,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Favorite Button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("track_fav_${track.id}")
            ) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Specs / Details Button
            IconButton(
                onClick = onShowDetails,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("track_info_${track.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Track Specifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
