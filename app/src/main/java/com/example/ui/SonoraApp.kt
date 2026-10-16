package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import com.example.model.Track
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.EqualizerSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.TrackDetailsSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.SonoraGold

@Composable
fun SonoraApp(
    viewModel: SonoraViewModel
) {
    val selectedTab by viewModel.selectedNavTab.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPos by viewModel.currentPositionMs.collectAsState()
    val duration by viewModel.durationMs.collectAsState()
    val isShuffled by viewModel.isShuffled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val amplitudes by viewModel.spectrumAmplitudes.collectAsState()
    val sleepSeconds by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepFade by viewModel.sleepTimerFadeActive.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val allTracks by viewModel.allTracks.collectAsState()
    val displayedTracks by viewModel.displayedTracks.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    val libraryTab by viewModel.libraryTab.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val eqBands by viewModel.eqBands.collectAsState()
    val eqPreset by viewModel.eqPreset.collectAsState()
    val bassBoost by viewModel.bassBoostStrength.collectAsState()
    val isEqEnabled by viewModel.isEqEnabled.collectAsState()
    val syncStatus by viewModel.syncStatusMessage.collectAsState()

    val trackDetailsSheet by viewModel.trackDetailsToShow.collectAsState()
    val showEqSheet by viewModel.showEqualizerSheet.collectAsState()
    val showSleepSheet by viewModel.showSleepTimerSheet.collectAsState()
    val trackForPlaylist by viewModel.trackForPlaylistSheet.collectAsState()

    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectNavTab(0)
    }

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Persistent Mini Player above the navigation bar when not on Now Playing tab
                if (selectedTab != 1) {
                    MiniPlayerBar(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPos,
                        durationMs = duration,
                        onBarClick = { viewModel.selectNavTab(1) },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onNext = { viewModel.skipNext() }
                    )
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SonoraGold,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectNavTab(0) },
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.tab_home)) },
                        label = { Text(stringResource(R.string.tab_home), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SonoraGold,
                            selectedTextColor = SonoraGold,
                            indicatorColor = SonoraGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectNavTab(1) },
                        icon = { Icon(Icons.Default.PlayCircle, contentDescription = stringResource(R.string.tab_now_playing)) },
                        label = { Text(stringResource(R.string.tab_now_playing), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SonoraGold,
                            selectedTextColor = SonoraGold,
                            indicatorColor = SonoraGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_now_playing")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectNavTab(2) },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = stringResource(R.string.tab_library)) },
                        label = { Text(stringResource(R.string.tab_library), fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SonoraGold,
                            selectedTextColor = SonoraGold,
                            indicatorColor = SonoraGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_library")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectNavTab(3) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.tab_settings)) },
                        label = { Text(stringResource(R.string.tab_settings), fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SonoraGold,
                            selectedTextColor = SonoraGold,
                            indicatorColor = SonoraGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        allTracks = allTracks,
                        favoriteTracks = favoriteTracks,
                        themeMode = themeMode,
                        spectrumAmplitudes = amplitudes,
                        onTrackSelect = { track -> viewModel.playTrack(track, allTracks) },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                        onShowTrackDetails = { track -> viewModel.trackDetailsToShow.value = track },
                        onOpenEqualizer = { viewModel.showEqualizerSheet.value = true },
                        onToggleTheme = { viewModel.toggleDarkMode() },
                        onNavigateToLibrary = { viewModel.selectNavTab(2) },
                        onNavigateToNowPlaying = { viewModel.selectNavTab(1) }
                    )
                    1 -> NowPlayingScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPos,
                        durationMs = duration,
                        isShuffled = isShuffled,
                        repeatMode = repeatMode,
                        spectrumAmplitudes = amplitudes,
                        sleepTimerSecondsLeft = sleepSeconds,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSeekTo = { pos -> viewModel.seekTo(pos) },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onCycleRepeat = { viewModel.cycleRepeatMode() },
                        onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                        onOpenEqualizer = { viewModel.showEqualizerSheet.value = true },
                        onOpenSleepTimer = { viewModel.showSleepTimerSheet.value = true },
                        onShowTrackDetails = { track -> viewModel.trackDetailsToShow.value = track },
                        onShareInstagram = { track -> viewModel.shareToInstagram(track) }
                    )
                    2 -> LibraryScreen(
                        currentTrack = currentTrack,
                        tracks = displayedTracks,
                        libraryTab = libraryTab,
                        sortOption = sortOption,
                        searchQuery = searchQuery,
                        playlists = playlists,
                        onSelectLibraryTab = { tabSelected -> viewModel.setLibraryTab(tabSelected) },
                        onSelectSortOption = { opt -> viewModel.setSortOption(opt) },
                        onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                        onTrackSelect = { track, queue -> viewModel.playTrack(track, queue) },
                        onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                        onShowTrackDetails = { track -> viewModel.trackDetailsToShow.value = track },
                        onScanStorage = { viewModel.scanDeviceTracks() }
                    )
                    3 -> SettingsScreen(
                        themeMode = themeMode,
                        language = language,
                        syncStatusMessage = syncStatus,
                        onSelectTheme = { mode -> viewModel.setThemeMode(mode) },
                        onSelectLanguage = { lang -> viewModel.setLanguage(lang) },
                        onOpenEqualizer = { viewModel.showEqualizerSheet.value = true },
                        onOpenSleepTimer = { viewModel.showSleepTimerSheet.value = true },
                        onTriggerSync = { viewModel.triggerCloudSync() },
                        onTriggerRestore = { viewModel.triggerCloudRestore() }
                    )
                }
            }

            // Dialogs & Sheets
            if (showEqSheet) {
                EqualizerSheet(
                    isEnabled = isEqEnabled,
                    bands = eqBands,
                    currentPreset = eqPreset,
                    bassBoostStrength = bassBoost,
                    spectrumAmplitudes = amplitudes,
                    onToggleEnabled = { viewModel.toggleEqualizer(it) },
                    onSelectPreset = { viewModel.applyEqualizerPreset(it) },
                    onBandChange = { band, millibels -> viewModel.setBandLevel(band, millibels) },
                    onBassBoostChange = { viewModel.setBassBoost(it) },
                    onDismiss = { viewModel.showEqualizerSheet.value = false }
                )
            }

            if (showSleepSheet) {
                SleepTimerDialog(
                    secondsLeft = sleepSeconds,
                    fadeActive = sleepFade,
                    onStartTimer = { mins, fade -> viewModel.setSleepTimer(mins, fade) },
                    onCancelTimer = { viewModel.cancelSleepTimer() },
                    onDismiss = { viewModel.showSleepTimerSheet.value = false }
                )
            }

            trackDetailsSheet?.let { track ->
                TrackDetailsSheet(
                    track = track,
                    onShareInstagram = { viewModel.shareToInstagram(it) },
                    onShareGeneral = { viewModel.shareTrackGeneral(it) },
                    onDismiss = { viewModel.trackDetailsToShow.value = null }
                )
            }

            trackForPlaylist?.let { track ->
                AddToPlaylistDialog(
                    track = track,
                    playlists = playlists,
                    onCreatePlaylist = { name, tr -> viewModel.createPlaylist(name, tr) },
                    onAddToPlaylist = { plId, trId -> viewModel.addTrackToPlaylist(plId, trId) },
                    onDismiss = { viewModel.trackForPlaylistSheet.value = null }
                )
            }
        }
    }
}
