package com.example.model

enum class AudioFormat(val displayName: String, val isLossless: Boolean, val isHiRes: Boolean) {
    FLAC("FLAC", isLossless = true, isHiRes = true),
    ALAC("ALAC", isLossless = true, isHiRes = true),
    WAV("WAV", isLossless = true, isHiRes = true),
    MP3("MP3", isLossless = false, isHiRes = false),
    AAC("AAC", isLossless = false, isHiRes = false),
    OGG("OGG", isLossless = false, isHiRes = false);

    companion object {
        fun fromExtension(ext: String): AudioFormat {
            return when (ext.lowercase()) {
                "flac" -> FLAC
                "m4a", "alac" -> ALAC
                "wav" -> WAV
                "mp3" -> MP3
                "aac" -> AAC
                "ogg" -> OGG
                else -> MP3
            }
        }
    }
}
