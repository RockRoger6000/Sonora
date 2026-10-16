package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AudioFormat
import com.example.model.SortOption
import com.example.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sonora", appName)
    }

    @Test
    fun `track hi res badge formatting`() {
        val flacTrack = Track(
            id = 1L,
            title = "Test Hi-Res",
            artist = "Audiophile Artist",
            album = "Master 96k",
            durationMs = 180000L,
            contentUri = "",
            year = 2024,
            bitrateKbps = 1411,
            sampleRateHz = 96000,
            bitDepth = 24,
            format = AudioFormat.FLAC
        )
        assertEquals("3:00", flacTrack.formattedDuration)
        assertEquals("96.0 kHz", flacTrack.formattedSampleRate)
        assertTrue(flacTrack.hiResBadge.contains("FLAC 24-bit"))
        assertTrue(flacTrack.format.isHiRes)
    }

    @Test
    fun `sorting by year released desc`() {
        val t1 = Track(1, "A", "Art", "Alb", 1000, "", year = 2020)
        val t2 = Track(2, "B", "Art", "Alb", 1000, "", year = 2024)
        val list = listOf(t1, t2).sortedByDescending { it.year }
        assertEquals(2024, list.first().year)
    }
}
