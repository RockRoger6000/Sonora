package com.example.playback

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

object SampleAudioGenerator {
    private const val SAMPLE_RATE = 44100
    private const val CHANNELS = 2
    private const val BITS_PER_SAMPLE = 16

    fun getOrCreateSampleWav(context: Context, filename: String, baseFrequency: Double = 440.0): String {
        val file = File(context.cacheDir, "$filename.wav")
        if (file.exists() && file.length() > 1000) {
            return file.absolutePath
        }

        try {
            val durationSeconds = 45 // 45 seconds loopable ambient music
            val totalSamples = SAMPLE_RATE * durationSeconds
            val byteRate = SAMPLE_RATE * CHANNELS * (BITS_PER_SAMPLE / 8)
            val dataSize = totalSamples * CHANNELS * (BITS_PER_SAMPLE / 8)
            val totalSize = 36 + dataSize

            FileOutputStream(file).use { fos ->
                val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
                header.put("RIFF".toByteArray())
                header.putInt(totalSize)
                header.put("WAVE".toByteArray())
                header.put("fmt ".toByteArray())
                header.putInt(16) // Subchunk1Size for PCM
                header.putShort(1) // AudioFormat PCM = 1
                header.putShort(CHANNELS.toShort())
                header.putInt(SAMPLE_RATE)
                header.putInt(byteRate)
                header.putShort((CHANNELS * (BITS_PER_SAMPLE / 8)).toShort())
                header.putShort(BITS_PER_SAMPLE.toShort())
                header.put("data".toByteArray())
                header.putInt(dataSize)

                fos.write(header.array())

                // Generate pleasant ambient chords with smooth harmonics
                val bufferSize = 8192
                val buffer = ByteBuffer.allocate(bufferSize).order(ByteOrder.LITTLE_ENDIAN)

                val freqs = listOf(baseFrequency, baseFrequency * 1.25, baseFrequency * 1.5, baseFrequency * 1.875)

                for (i in 0 until totalSamples) {
                    val time = i.toDouble() / SAMPLE_RATE
                    val chordIdx = ((time / 3.0).toInt()) % freqs.size
                    val f = freqs[chordIdx]

                    // Smooth envelope and subtle vibrato
                    val envelope = (0.5 + 0.5 * sin(2.0 * Math.PI * 0.33 * time))
                    val wave = (sin(2.0 * Math.PI * f * time) * 0.6 +
                                sin(2.0 * Math.PI * (f * 2) * time) * 0.25 +
                                sin(2.0 * Math.PI * (f * 0.5) * time) * 0.15) * envelope

                    val sampleValue = (wave * 14000).toInt().coerceIn(-32767, 32767).toShort()

                    if (buffer.remaining() < 4) {
                        fos.write(buffer.array(), 0, buffer.position())
                        buffer.clear()
                    }

                    // Left channel & Right channel
                    buffer.putShort(sampleValue)
                    buffer.putShort((sampleValue * 0.95).toInt().toShort())
                }

                if (buffer.position() > 0) {
                    fos.write(buffer.array(), 0, buffer.position())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file.absolutePath
    }
}
