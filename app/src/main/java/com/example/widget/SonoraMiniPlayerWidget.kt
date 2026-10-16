package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.SonoraApplication
import com.example.model.Track
import com.example.playback.SonoraPlaybackService

class SonoraMiniPlayerWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val app = context.applicationContext as? SonoraApplication
        val track = app?.audioEngine?.currentTrack?.value
        val isPlaying = app?.audioEngine?.isPlaying?.value ?: false

        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId, track, isPlaying)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        val app = context.applicationContext as? SonoraApplication
        val engine = app?.audioEngine

        when (action) {
            "com.example.sonora.ACTION_PLAY_PAUSE" -> engine?.togglePlayPause()
            "com.example.sonora.ACTION_NEXT" -> engine?.skipNext()
            "com.example.sonora.ACTION_PREV" -> engine?.skipPrevious()
        }
    }

    companion object {
        fun updateWidget(context: Context, track: Track?, isPlaying: Boolean) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, SonoraMiniPlayerWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (allWidgetIds.isNotEmpty()) {
                for (widgetId in allWidgetIds) {
                    updateAppWidget(context, appWidgetManager, widgetId, track, isPlaying)
                }
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            track: Track?,
            isPlaying: Boolean
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_sonora_mini_player)

            val title = track?.title ?: context.getString(R.string.app_name)
            val artistInfo = if (track != null) {
                "${track.artist} • ${track.hiResBadge}"
            } else {
                "Hi-Res Audio Player"
            }

            views.setTextViewText(R.id.widget_track_title, title)
            views.setTextViewText(R.id.widget_track_artist, artistInfo)

            val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            views.setImageViewResource(R.id.widget_btn_play_pause, playPauseIcon)

            val artRes = track?.drawableResId ?: R.drawable.sonora_launcher_icon_1792130927600
            views.setImageViewResource(R.id.widget_album_art, artRes)

            // Open app intent
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

            // Play/Pause intent
            val playPauseIntent = Intent(context, SonoraMiniPlayerWidget::class.java).apply {
                action = "com.example.sonora.ACTION_PLAY_PAUSE"
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context, 1, playPauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_btn_play_pause, playPausePendingIntent)

            // Next intent
            val nextIntent = Intent(context, SonoraMiniPlayerWidget::class.java).apply {
                action = "com.example.sonora.ACTION_NEXT"
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_btn_next, nextPendingIntent)

            // Prev intent
            val prevIntent = Intent(context, SonoraMiniPlayerWidget::class.java).apply {
                action = "com.example.sonora.ACTION_PREV"
            }
            val prevPendingIntent = PendingIntent.getBroadcast(
                context, 3, prevIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_btn_prev, prevPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
