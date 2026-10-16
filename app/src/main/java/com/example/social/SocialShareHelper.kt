package com.example.social

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.R
import com.example.model.Track

object SocialShareHelper {

    fun shareTrackToInstagram(context: Context, track: Track) {
        val shareText = context.getString(
            R.string.share_message,
            track.title,
            track.artist
        ) + " [${track.hiResBadge}]"

        val instaIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            type = "image/*"
            putExtra("content_url", "https://sonora.audio/track/${track.id}")
            putExtra("top_background_color", "#0F172A")
            putExtra("bottom_background_color", "#0284C7")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            val packageManager = context.packageManager
            val activities = packageManager.queryIntentActivities(instaIntent, 0)
            if (activities.isNotEmpty()) {
                context.startActivity(instaIntent)
                return
            }
        } catch (_: Exception) {}

        // Fallback to standard share sheet
        shareTrackGeneral(context, track)
    }

    fun shareTrackGeneral(context: Context, track: Track) {
        val shareText = context.getString(
            R.string.share_message,
            track.title,
            track.artist
        ) + "\n\nFormat: ${track.format.displayName} ${track.bitDepth}-bit / ${track.formattedSampleRate} (${track.formattedBitrate})"

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Now Playing on Sonora: ${track.title}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(sendIntent, context.getString(R.string.share_general))
        try {
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No sharing app found", Toast.LENGTH_SHORT).show()
        }
    }
}
