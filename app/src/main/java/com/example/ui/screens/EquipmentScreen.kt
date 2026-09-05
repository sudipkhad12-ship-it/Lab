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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EquipmentItem
import com.example.data.LabDomain
import com.example.data.LabRepository
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun EquipmentScreen(
  userCoins: Int,
  unlockedEquipment: Set<String>,
  onUnlockEquipment: (EquipmentItem) -> Boolean,
  onUpgradeEquipment: (EquipmentItem) -> Unit
) {
  var calibratedItems by remember { mutableStateOf(setOf<String>()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header with Science Coins balance
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      shape = RoundedCornerShape(14.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Laboratory Store & Calibration", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
          Text("Unlock higher precision instruments", fontSize = 12.sp, color = Color(0xFF94A3B8))
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomicAmber.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AtomicAmber, modifier = Modifier.size(18.dp))
          Text("$userCoins", fontWeight = FontWeight.Black, fontSize = 15.sp, color = AtomicAmber)
        }
      }
    }

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(LabRepository.equipmentCatalog) { item ->
        val isUnlocked = unlockedEquipment.contains(item.id)
        val isCalibrated = calibratedItems.contains(item.id)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("equipment_item_${item.id}"),
          colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFF0F172A)
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) ElectricCyan.copy(alpha = 0.15f) else Color(0xFF334155)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isUnlocked) Icons.Default.Speed else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) ElectricCyan else Color.Gray,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column {
                  Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(
                    text = "${if (item.domain == LabDomain.PHYSICS) "Physics" else "Chemistry"} • Level ${item.level} • ${item.specSummary}",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }
              }

              if (isUnlocked) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text("UNLOCKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                }
              }
            }

            Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Unlock / Calibrate Controls
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (!isUnlocked) {
                Button(
                  onClick = { onUnlockEquipment(item) },
                  colors = ButtonDefaults.buttonColors(containerColor = AtomicAmber),
                  enabled = userCoins >= item.unlockCost,
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Icon(Icons.Default.MonetizationOn, null, Modifier.size(14.dp), tint = Color(0xFF002917))
                  Spacer(Modifier.width(4.dp))
                  Text("Unlock (${item.unlockCost} Coins)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF002917))
                }
              } else {
                OutlinedButton(
                  onClick = {
                    calibratedItems = calibratedItems + item.id
                  },
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Icon(if (isCalibrated) Icons.Default.Check else Icons.Default.Tune, null, Modifier.size(14.dp))
                  Spacer(Modifier.width(4.dp))
                  Text(if (isCalibrated) "Calibrated" else "Calibrate Instrument", fontSize = 11.sp)
                }

                Button(
                  onClick = { onUpgradeEquipment(item) },
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Icon(Icons.Default.Upgrade, null, Modifier.size(14.dp), tint = Color(0xFF00363F))
                  Spacer(Modifier.width(4.dp))
                  Text("Upgrade Precision", fontSize = 11.sp, color = Color(0xFF00363F))
                }
              }
            }
          }
        }
      }
    }
  }
}
