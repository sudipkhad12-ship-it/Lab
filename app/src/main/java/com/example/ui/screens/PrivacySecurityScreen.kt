package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun PrivacySecurityScreen(
  onAccepted: () -> Unit
) {
  var isAgreed by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF090D16), Color(0xFF111827), Color(0xFF0F172A))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NeonEmerald.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = NeonEmerald,
            modifier = Modifier.size(26.dp)
          )
        }

        Column {
          Text(
            text = "Privacy & Security",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Data Safety & Permissions Commitment",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )
        }
      }

      // Security Guarantee Card
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Lock, null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            Text("100% Offline-First Safety", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
          }
          Text(
            text = "POWER LAB does not collect, transmit, or share personal data with external servers. All experiment configurations, molecule models, and game achievements are stored strictly on your local device.",
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 18.sp
          )
        }
      }

      // Permission Management Breakdown
      Text("Required & Optional Device Permissions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)

      PermissionItemCard(
        title = "Local App Storage",
        purpose = "Saves your laboratory progress, experiment completions, coins, unlocked apparatus, and customized molecule designs directly in device app storage.",
        icon = Icons.Default.Security,
        iconTint = ElectricCyan
      )

      PermissionItemCard(
        title = "Haptic Vibration (Optional)",
        purpose = "Provides subtle tactile clicks when rotating instrument knobs, flipping high-voltage switches, and opening burette valves for realistic feedback.",
        icon = Icons.Default.Vibration,
        iconTint = NeonEmerald
      )

      PermissionItemCard(
        title = "Zero Private File Access",
        purpose = "POWER LAB will never access, upload, share, or modify your personal files, camera, microphone, or contacts. Fully compliant with Google Play Data Safety policies.",
        icon = Icons.Default.VisibilityOff,
        iconTint = Color(0xFFF59E0B)
      )

      // Terms Summary
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF334155))),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("Terms of Use Summary", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
          Text(
            text = "• Educational simulation purposes only; calculations match standard Class 12 physics & chemistry formulas.\n• You can reset or clear all saved laboratory progress anytime in the Settings tab.\n• You may review this policy at any time from Settings.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            lineHeight = 16.sp
          )
        }
      }

      Spacer(Modifier.height(4.dp))

      // Acceptance Checkbox
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1E293B).copy(alpha = 0.7f))
          .padding(8.dp)
      ) {
        Checkbox(
          checked = isAgreed,
          onCheckedChange = { isAgreed = it },
          colors = CheckboxDefaults.colors(checkedColor = NeonEmerald),
          modifier = Modifier.testTag("privacy_agreement_checkbox")
        )
        Text(
          text = "I agree to the Privacy Policy, Terms & Conditions, and Data Safety terms.",
          fontSize = 12.sp,
          color = Color.White,
          fontWeight = FontWeight.Medium
        )
      }

      // Enter Lab Button
      Button(
        onClick = onAccepted,
        enabled = isAgreed,
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonEmerald,
          disabledContainerColor = Color(0xFF334155)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("enter_laboratory_button")
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isAgreed) Color(0xFF002917) else Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(
          text = "Accept & Enter Laboratory",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = if (isAgreed) Color(0xFF002917) else Color.Gray
        )
      }
    }
  }
}

@Composable
private fun PermissionItemCard(
  title: String,
  purpose: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp).padding(top = 2.dp))
      Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
        Spacer(Modifier.height(2.dp))
        Text(purpose, fontSize = 11.sp, color = Color(0xFF94A3B8), lineHeight = 16.sp)
      }
    }
  }
}
