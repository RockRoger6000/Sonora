package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.playback.BandInfo
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SonoraCyan
import com.example.ui.theme.SonoraGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSheet(
    isEnabled: Boolean,
    bands: List<BandInfo>,
    currentPreset: String,
    bassBoostStrength: Short,
    spectrumAmplitudes: List<Float>,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectPreset: (String) -> Unit,
    onBandChange: (Short, Short) -> Unit,
    onBassBoostChange: (Short) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val presets = listOf(
        "Flat", "Rock", "Pop", "Jazz", "Classical",
        "Hip Hop", "Heavy Metal", "Acoustic", "Electronic", "Bass Heavy"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("equalizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = SonoraGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.equalizer_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.equalizer_enabled),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isEnabled) SonoraCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onToggleEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SonoraGold,
                            checkedTrackColor = SonoraGold.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("equalizer_switch")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Visualizer Spectrum Preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                spectrumAmplitudes.forEach { amp ->
                    val barHeight = (amp * 36).dp.coerceIn(4.dp, 36.dp)
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (isEnabled) SonoraGold else MaterialTheme.colorScheme.outline)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Horizontal Chips
            Text(
                text = stringResource(R.string.equalizer_preset),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val selected = currentPreset == preset
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectPreset(preset) },
                        label = { Text(preset) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SonoraGold,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Frequency Bands Sliders
            Text(
                text = "Frequency Bands (Gain dB)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            bands.forEach { band ->
                val minDb = band.minLevel / 100f
                val maxDb = band.maxLevel / 100f
                val currentDb = band.levelMillibels / 100f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = band.frequencyLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Slider(
                        value = currentDb,
                        onValueChange = { newVal ->
                            val millibels = (newVal * 100).toInt().toShort()
                            onBandChange(band.index, millibels)
                        },
                        valueRange = minDb..maxDb,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("eq_band_${band.index}"),
                        enabled = isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = SonoraGold,
                            activeTrackColor = SonoraGold,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )

                    Text(
                        text = "${if (band.gainDb >= 0) "+" else ""}${band.gainDb} dB",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (band.gainDb != 0) HiResGold else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.width(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bass Boost Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.width(100.dp)) {
                    Text(
                        text = stringResource(R.string.bass_boost_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${bassBoostStrength / 10}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SonoraCyan
                    )
                }

                Slider(
                    value = bassBoostStrength.toFloat(),
                    onValueChange = { onBassBoostChange(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bass_boost_slider"),
                    enabled = isEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = SonoraCyan,
                        activeTrackColor = SonoraCyan,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
