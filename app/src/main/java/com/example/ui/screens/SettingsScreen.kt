package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppThemeMode
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SonoraCyan
import com.example.ui.theme.SonoraGold

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    language: String,
    syncStatusMessage: String?,
    onSelectTheme: (AppThemeMode) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onTriggerSync: () -> Unit,
    onTriggerRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var gaplessEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Text(
            text = stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance & Themes Card
            item {
                SettingsCard(
                    title = stringResource(R.string.settings_appearance),
                    icon = Icons.Default.Palette
                ) {
                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val themes = listOf(
                        AppThemeMode.MIDNIGHT_DARK to stringResource(R.string.theme_midnight),
                        AppThemeMode.AMOLED_DARK to stringResource(R.string.theme_amoled),
                        AppThemeMode.STUDIO_LIGHT to stringResource(R.string.theme_light),
                        AppThemeMode.SYSTEM to stringResource(R.string.theme_system)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        themes.forEach { (mode, label) ->
                            val isSelected = themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectTheme(mode) },
                                label = { Text(label) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chip_theme_${mode.name}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SonoraGold,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Language / Bahasa Card
            item {
                SettingsCard(
                    title = stringResource(R.string.settings_language),
                    icon = Icons.Default.Language
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = language == "en",
                            onClick = { onSelectLanguage("en") },
                            label = { Text(stringResource(R.string.lang_english)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_lang_en"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SonoraGold,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = language == "in",
                            onClick = { onSelectLanguage("in") },
                            label = { Text(stringResource(R.string.lang_indonesian)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_lang_in"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SonoraGold,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Audio Engine & Hardware DSP
            item {
                SettingsCard(
                    title = stringResource(R.string.settings_audio_engine),
                    icon = Icons.Default.Speaker
                ) {
                    Text(
                        text = "Android Native MediaCodec",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.native_decoder_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.gapless_playback),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = gaplessEnabled,
                            onCheckedChange = { gaplessEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SonoraGold,
                                checkedTrackColor = SonoraGold.copy(alpha = 0.4f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Shortcuts to Equalizer and Sleep Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenEqualizer,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_settings_eq"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = SonoraGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.equalizer_title), fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenSleepTimer,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_settings_sleep_timer"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = SonoraGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.sleep_timer_title), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Cross-Platform Cloud Sync Card
            item {
                SettingsCard(
                    title = stringResource(R.string.cloud_sync_title),
                    icon = Icons.Default.CloudSync
                ) {
                    Text(
                        text = stringResource(R.string.cloud_sync_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (syncStatusMessage != null) {
                        Text(
                            text = syncStatusMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SonoraCyan
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTriggerSync,
                            colors = ButtonDefaults.buttonColors(containerColor = SonoraGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_cloud_sync_now")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_sync_now), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onTriggerRestore,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_cloud_restore")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_restore))
                        }
                    }
                }
            }

            // About Card
            item {
                SettingsCard(
                    title = stringResource(R.string.about_sonora),
                    icon = Icons.Default.Speaker
                ) {
                    Text(
                        text = stringResource(R.string.version_info),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SonoraGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Native 24-bit/192kHz audio engine for FLAC, ALAC, WAV, and MP3. Built with Jetpack Compose & Android Hardware DSP.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SonoraGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
