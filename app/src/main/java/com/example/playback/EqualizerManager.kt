package com.example.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.util.Log

data class BandInfo(
    val index: Short,
    val centerFreqHz: Int,
    val levelMillibels: Short,
    val minLevel: Short,
    val maxLevel: Short
) {
    val frequencyLabel: String
        get() = if (centerFreqHz >= 1000) "${centerFreqHz / 1000} kHz" else "$centerFreqHz Hz"

    val gainDb: Int
        get() = (levelMillibels / 100)
}

class EqualizerManager {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    var isEnabled: Boolean = true
        private set

    var currentPreset: String = "Flat"
        private set

    var bassBoostStrength: Short = 300 // 0 to 1000
        private set

    // Default 5-band fallbacks if hardware equalizer is not yet attached
    private val fallbackBands = listOf(
        BandInfo(0, 60, 0, -1500, 1500),
        BandInfo(1, 230, 0, -1500, 1500),
        BandInfo(2, 910, 0, -1500, 1500),
        BandInfo(3, 3600, 0, -1500, 1500),
        BandInfo(4, 14000, 0, -1500, 1500)
    )

    private val customBandLevels = mutableMapOf<Short, Short>()

    fun attachToAudioSession(audioSessionId: Int) {
        if (audioSessionId <= 0) return
        try {
            release()
            val eq = Equalizer(0, audioSessionId)
            eq.enabled = isEnabled
            equalizer = eq

            val bb = BassBoost(0, audioSessionId)
            bb.enabled = isEnabled
            bb.setStrength(bassBoostStrength)
            bassBoost = bb

            applyPreset(currentPreset)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Could not attach hardware equalizer: ${e.message}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set equalizer enabled: ${e.message}")
        }
    }

    fun setBassBoost(strength: Short) {
        bassBoostStrength = strength.coerceIn(0, 1000)
        try {
            bassBoost?.setStrength(bassBoostStrength)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set bass boost: ${e.message}")
        }
    }

    fun getBands(): List<BandInfo> {
        val eq = equalizer ?: return fallbackBands.map {
            val level = customBandLevels[it.index] ?: it.levelMillibels
            it.copy(levelMillibels = level)
        }

        return try {
            val numBands = eq.numberOfBands
            val min = eq.bandLevelRange[0]
            val max = eq.bandLevelRange[1]
            (0 until numBands).map { i ->
                val shortIdx = i.toShort()
                val freq = eq.getCenterFreq(shortIdx) / 1000
                val level = eq.getBandLevel(shortIdx)
                BandInfo(shortIdx, freq, level, min, max)
            }
        } catch (e: Exception) {
            fallbackBands
        }
    }

    fun setBandLevel(band: Short, millibels: Short) {
        customBandLevels[band] = millibels
        currentPreset = "Custom"
        try {
            equalizer?.setBandLevel(band, millibels)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set band level: ${e.message}")
        }
    }

    fun applyPreset(presetName: String) {
        currentPreset = presetName
        val presetMap = getPresetOffsets(presetName)

        val bands = getBands()
        bands.forEach { band ->
            val ratio = presetMap.getOrElse(band.index.toInt()) { 0 }
            val level = (ratio * 100).toShort()
            customBandLevels[band.index] = level
            try {
                equalizer?.setBandLevel(band.index, level)
            } catch (_: Exception) {}
        }

        if (presetName == "Bass Heavy") {
            setBassBoost(800)
        } else if (presetName == "Electronic") {
            setBassBoost(600)
        } else if (presetName == "Flat") {
            setBassBoost(200)
        }
    }

    private fun getPresetOffsets(preset: String): List<Int> {
        return when (preset) {
            "Rock" -> listOf(4, 2, -1, 3, 5)
            "Pop" -> listOf(-1, 2, 4, 2, -1)
            "Jazz" -> listOf(3, 1, 0, 2, 3)
            "Classical" -> listOf(4, 3, 2, 2, 4)
            "Hip Hop" -> listOf(5, 3, 0, 2, 3)
            "Heavy Metal" -> listOf(5, 1, -2, 3, 6)
            "Acoustic" -> listOf(3, 2, 1, 3, 3)
            "Electronic" -> listOf(5, 4, -1, 3, 4)
            "Bass Heavy" -> listOf(7, 5, 1, 0, 0)
            "Flat" -> listOf(0, 0, 0, 0, 0)
            else -> listOf(0, 0, 0, 0, 0)
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
        } catch (_: Exception) {}
        equalizer = null
        bassBoost = null
    }
}
