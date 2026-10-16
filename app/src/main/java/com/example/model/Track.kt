package com.example.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: String,
    val albumArtUri: String? = null,
    val drawableResId: Int? = null,
    val year: Int = 2024,
    val dateAdded: Long = System.currentTimeMillis(),
    val bitrateKbps: Int = 1411,
    val sampleRateHz: Int = 96000,
    val bitDepth: Int = 24,
    val channels: Int = 2,
    val format: AudioFormat = AudioFormat.FLAC,
    val fileSize: Long = 32_000_000L,
    val isFavorite: Boolean = false,
    val genre: String = "Audiophile",
    val playCount: Int = 0
) {
    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val formattedSampleRate: String
        get() = "%.1f kHz".format(sampleRateHz / 1000.0)

    val formattedBitrate: String
        get() = "$bitrateKbps kbps"

    val hiResBadge: String
        get() = when {
            format == AudioFormat.FLAC || format == AudioFormat.ALAC || format == AudioFormat.WAV ->
                "${format.name} $bitDepth-bit / ${formattedSampleRate}"
            else ->
                "${format.name} ${bitrateKbps}k"
        }
}
