package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.NeonEmerald

@Composable
fun SettingsScreen(
  currentTheme: String,
  onThemeChanged: (String) -> Unit,
  onReviewPrivacy: () -> Unit,
  onResetProgress: () -> Unit
) {
  var soundEnabled by remember { mutableStateOf(true) }
  var hapticsEnabled by remember { mutableStateOf(true) }
  var showResetDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text("Settings & Lab Configuration", fontWeight = FontWeight.Bold, fontSize = 20.sp)

    // Visual Theme Selector
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Palette, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
          Text("Laboratory Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = currentTheme == "dark",
            onClick = { onThemeChanged("dark") },
            label = { Text("Cyber Dark") },
            leadingIcon = { Icon(Icons.Default.Brightness4, null, Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = currentTheme == "light",
            onClick = { onThemeChanged("light") },
            label = { Text("Clean Light") },
            leadingIcon = { Icon(Icons.Default.Brightness7, null, Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = currentTheme == "system",
            onClick = { onThemeChanged("system") },
            label = { Text("System") },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Audio & Haptic Clicks
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Apparatus Feedback", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.VolumeUp, null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
            Column {
              Text("Sound Effects", fontWeight = FontWeight.Medium, fontSize = 13.sp)
              Text("Switch toggles, burner roar, meter hum", fontSize = 11.sp, color = Color.Gray)
            }
          }
          Switch(
            checked = soundEnabled,
            onCheckedChange = { soundEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Vibration, null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
            Column {
              Text("Haptic Feedback", fontWeight = FontWeight.Medium, fontSize = 13.sp)
              Text("Tactile clicks when turning knobs & burette valves", fontSize = 11.sp, color = Color.Gray)
            }
          }
          Switch(
            checked = hapticsEnabled,
            onCheckedChange = { hapticsEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
          )
        }
      }
    }

    // Privacy & Security
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Security, null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
          Text("Privacy & Security Commitment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Text(
          "POWER LAB is completely offline and does not collect or transmit personal information. Review permissions and security guarantees anytime.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )

        OutlinedButton(
          onClick = onReviewPrivacy,
          modifier = Modifier.fillMaxWidth().height(38.dp)
        ) {
          Icon(Icons.Default.Lock, null, Modifier.size(16.dp))
          Spacer(Modifier.width(6.dp))
          Text("Review Privacy Policy & Permissions", fontSize = 12.sp)
        }
      }
    }

    // Reset Progress
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.DeleteForever, null, tint = LaserCrimson, modifier = Modifier.size(20.dp))
          Text("Reset Laboratory State", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LaserCrimson)
        }

        Text(
          "Clear all completed experiments, unlocked equipment, coins, and earned badges to start fresh.",
          fontSize = 12.sp,
          color = Color(0xFF94A3B8)
        )

        Button(
          onClick = { showResetDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = LaserCrimson),
          modifier = Modifier.fillMaxWidth().height(38.dp).testTag("reset_progress_button")
        ) {
          Text("Reset All Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // App Information & Syllabus
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Info, null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
          Text("POWER LAB v1.0.0", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
        }
        Text(
          "Engineered for high performance 2D science simulation on Android 9+. Covers full Class 12 Physics & Chemistry practical syllabus including real-time formulas, apparatus interactions, and organic compound construction.",
          fontSize = 11.sp,
          color = Color(0xFF94A3B8),
          lineHeight = 16.sp
        )
      }
    }
  }

  // Confirmation Alert Dialog
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("Reset Laboratory Progress?", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to reset all earned XP, coins, and completed experiments? This cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            showResetDialog = false
            onResetProgress()
          },
          colors = ButtonDefaults.buttonColors(containerColor = LaserCrimson)
        ) {
          Text("Reset")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
