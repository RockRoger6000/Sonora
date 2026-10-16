package com.example.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.model.AudioFormat
import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AudioScanner {

    suspend fun scanDeviceAudio(context: Context): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                val mmr = MediaMetadataRetriever()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Title"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val durationMs = cursor.getLong(durationCol)
                    val year = cursor.getInt(yearCol).let { if (it > 0) it else 2024 }
                    val dateAdded = cursor.getLong(dateAddedCol) * 1000L
                    val albumId = cursor.getLong(albumIdCol)
                    val filePath = cursor.getString(dataCol) ?: ""

                    if (durationMs < 10000) continue // Skip short ringtones/alerts

                    val trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val albumArtUri = "content://media/external/audio/albumart/$albumId"

                    // Determine format from file extension or mime
                    val ext = filePath.substringAfterLast(".", "mp3")
                    val format = AudioFormat.fromExtension(ext)

                    // Estimate or extract bitrate and sample rate
                    var bitrate = when (format) {
                        AudioFormat.FLAC -> 1411
                        AudioFormat.ALAC -> 1200
                        AudioFormat.WAV -> 1411
                        AudioFormat.MP3 -> 320
                        AudioFormat.AAC -> 256
                        AudioFormat.OGG -> 192
                    }
                    var sampleRate = if (format.isHiRes) 96000 else 44100
                    var bitDepth = if (format.isHiRes) 24 else 16

                    try {
                        mmr.setDataSource(filePath)
                        val brStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                        if (!brStr.isNullOrEmpty()) {
                            bitrate = (brStr.toIntOrNull() ?: (bitrate * 1000)) / 1000
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val srStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                            if (!srStr.isNullOrEmpty()) {
                                sampleRate = srStr.toIntOrNull() ?: sampleRate
                            }
                            val bitsStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE)
                            if (!bitsStr.isNullOrEmpty()) {
                                bitDepth = bitsStr.toIntOrNull() ?: bitDepth
                            }
                        }
                    } catch (_: Exception) { }

                    tracks.add(
                        Track(
                            id = id,
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            durationMs = durationMs,
                            contentUri = trackUri,
                            albumArtUri = albumArtUri,
                            drawableResId = null,
                            year = year,
                            dateAdded = dateAdded,
                            bitrateKbps = bitrate,
                            sampleRateHz = sampleRate,
                            bitDepth = bitDepth,
                            channels = 2,
                            format = format,
                            fileSize = (durationMs * (bitrate / 8)).toLong(),
                            isFavorite = false,
                            genre = "Music"
                        )
                    )
                }
                mmr.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tracks
    }
}
