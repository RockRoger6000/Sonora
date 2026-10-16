package com.example.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.SonoraApplication
import com.example.model.Track

class SonoraPlaybackService : Service() {

    companion object {
        const val CHANNEL_ID = "sonora_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.example.sonora.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.sonora.ACTION_PAUSE"
        const val ACTION_PLAY_PAUSE = "com.example.sonora.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.sonora.ACTION_NEXT"
        const val ACTION_PREV = "com.example.sonora.ACTION_PREV"
        const val ACTION_STOP = "com.example.sonora.ACTION_STOP"

        fun updateNotification(context: Context, track: Track?, isPlaying: Boolean) {
            val intent = Intent(context, SonoraPlaybackService::class.java).apply {
                putExtra("IS_PLAYING", isPlaying)
                putExtra("TRACK_TITLE", track?.title ?: "No Track Playing")
                putExtra("TRACK_ARTIST", track?.artist ?: "Sonora Hi-Res Audio")
                putExtra("TRACK_BADGE", track?.hiResBadge ?: "")
                putExtra("DRAWABLE_RES", track?.drawableResId ?: R.drawable.sonora_launcher_icon_1792130927600)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // Background start restriction fallback
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val engine = (application as? SonoraApplication)?.audioEngine

        when (action) {
            ACTION_PLAY_PAUSE -> engine?.togglePlayPause()
            ACTION_PLAY -> engine?.resume()
            ACTION_PAUSE -> engine?.pause()
            ACTION_NEXT -> engine?.skipNext()
            ACTION_PREV -> engine?.skipPrevious()
            ACTION_STOP -> {
                engine?.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }

        val isPlaying = intent?.getBooleanExtra("IS_PLAYING", engine?.isPlaying?.value ?: false) ?: false
        val title = intent?.getStringExtra("TRACK_TITLE") ?: engine?.currentTrack?.value?.title ?: "Sonora Player"
        val artist = intent?.getStringExtra("TRACK_ARTIST") ?: engine?.currentTrack?.value?.artist ?: "Hi-Res Audio"
        val badge = intent?.getStringExtra("TRACK_BADGE") ?: engine?.currentTrack?.value?.hiResBadge ?: ""
        val drawableRes = intent?.getIntExtra("DRAWABLE_RES", R.drawable.sonora_launcher_icon_1792130927600) ?: R.drawable.sonora_launcher_icon_1792130927600

        val notification = buildNotification(title, "$artist • $badge", isPlaying, drawableRes)
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun buildNotification(title: String, subtitle: String, isPlaying: Boolean, drawableRes: Int): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val prevIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, SonoraPlaybackService::class.java).apply { action = ACTION_PREV },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, SonoraPlaybackService::class.java).apply { action = ACTION_PLAY_PAUSE },
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, SonoraPlaybackService::class.java).apply { action = ACTION_NEXT },
            PendingIntent.FLAG_IMMUTABLE
        )

        val largeIconBitmap = try {
            BitmapFactory.decodeResource(resources, drawableRes)
        } catch (_: Exception) {
            null
        }

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.sonora_launcher_icon_1792130927600)
            .setLargeIcon(largeIconBitmap)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevIntent)
            .addAction(playPauseIcon, playPauseTitle, playPauseIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sonora Hi-Res Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent playback controls on lockscreen and status bar"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
