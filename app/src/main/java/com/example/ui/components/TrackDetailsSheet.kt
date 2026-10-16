package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Track
import com.example.ui.theme.HiResGold
import com.example.ui.theme.HiResTagBackground
import com.example.ui.theme.SonoraGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsSheet(
    track: Track,
    onShareInstagram: (Track) -> Unit,
    onShareGeneral: (Track) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("track_details_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = HiResGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.track_info_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hi-Res Audio Certificate Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, HiResGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                HiResTagBackground,
                                Color(0x1A0F172A)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = HiResGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "HI-RES",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (track.format.isLossless) "Studio Master Lossless Audio" else "Standard Compressed Audio",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = track.hiResBadge,
                            style = MaterialTheme.typography.bodySmall,
                            color = HiResGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Specifications Rows
            SpecRow(label = stringResource(R.string.info_format), value = track.format.displayName)
            SpecRow(label = stringResource(R.string.info_bitrate), value = track.formattedBitrate)
            SpecRow(label = stringResource(R.string.info_samplerate), value = track.formattedSampleRate)
            SpecRow(label = stringResource(R.string.info_bitdepth), value = "${track.bitDepth}-bit PCM")
            SpecRow(label = stringResource(R.string.info_channels), value = if (track.channels == 2) "Stereo (2.0)" else "Mono")
            SpecRow(label = stringResource(R.string.info_decoder), value = stringResource(R.string.info_decoder_val))
            SpecRow(label = stringResource(R.string.info_year), value = track.year.toString())
            SpecRow(label = stringResource(R.string.info_album), value = track.album)
            SpecRow(label = stringResource(R.string.info_artist), value = track.artist)
            SpecRow(label = stringResource(R.string.info_filesize), value = "%.1f MB".format(track.fileSize / (1024f * 1024f)))

            Spacer(modifier = Modifier.height(20.dp))

            // Social Sharing Actions
            Text(
                text = "Social Sharing",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instagram Story Button
            Button(
                onClick = { onShareInstagram(track) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_share_instagram"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF833AB4),
                                    Color(0xFFFD1D1D),
                                    Color(0xFFFCB045)
                                )
                            )
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.share_instagram),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // General Share Button
            OutlinedButton(
                onClick = { onShareGeneral(track) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_share_general"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.share_general))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.5.dp)
}
