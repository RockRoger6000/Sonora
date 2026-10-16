package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SonoraGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SleepTimerDialog(
    secondsLeft: Long,
    fadeActive: Boolean,
    onStartTimer: (Int, Boolean) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(30) }
    var fadeVolume by remember { mutableStateOf(fadeActive) }

    val options = listOf(5, 15, 30, 45, 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                tint = SonoraGold,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.sleep_timer_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (secondsLeft > 0) {
                    val mins = secondsLeft / 60
                    val secs = secondsLeft % 60
                    val formatted = "%02d:%02d".format(mins, secs)
                    Text(
                        text = stringResource(R.string.sleep_timer_active, formatted),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SonoraGold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Text(
                    text = "Select Duration",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { minutes ->
                        FilterChip(
                            selected = selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes },
                            label = { Text("$minutes min") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SonoraGold,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gradual volume fade toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.sleep_timer_fade_label),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = fadeVolume,
                        onCheckedChange = { fadeVolume = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SonoraGold,
                            checkedTrackColor = SonoraGold.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStartTimer(selectedMinutes, fadeVolume)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SonoraGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_start_sleep_timer")
            ) {
                Text(
                    text = if (secondsLeft > 0) "Update" else "Start Timer",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Row {
                if (secondsLeft > 0) {
                    OutlinedButton(
                        onClick = {
                            onCancelTimer()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.cancel_timer))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
