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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LabRepository
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet

@Composable
fun AchievementsScreen(
  userXp: Int,
  userLevel: Int,
  userRank: String,
  userCoins: Int,
  unlockedAchievements: Set<String>
) {
  // Compute progress to next level
  val xpInCurrentLevel = userXp % 200
  val levelProgress = (xpInCurrentLevel / 200f).coerceIn(0f, 1f)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Rank & XP Banner Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
            )
          )
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PlasmaViolet.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = PlasmaViolet, modifier = Modifier.size(28.dp))
            }

            Column {
              Text("RANK: $userRank", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
              Text("Laboratory Level $userLevel", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(AtomicAmber.copy(alpha = 0.2f))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.MonetizationOn, null, tint = AtomicAmber, modifier = Modifier.size(16.dp))
            Text("$userCoins", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AtomicAmber)
          }
        }

        // XP Progress bar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Experience Points (XP)", fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text("$userXp Total XP ($xpInCurrentLevel / 200 to Lvl ${userLevel + 1})", fontSize = 11.sp, color = ElectricCyan, fontWeight = FontWeight.SemiBold)
          }

          LinearProgressIndicator(
            progress = { levelProgress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = ElectricCyan,
            trackColor = Color(0xFF334155),
          )
        }
      }
    }

    // Badges & Honors Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Laboratory Honors & Medals", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Text("${unlockedAchievements.size} / ${LabRepository.achievements.size} Unlocked", fontSize = 12.sp, color = NeonEmerald)
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(LabRepository.achievements) { ach ->
        val isUnlocked = unlockedAchievements.contains(ach.id)

        Card(
          colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFF0F172A)
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("achievement_badge_${ach.id}")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) NeonEmerald.copy(alpha = 0.2f) else Color(0xFF334155)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) NeonEmerald else Color.Gray,
                modifier = Modifier.size(22.dp)
              )
            }

            Text(
              text = ach.title,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              textAlign = TextAlign.Center
            )

            Text(
              text = ach.description,
              fontSize = 10.sp,
              color = Color.Gray,
              textAlign = TextAlign.Center,
              lineHeight = 14.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.MonetizationOn, null, tint = AtomicAmber, modifier = Modifier.size(12.dp))
              Text("+${ach.coinReward} Coins", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AtomicAmber)
            }
          }
        }
      }
    }
  }
}
