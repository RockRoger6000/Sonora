package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT trackId FROM favorites ORDER BY addedAt DESC")
    fun getAllFavoriteIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    fun isFavorite(trackId: Long): Flow<Boolean>

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt ASC")
    fun getTracksForPlaylist(playlistId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordHistory(history: HistoryEntity)

    @Query("SELECT trackId FROM playback_history ORDER BY playedAt DESC LIMIT 20")
    fun getRecentHistoryTrackIds(): Flow<List<Long>>
}
