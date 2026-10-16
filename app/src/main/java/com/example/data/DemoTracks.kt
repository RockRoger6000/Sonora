package com.example.data

import android.content.Context
import com.example.R
import com.example.model.AudioFormat
import com.example.model.Track
import com.example.playback.SampleAudioGenerator

object DemoTracks {
    fun getDemoTracks(context: Context): List<Track> {
        val sample1 = SampleAudioGenerator.getOrCreateSampleWav(context, "aurora_jazz", 329.63) // E4
        val sample2 = SampleAudioGenerator.getOrCreateSampleWav(context, "cyber_pulse", 440.0) // A4
        val sample3 = SampleAudioGenerator.getOrCreateSampleWav(context, "crystal_reverie", 523.25) // C5
        val sample4 = SampleAudioGenerator.getOrCreateSampleWav(context, "sunset_boulevard", 392.0) // G4
        val sample5 = SampleAudioGenerator.getOrCreateSampleWav(context, "acoustic_serenade", 293.66) // D4
        val sample6 = SampleAudioGenerator.getOrCreateSampleWav(context, "coffee_shop_lofi", 349.23) // F4

        return listOf(
            Track(
                id = 101L,
                title = "Midnight Saxophone",
                artist = "Aurora & Julian Banks",
                album = "Smooth Jazz & Ambient Soul",
                durationMs = 214000L,
                contentUri = sample1,
                drawableResId = R.drawable.album_aurora_jazz_1792131031657,
                year = 2024,
                dateAdded = 1712000000000L,
                bitrateKbps = 1411,
                sampleRateHz = 96000,
                bitDepth = 24,
                channels = 2,
                format = AudioFormat.FLAC,
                fileSize = 48_500_000L,
                isFavorite = true,
                genre = "Smooth Jazz",
                playCount = 42
            ),
            Track(
                id = 102L,
                title = "Neon Skyline",
                artist = "Cyber Pulse",
                album = "Neon Waves",
                durationMs = 198000L,
                contentUri = sample2,
                drawableResId = R.drawable.album_cyber_pulse_1792131046953,
                year = 2023,
                dateAdded = 1708000000000L,
                bitrateKbps = 2304,
                sampleRateHz = 192000,
                bitDepth = 24,
                channels = 2,
                format = AudioFormat.ALAC,
                fileSize = 64_200_000L,
                isFavorite = true,
                genre = "Synthwave",
                playCount = 38
            ),
            Track(
                id = 103L,
                title = "Crystal Reverie",
                artist = "Elysian Orchestra",
                album = "Audiophile Reference Master Vol. 1",
                durationMs = 265000L,
                contentUri = sample3,
                drawableResId = R.drawable.sonora_launcher_icon_1792130927600,
                year = 2024,
                dateAdded = 1715000000000L,
                bitrateKbps = 4608,
                sampleRateHz = 96000,
                bitDepth = 24,
                channels = 2,
                format = AudioFormat.WAV,
                fileSize = 120_000_000L,
                isFavorite = false,
                genre = "Classical Symphony",
                playCount = 15
            ),
            Track(
                id = 104L,
                title = "Sunset Boulevard",
                artist = "Cyber Pulse",
                album = "Neon Waves",
                durationMs = 245000L,
                contentUri = sample4,
                drawableResId = R.drawable.album_cyber_pulse_1792131046953,
                year = 2022,
                dateAdded = 1690000000000L,
                bitrateKbps = 1280,
                sampleRateHz = 88200,
                bitDepth = 24,
                channels = 2,
                format = AudioFormat.FLAC,
                fileSize = 42_100_000L,
                isFavorite = false,
                genre = "Electronic",
                playCount = 20
            ),
            Track(
                id = 105L,
                title = "Acoustic Serenade",
                artist = "Mateo Silva",
                album = "Acoustic Strings Unplugged",
                durationMs = 182000L,
                contentUri = sample5,
                drawableResId = R.drawable.album_aurora_jazz_1792131031657,
                year = 2021,
                dateAdded = 1680000000000L,
                bitrateKbps = 940,
                sampleRateHz = 44100,
                bitDepth = 16,
                channels = 2,
                format = AudioFormat.FLAC,
                fileSize = 25_800_000L,
                isFavorite = true,
                genre = "Acoustic",
                playCount = 29
            ),
            Track(
                id = 106L,
                title = "Coffee Shop Lo-Fi",
                artist = "Komorebi Beats",
                album = "Chillhop Sessions",
                durationMs = 154000L,
                contentUri = sample6,
                drawableResId = R.drawable.sonora_launcher_icon_1792130927600,
                year = 2025,
                dateAdded = 1720000000000L,
                bitrateKbps = 320,
                sampleRateHz = 44100,
                bitDepth = 16,
                channels = 2,
                format = AudioFormat.MP3,
                fileSize = 8_200_000L,
                isFavorite = false,
                genre = "Lo-Fi",
                playCount = 12
            )
        )
    }
}
