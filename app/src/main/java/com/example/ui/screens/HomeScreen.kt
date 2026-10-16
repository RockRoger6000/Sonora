package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AppThemeMode
import com.example.model.Track
import com.example.ui.components.TrackItemCard
import com.example.ui.theme.HiResGold
import com.example.ui.theme.HiResTagBackground
import com.example.ui.theme.SonoraCyan
import com.example.ui.theme.SonoraGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentTrack: Track?,
    isPlaying: Boolean,
    allTracks: List<Track>,
    favoriteTracks: List<Track>,
    themeMode: AppThemeMode,
    spectrumAmplitudes: List<Float>,
    onTrackSelect: (Track) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onShowTrackDetails: (Track) -> Unit,
    onOpenEqualizer: () -> Unit,
    onToggleTheme: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                        contentDescription = "Sonora Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            ),
                            color = SonoraGold
                        )
                        Text(
                            text = "HI-RES AUDIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.2.sp
                            ),
                            color = SonoraCyan
                        )
                    }
                }
            },
            actions = {
                // Theme Toggle
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("btn_toggle_dark_mode")
                ) {
                    Icon(
                        imageVector = if (themeMode == AppThemeMode.STUDIO_LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Equalizer button
                IconButton(
                    onClick = onOpenEqualizer,
                    modifier = Modifier.testTag("btn_quick_equalizer")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = stringResource(R.string.equalizer_title),
                        tint = SonoraGold
                    )
                }

                // Search button -> opens Library
                IconButton(
                    onClick = onNavigateToLibrary,
                    modifier = Modifier.testTag("btn_quick_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Now Playing Banner
            item {
                val heroTrack = currentTrack ?: allTracks.firstOrNull()
                if (heroTrack != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onNavigateToNowPlaying() }
                            .testTag("hero_now_playing_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            Color(0xFF1E293B)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Artwork Thumbnail with glowing border
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black)
                                ) {
                                    if (heroTrack.drawableResId != null) {
                                        Image(
                                            painter = painterResource(id = heroTrack.drawableResId),
                                            contentDescription = heroTrack.title,
                                            modifier = Modifier.size(76.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else if (!heroTrack.albumArtUri.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = heroTrack.albumArtUri,
                                            contentDescription = heroTrack.title,
                                            modifier = Modifier.size(76.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                                            contentDescription = heroTrack.title,
                                            modifier = Modifier.size(76.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = HiResTagBackground,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = heroTrack.hiResBadge,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HiResGold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = heroTrack.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = heroTrack.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Spectrum amplitude mini bar
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        verticalAlignment = Alignment.Bottom,
                                        modifier = Modifier.height(14.dp)
                                    ) {
                                        spectrumAmplitudes.take(10).forEach { amp ->
                                            val h = (amp * 14).dp.coerceIn(2.dp, 14.dp)
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(h)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(if (isPlaying) SonoraGold else MaterialTheme.colorScheme.outline)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                FilledIconButton(
                                    onClick = onTogglePlayPause,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("hero_play_pause"),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = SonoraGold,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hi-Res Highlights Carousel
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hi-Res Audiophile Picks",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${allTracks.count { it.format.isHiRes }} Masters",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SonoraGold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(allTracks.filter { it.format.isHiRes }) { track ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onTrackSelect(track) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black)
                                ) {
                                    if (track.drawableResId != null) {
                                        Image(
                                            painter = painterResource(id = track.drawableResId),
                                            contentDescription = track.title,
                                            modifier = Modifier.size(130.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                                            contentDescription = track.title,
                                            modifier = Modifier.size(130.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // Hi-Res corner tag
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = track.format.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = HiResGold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "${track.bitDepth}-bit / ${track.formattedSampleRate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Top Favorites Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.playlist_favorites),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${favoriteTracks.size} songs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (favoriteTracks.isEmpty()) {
                item {
                    Text(
                        text = "Tap the heart on any song to add to Favorites for quick access",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(favoriteTracks) { track ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        TrackItemCard(
                            track = track,
                            isCurrentPlaying = currentTrack?.id == track.id,
                            onTrackClick = { onTrackSelect(track) },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onShowDetails = { onShowTrackDetails(track) }
                        )
                    }
                }
            }

            // All Audio Tracks Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All Tracks (${allTracks.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(allTracks.take(8)) { track ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TrackItemCard(
                        track = track,
                        isCurrentPlaying = currentTrack?.id == track.id,
                        onTrackClick = { onTrackSelect(track) },
                        onToggleFavorite = { onToggleFavorite(track) },
                        onShowDetails = { onShowTrackDetails(track) }
                    )
                }
            }
        }
    }
}
