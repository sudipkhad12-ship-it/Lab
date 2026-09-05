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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChallengeModel
import com.example.data.LabRepository
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun ChallengesScreen(
  completedChallenges: Set<String>,
  onClaimChallenge: (ChallengeModel) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
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
          Text("Laboratory Target Missions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
          Text("${completedChallenges.size} of ${LabRepository.challenges.size} Completed", fontSize = 12.sp, color = NeonEmerald)
        }

        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AtomicAmber, modifier = Modifier.size(28.dp))
      }
    }

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(LabRepository.challenges) { ch ->
        val isDone = completedChallenges.contains(ch.id)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_item_${ch.id}"),
          colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFF0F172A) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDone) NeonEmerald.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Flag,
                    contentDescription = null,
                    tint = if (isDone) NeonEmerald else ElectricCyan,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Text(ch.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }

              // Bounty
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                  Icon(Icons.Default.Stars, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                  Text("+${ch.xpReward} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                  Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AtomicAmber, modifier = Modifier.size(14.dp))
                  Text("+${ch.coinsReward}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AtomicAmber)
                }
              }
            }

            Text(ch.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Progress bar
            LinearProgressIndicator(
              progress = { if (isDone) 1f else 0.5f },
              modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
              color = if (isDone) NeonEmerald else ElectricCyan,
              trackColor = Color(0xFF334155),
            )

            // Claim Action
            Button(
              onClick = { onClaimChallenge(ch) },
              colors = ButtonDefaults.buttonColors(containerColor = if (isDone) Color(0xFF334155) else NeonEmerald),
              enabled = !isDone,
              modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, Modifier.size(14.dp))
              Spacer(Modifier.width(6.dp))
              Text(if (isDone) "Mission Completed" else "Verify & Claim Bounty", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}
