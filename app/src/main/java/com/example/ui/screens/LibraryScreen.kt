package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.database.PlaylistEntity
import com.example.model.LibraryTab
import com.example.model.SortOption
import com.example.model.Track
import com.example.ui.components.TrackItemCard
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SonoraGold

@Composable
fun LibraryScreen(
    currentTrack: Track?,
    tracks: List<Track>,
    libraryTab: LibraryTab,
    sortOption: SortOption,
    searchQuery: String,
    playlists: List<PlaylistEntity>,
    onSelectLibraryTab: (LibraryTab) -> Unit,
    onSelectSortOption: (SortOption) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTrackSelect: (Track, List<Track>) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onShowTrackDetails: (Track) -> Unit,
    onScanStorage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    // Permission launcher for real storage audio scanning
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onScanStorage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Scan Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SonoraGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("library_search_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Scan storage button
            IconButton(
                onClick = {
                    val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    permissionLauncher.launch(perm)
                },
                modifier = Modifier.testTag("btn_scan_storage")
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Scan Storage",
                    tint = SonoraGold
                )
            }
        }

        // Subtabs: Songs, Albums, Artists, Playlists
        val tabs = listOf(
            LibraryTab.SONGS to stringResource(R.string.lib_songs),
            LibraryTab.ALBUMS to stringResource(R.string.lib_albums),
            LibraryTab.ARTISTS to stringResource(R.string.lib_artists),
            LibraryTab.PLAYLISTS to stringResource(R.string.lib_playlists)
        )
        val selectedIndex = tabs.indexOfFirst { it.first == libraryTab }.coerceAtLeast(0)

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = SonoraGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = SonoraGold,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { idx, (tab, title) ->
                Tab(
                    selected = selectedIndex == idx,
                    onClick = { onSelectLibraryTab(tab) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedIndex == idx) SonoraGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Sorting & Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${tracks.size} tracks available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSortMenu = true }
                        .testTag("btn_sort_dropdown")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = SonoraGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (sortOption) {
                                SortOption.YEAR_RELEASED_DESC -> "Year (Newest)"
                                SortOption.YEAR_RELEASED_ASC -> "Year (Oldest)"
                                SortOption.LATEST_ADDED -> "Latest Added"
                                SortOption.TITLE_ASC -> "Title (A-Z)"
                                SortOption.ARTIST_ASC -> "Artist (A-Z)"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_year_released) + " (Newest)") },
                        onClick = {
                            onSelectSortOption(SortOption.YEAR_RELEASED_DESC)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_year_released) + " (Oldest)") },
                        onClick = {
                            onSelectSortOption(SortOption.YEAR_RELEASED_ASC)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_latest_added)) },
                        onClick = {
                            onSelectSortOption(SortOption.LATEST_ADDED)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_title)) },
                        onClick = {
                            onSelectSortOption(SortOption.TITLE_ASC)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_artist)) },
                        onClick = {
                            onSelectSortOption(SortOption.ARTIST_ASC)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        // Tab Content Display
        when (libraryTab) {
            LibraryTab.SONGS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp)
                ) {
                    items(tracks) { track ->
                        Box(modifier = Modifier.padding(vertical = 4.dp)) {
                            TrackItemCard(
                                track = track,
                                isCurrentPlaying = currentTrack?.id == track.id,
                                onTrackClick = { onTrackSelect(track, tracks) },
                                onToggleFavorite = { onToggleFavorite(track) },
                                onShowDetails = { onShowTrackDetails(track) }
                            )
                        }
                    }
                }
            }

            LibraryTab.ALBUMS -> {
                // Group tracks by album
                val albumsGrouped = tracks.groupBy { it.album }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(albumsGrouped.toList()) { (albumName, albumTracks) ->
                        val first = albumTracks.first()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onTrackSelect(first, albumTracks)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black)
                                ) {
                                    if (first.drawableResId != null) {
                                        Image(
                                            painter = painterResource(id = first.drawableResId),
                                            contentDescription = albumName,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.sonora_launcher_icon_1792130927600),
                                            contentDescription = albumName,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = "${first.year}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HiResGold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = albumName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "${first.artist} • ${albumTracks.size} tracks",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            LibraryTab.ARTISTS -> {
                // Group tracks by artist -> artist-album view
                val artistsGrouped = tracks.groupBy { it.artist }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp)
                ) {
                    items(artistsGrouped.toList()) { (artistName, artistTracks) ->
                        val albums = artistTracks.map { it.album }.distinct()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onTrackSelect(artistTracks.first(), artistTracks)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = SonoraGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = SonoraGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = artistName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${albums.size} albums • ${artistTracks.size} songs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            LibraryTab.PLAYLISTS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp)
                ) {
                    item {
                        // Quick default playlists: Favorites, Hi-Res Masters
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val favs = tracks.filter { it.isFavorite }
                                    if (favs.isNotEmpty()) onTrackSelect(favs.first(), favs)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = SonoraGold,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.playlist_favorites),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${tracks.count { it.isFavorite }} saved tracks",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val hires = tracks.filter { it.format.isHiRes }
                                    if (hires.isNotEmpty()) onTrackSelect(hires.first(), hires)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = HiResGold,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.playlist_hires),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Lossless FLAC, ALAC & WAV studio tracks",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    items(playlists) { pl ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = SonoraGold,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = pl.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
