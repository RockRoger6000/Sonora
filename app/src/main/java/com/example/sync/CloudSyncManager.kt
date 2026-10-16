package com.example.sync

import android.content.Context
import com.example.database.FavoriteEntity
import com.example.database.PlaylistEntity
import com.example.database.PlaylistTrackEntity
import com.example.database.SonoraDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CloudSyncManager {

    suspend fun performCloudSync(context: Context, database: SonoraDatabase): String = withContext(Dispatchers.IO) {
        val dao = database.musicDao()
        val favorites = dao.getAllFavoriteIds().first()
        val playlists = dao.getAllPlaylists().first()

        val syncPayload = JSONObject()
        syncPayload.put("version", 1)
        syncPayload.put("timestamp", System.currentTimeMillis())
        syncPayload.put("clientPlatform", "Android")

        val favArray = JSONArray()
        favorites.forEach { favArray.put(it) }
        syncPayload.put("favorites", favArray)

        val plArray = JSONArray()
        for (pl in playlists) {
            val plObj = JSONObject()
            plObj.put("name", pl.name)
            val trackIds = dao.getTracksForPlaylist(pl.id).first()
            val tracksArr = JSONArray()
            trackIds.forEach { tracksArr.put(it) }
            plObj.put("tracks", tracksArr)
            plArray.put(plObj)
        }
        syncPayload.put("playlists", plArray)

        // Save local backup file for offline / cross-platform export
        val backupFile = File(context.filesDir, "sonora_cloud_sync_backup.json")
        backupFile.writeText(syncPayload.toString(2))

        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        dateFormat.format(Date())
    }

    suspend fun restoreFromBackup(context: Context, database: SonoraDatabase): Boolean = withContext(Dispatchers.IO) {
        val backupFile = File(context.filesDir, "sonora_cloud_sync_backup.json")
        if (!backupFile.exists()) return@withContext false

        try {
            val json = JSONObject(backupFile.readText())
            val dao = database.musicDao()

            val favArray = json.optJSONArray("favorites")
            if (favArray != null) {
                for (i in 0 until favArray.length()) {
                    val trackId = favArray.getLong(i)
                    dao.addFavorite(FavoriteEntity(trackId))
                }
            }

            val plArray = json.optJSONArray("playlists")
            if (plArray != null) {
                for (i in 0 until plArray.length()) {
                    val plObj = plArray.getJSONObject(i)
                    val name = plObj.getString("name")
                    val plId = dao.insertPlaylist(PlaylistEntity(name = name))
                    val tracksArr = plObj.optJSONArray("tracks")
                    if (tracksArr != null) {
                        for (j in 0 until tracksArr.length()) {
                            val trackId = tracksArr.getLong(j)
                            dao.addTrackToPlaylist(PlaylistTrackEntity(plId, trackId))
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
