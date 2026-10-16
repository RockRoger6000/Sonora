package com.example.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.RepeatMode
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class SonoraAudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var mediaPlayer: MediaPlayer? = null
    val equalizerManager = EqualizerManager()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.REPEAT_ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled.asStateFlow()

    private val _playlist = MutableStateFlow<List<Track>>(emptyList())
    val playlist: StateFlow<List<Track>> = _playlist.asStateFlow()

    // Sleep Timer states
    private val _sleepTimerSecondsLeft = MutableStateFlow(0L)
    val sleepTimerSecondsLeft: StateFlow<Long> = _sleepTimerSecondsLeft.asStateFlow()

    private val _sleepTimerFadeActive = MutableStateFlow(true)
    val sleepTimerFadeActive: StateFlow<Boolean> = _sleepTimerFadeActive.asStateFlow()

    private var sleepTimerJob: Job? = null
    private var progressJob: Job? = null

    // Real-time simulated visualizer spectrum bars
    private val _spectrumAmplitudes = MutableStateFlow(List(16) { 0.2f })
    val spectrumAmplitudes: StateFlow<List<Float>> = _spectrumAmplitudes.asStateFlow()

    private var originalQueue = listOf<Track>()
    private var baseVolume = 1.0f

    init {
        startProgressTracker()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                try {
                    val player = mediaPlayer
                    if (player != null && player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition.toLong()
                        _durationMs.value = player.duration.toLong().coerceAtLeast(1L)

                        // Update dynamic audio wave spectrum for visuals
                        _spectrumAmplitudes.value = List(16) { index ->
                            val base = (index % 4 + 1) * 0.15f
                            (base + Random.nextFloat() * 0.5f).coerceIn(0.1f, 1.0f)
                        }
                    } else if (player != null && !player.isPlaying) {
                        _spectrumAmplitudes.value = List(16) { 0.15f }
                    }
                } catch (_: Exception) {}
                delay(200)
            }
        }
    }

    fun setPlaylist(tracks: List<Track>, startTrack: Track? = null) {
        originalQueue = tracks
        val activeList = if (_isShuffled.value) tracks.shuffled() else tracks
        _playlist.value = activeList
        val target = startTrack ?: activeList.firstOrNull()
        if (target != null) {
            playTrack(target)
        }
    }

    fun playTrack(track: Track) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                if (track.contentUri.startsWith("content://") || track.contentUri.startsWith("android.resource://")) {
                    setDataSource(context, Uri.parse(track.contentUri))
                } else {
                    val file = File(track.contentUri)
                    if (file.exists()) {
                        setDataSource(file.absolutePath)
                    } else {
                        // Fallback sample audio
                        val samplePath = SampleAudioGenerator.getOrCreateSampleWav(context, "sample_audio_${track.id}")
                        setDataSource(samplePath)
                    }
                }

                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                    _durationMs.value = mp.duration.toLong()
                    restoreVolume()
                    equalizerManager.attachToAudioSession(mp.audioSessionId)
                    notifyServiceUpdate()
                }

                setOnCompletionListener {
                    onTrackCompleted()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("SonoraAudioEngine", "MediaPlayer error: what=$what, extra=$extra")
                    true
                }

                prepareAsync()
            }

            mediaPlayer = player
            _currentTrack.value = track
            _currentPositionMs.value = 0L
            _durationMs.value = track.durationMs

        } catch (e: Exception) {
            Log.e("SonoraAudioEngine", "Error playing track: ${e.message}", e)
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: run {
            _currentTrack.value?.let { playTrack(it) }
            return
        }

        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.start()
            _isPlaying.value = true
        }
        notifyServiceUpdate()
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                notifyServiceUpdate()
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                notifyServiceUpdate()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
            _currentPositionMs.value = positionMs
        } catch (e: Exception) {
            Log.e("SonoraAudioEngine", "Error seeking: ${e.message}")
        }
    }

    fun skipNext() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val current = _currentTrack.value
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val nextIndex = if (currentIndex != -1 && currentIndex + 1 < list.size) {
            currentIndex + 1
        } else {
            0 // loop around
        }
        playTrack(list[nextIndex])
    }

    fun skipPrevious() {
        // If current position > 3 seconds, replay track from beginning
        if (_currentPositionMs.value > 3000) {
            seekTo(0)
            return
        }
        val list = _playlist.value
        if (list.isEmpty()) return
        val current = _currentTrack.value
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            list.lastIndex
        }
        playTrack(list[prevIndex])
    }

    fun toggleShuffle() {
        val newShuffle = !_isShuffled.value
        _isShuffled.value = newShuffle
        val current = _currentTrack.value
        if (newShuffle) {
            val shuffled = originalQueue.toMutableList()
            if (current != null) {
                shuffled.remove(current)
                shuffled.shuffle()
                shuffled.add(0, current)
            } else {
                shuffled.shuffle()
            }
            _playlist.value = shuffled
        } else {
            _playlist.value = originalQueue
        }
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.OFF
        }
    }

    private fun onTrackCompleted() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_ONE -> {
                seekTo(0)
                mediaPlayer?.start()
                _isPlaying.value = true
            }
            RepeatMode.REPEAT_ALL -> {
                skipNext()
            }
            RepeatMode.OFF -> {
                val list = _playlist.value
                val currentIndex = list.indexOfFirst { it.id == _currentTrack.value?.id }
                if (currentIndex != -1 && currentIndex + 1 < list.size) {
                    skipNext()
                } else {
                    _isPlaying.value = false
                    seekTo(0)
                }
            }
        }
        notifyServiceUpdate()
    }

    // Sleep Timer with Gradual Volume Fade-Out
    fun setSleepTimer(minutes: Int, fadeVolume: Boolean = true) {
        cancelSleepTimer()
        _sleepTimerFadeActive.value = fadeVolume
        if (minutes <= 0) return

        val totalSeconds = minutes * 60L
        _sleepTimerSecondsLeft.value = totalSeconds

        sleepTimerJob = scope.launch {
            var remaining = totalSeconds
            val fadeThreshold = 30L // fade during final 30 seconds

            while (remaining > 0 && isActive) {
                delay(1000)
                remaining--
                _sleepTimerSecondsLeft.value = remaining

                // Handle gradual volume fade
                if (fadeVolume && remaining <= fadeThreshold) {
                    val fadeFactor = (remaining.toFloat() / fadeThreshold).coerceIn(0f, 1f)
                    setVolumeInternal(fadeFactor)
                }
            }

            if (remaining <= 0) {
                pause()
                restoreVolume()
                _sleepTimerSecondsLeft.value = 0L
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerSecondsLeft.value = 0L
        restoreVolume()
    }

    private fun setVolumeInternal(ratio: Float) {
        try {
            val vol = (baseVolume * ratio).coerceIn(0.0f, 1.0f)
            mediaPlayer?.setVolume(vol, vol)
        } catch (_: Exception) {}
    }

    private fun restoreVolume() {
        try {
            mediaPlayer?.setVolume(baseVolume, baseVolume)
        } catch (_: Exception) {}
    }

    private fun notifyServiceUpdate() {
        SonoraPlaybackService.updateNotification(context, _currentTrack.value, _isPlaying.value)
        com.example.widget.SonoraMiniPlayerWidget.updateWidget(context, _currentTrack.value, _isPlaying.value)
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        equalizerManager.release()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
