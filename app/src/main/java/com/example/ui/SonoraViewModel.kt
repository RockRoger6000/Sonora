package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SonoraApplication
import com.example.data.AudioScanner
import com.example.data.DemoTracks
import com.example.database.FavoriteEntity
import com.example.database.HistoryEntity
import com.example.database.PlaylistEntity
import com.example.database.PlaylistTrackEntity
import com.example.model.AppThemeMode
import com.example.model.LibraryTab
import com.example.model.RepeatMode
import com.example.model.SortOption
import com.example.model.Track
import com.example.playback.BandInfo
import com.example.social.SocialShareHelper
import com.example.sync.CloudSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SonoraViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as SonoraApplication
    private val engine = app.audioEngine
    private val db = app.database
    private val dao = db.musicDao()

    val isPlaying = engine.isPlaying
    val currentTrack = engine.currentTrack
    val currentPositionMs = engine.currentPositionMs
    val durationMs = engine.durationMs
    val repeatMode = engine.repeatMode
    val isShuffled = engine.isShuffled
    val sleepTimerSecondsLeft = engine.sleepTimerSecondsLeft
    val sleepTimerFadeActive = engine.sleepTimerFadeActive
    val spectrumAmplitudes = engine.spectrumAmplitudes

    private val _allTracks = MutableStateFlow<List<Track>>(emptyList())
    val allTracks: StateFlow<List<Track>> = _allTracks.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.YEAR_RELEASED_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _libraryTab = MutableStateFlow(LibraryTab.SONGS)
    val libraryTab: StateFlow<LibraryTab> = _libraryTab.asStateFlow()

    private val _selectedNavTab = MutableStateFlow(0) // 0: Home, 1: Now Playing, 2: Library, 3: Settings
    val selectedNavTab: StateFlow<Int> = _selectedNavTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.MIDNIGHT_DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow("en") // "en" or "in"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteIds: StateFlow<Set<Long>> = _favoriteIds.asStateFlow()

    val playlists = dao.getAllPlaylists().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Equalizer States
    private val _eqBands = MutableStateFlow<List<BandInfo>>(emptyList())
    val eqBands: StateFlow<List<BandInfo>> = _eqBands.asStateFlow()

    private val _eqPreset = MutableStateFlow("Flat")
    val eqPreset: StateFlow<String> = _eqPreset.asStateFlow()

    private val _bassBoostStrength = MutableStateFlow<Short>(300)
    val bassBoostStrength: StateFlow<Short> = _bassBoostStrength.asStateFlow()

    private val _isEqEnabled = MutableStateFlow(true)
    val isEqEnabled: StateFlow<Boolean> = _isEqEnabled.asStateFlow()

    // Dialog sheets state
    val trackDetailsToShow = MutableStateFlow<Track?>(null)
    val showEqualizerSheet = MutableStateFlow(false)
    val showSleepTimerSheet = MutableStateFlow(false)
    val trackForPlaylistSheet = MutableStateFlow<Track?>(null)
    val syncStatusMessage = MutableStateFlow<String?>(null)

    // Filtered and Sorted Tracks
    val displayedTracks = combine(_allTracks, _searchQuery, _sortOption, _favoriteIds) { tracks, query, sort, favs ->
        var list = tracks.map { it.copy(isFavorite = favs.contains(it.id)) }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.album.lowercase().contains(q) ||
                it.format.displayName.lowercase().contains(q)
            }
        }

        when (sort) {
            SortOption.YEAR_RELEASED_DESC -> list.sortedByDescending { it.year }
            SortOption.YEAR_RELEASED_ASC -> list.sortedBy { it.year }
            SortOption.LATEST_ADDED -> list.sortedByDescending { it.dateAdded }
            SortOption.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            SortOption.ARTIST_ASC -> list.sortedBy { it.artist.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTracks = combine(_allTracks, _favoriteIds) { tracks, favs ->
        tracks.filter { favs.contains(it.id) }.map { it.copy(isFavorite = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialData()
        observeFavorites()
        refreshEqualizerState()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val demoList = DemoTracks.getDemoTracks(app)
            _allTracks.value = demoList
            if (engine.currentTrack.value == null && demoList.isNotEmpty()) {
                engine.setPlaylist(demoList, demoList.first())
                engine.pause() // keep ready
            }
            scanDeviceTracks()
        }
    }

    fun scanDeviceTracks() {
        viewModelScope.launch {
            val localTracks = AudioScanner.scanDeviceAudio(app)
            val demoList = DemoTracks.getDemoTracks(app)
            val combined = (demoList + localTracks).distinctBy { it.id }
            _allTracks.value = combined
            refreshFavoritesMap()
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            dao.getAllFavoriteIds().collect { ids ->
                _favoriteIds.value = ids.toSet()
            }
        }
    }

    private fun refreshFavoritesMap() {
        val favs = _favoriteIds.value
        _allTracks.value = _allTracks.value.map { it.copy(isFavorite = favs.contains(it.id)) }
    }

    fun selectNavTab(index: Int) {
        _selectedNavTab.value = index
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setLibraryTab(tab: LibraryTab) {
        _libraryTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleDarkMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.STUDIO_LIGHT -> AppThemeMode.MIDNIGHT_DARK
            AppThemeMode.MIDNIGHT_DARK -> AppThemeMode.AMOLED_DARK
            AppThemeMode.AMOLED_DARK -> AppThemeMode.STUDIO_LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.MIDNIGHT_DARK
        }
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun playTrack(track: Track, customQueue: List<Track>? = null) {
        val queue = customQueue ?: _allTracks.value
        engine.setPlaylist(queue, track)
        viewModelScope.launch {
            dao.recordHistory(HistoryEntity(trackId = track.id))
        }
    }

    fun togglePlayPause() {
        engine.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        engine.seekTo(positionMs)
    }

    fun skipNext() {
        engine.skipNext()
    }

    fun skipPrevious() {
        engine.skipPrevious()
    }

    fun toggleShuffle() {
        engine.toggleShuffle()
    }

    fun cycleRepeatMode() {
        engine.cycleRepeatMode()
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            if (_favoriteIds.value.contains(track.id)) {
                dao.removeFavorite(track.id)
            } else {
                dao.addFavorite(FavoriteEntity(trackId = track.id))
            }
        }
    }

    // Equalizer controls
    fun refreshEqualizerState() {
        _eqBands.value = engine.equalizerManager.getBands()
        _eqPreset.value = engine.equalizerManager.currentPreset
        _bassBoostStrength.value = engine.equalizerManager.bassBoostStrength
        _isEqEnabled.value = engine.equalizerManager.isEnabled
    }

    fun applyEqualizerPreset(preset: String) {
        engine.equalizerManager.applyPreset(preset)
        refreshEqualizerState()
    }

    fun setBandLevel(bandIndex: Short, millibels: Short) {
        engine.equalizerManager.setBandLevel(bandIndex, millibels)
        refreshEqualizerState()
    }

    fun setBassBoost(strength: Short) {
        engine.equalizerManager.setBassBoost(strength)
        _bassBoostStrength.value = strength
    }

    fun toggleEqualizer(enabled: Boolean) {
        engine.equalizerManager.setEnabled(enabled)
        _isEqEnabled.value = enabled
    }

    // Sleep Timer
    fun setSleepTimer(minutes: Int, fade: Boolean = true) {
        engine.setSleepTimer(minutes, fade)
    }

    fun cancelSleepTimer() {
        engine.cancelSleepTimer()
    }

    // Playlists
    fun createPlaylist(name: String, initialTrack: Track? = null) {
        viewModelScope.launch {
            val id = dao.insertPlaylist(PlaylistEntity(name = name))
            if (initialTrack != null) {
                dao.addTrackToPlaylist(PlaylistTrackEntity(playlistId = id, trackId = initialTrack.id))
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            dao.addTrackToPlaylist(PlaylistTrackEntity(playlistId, trackId))
        }
    }

    // Cloud Sync
    fun triggerCloudSync() {
        viewModelScope.launch {
            try {
                val timestamp = CloudSyncManager.performCloudSync(app, db)
                syncStatusMessage.value = "Synced at $timestamp"
            } catch (e: Exception) {
                syncStatusMessage.value = "Sync error: ${e.message}"
            }
        }
    }

    fun triggerCloudRestore() {
        viewModelScope.launch {
            val success = CloudSyncManager.restoreFromBackup(app, db)
            syncStatusMessage.value = if (success) "Restored successfully" else "No backup found"
        }
    }

    // Social Sharing
    fun shareToInstagram(track: Track) {
        SocialShareHelper.shareTrackToInstagram(app, track)
    }

    fun shareTrackGeneral(track: Track) {
        SocialShareHelper.shareTrackGeneral(app, track)
    }
}
