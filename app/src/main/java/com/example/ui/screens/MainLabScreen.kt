package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChallengeModel
import com.example.data.LabDomain
import com.example.data.LabPreferences
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekBorderMuted
import com.example.ui.theme.SleekCyan
import com.example.ui.theme.SleekCyanDark
import com.example.ui.theme.SleekNavBg
import com.example.ui.theme.SleekPurpleChem
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceHeader
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekYellowEnergy
import kotlinx.coroutines.launch

data class NavTabItem(
  val title: String,
  val icon: ImageVector,
  val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLabScreen(
  prefs: LabPreferences,
  currentTheme: String,
  onThemeChanged: (String) -> Unit,
  onReviewPrivacy: () -> Unit
) {
  var activeTab by remember { mutableIntStateOf(0) }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  // Dynamic user progress
  var userXp by remember { mutableIntStateOf(prefs.xp) }
  var userCoins by remember { mutableIntStateOf(prefs.coins) }
  var userLevel by remember { mutableIntStateOf(prefs.level) }
  var completedExperiments by remember { mutableStateOf(prefs.getCompletedExperiments()) }
  var unlockedEquipment by remember { mutableStateOf(prefs.getUnlockedEquipment()) }
  var completedChallenges by remember { mutableStateOf(prefs.getCompletedChallenges()) }
  var unlockedAchievements by remember { mutableStateOf(prefs.getUnlockedAchievements()) }

  val tabs = listOf(
    NavTabItem("Lab", Icons.Default.Science, "nav_tab_lab"),
    NavTabItem("Experiments", Icons.Default.Science, "nav_tab_experiments"),
    NavTabItem("Molecules", Icons.Default.Hub, "nav_tab_molecules"),
    NavTabItem("Equipment", Icons.Default.Build, "nav_tab_equipment"),
    NavTabItem("Missions", Icons.Default.Flag, "nav_tab_challenges"),
    NavTabItem("Honors", Icons.Default.EmojiEvents, "nav_tab_achievements"),
    NavTabItem("Config", Icons.Default.Settings, "nav_tab_settings")
  )

  fun grantReward(xp: Int, coins: Int, message: String? = null) {
    userXp += xp
    userCoins += coins
    userLevel = (userXp / 250) + 1
    prefs.xp = userXp
    prefs.coins = userCoins

    coroutineScope.launch {
      val text = message ?: "+$xp XP  •  +$coins Coins Earned!"
      snackbarHostState.showSnackbar(text)
    }
  }

  Scaffold(
    containerColor = SleekBg,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      // Sleek Interface Header: px-4 py-3 bg-slate-900 border-b border-slate-800
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SleekSurfaceHeader)
      ) {
        TopAppBar(
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SleekSurfaceHeader,
            titleContentColor = SleekTextPrimary
          ),
          title = {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Left Brand & Level Progress
              Column {
                Text(
                  text = "POWER LAB",
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = SleekCyan,
                  letterSpacing = 0.5.sp,
                  lineHeight = 20.sp
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.padding(top = 3.dp)
                ) {
                  // Mini Sleek Progress Bar: w-20 h-1.5 bg-slate-800 rounded-full
                  Box(
                    modifier = Modifier
                      .width(68.dp)
                      .height(6.dp)
                      .clip(CircleShape)
                      .background(SleekSurface)
                  ) {
                    val progressFraction = ((userXp % 250).toFloat() / 250f).coerceIn(0.08f, 1f)
                    Box(
                      modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .clip(CircleShape)
                        .background(SleekCyanDark)
                    )
                  }

                  Text(
                    text = "LVL $userLevel",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary,
                    letterSpacing = 1.2.sp
                  )
                }
              }

              // Right Stat Chips: px-2 py-1 bg-slate-800 rounded-lg border border-slate-700
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Energy/Coin Pill (⚡ 2,450)
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SleekSurface)
                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                  Text("⚡", fontSize = 12.sp, color = SleekYellowEnergy)
                  Text(
                    text = String.format("%,d", userCoins),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SleekTextPrimary
                  )
                }

                // Research/XP Pill (🧪 820)
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SleekSurface)
                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                  Text("🧪", fontSize = 12.sp, color = SleekPurpleChem)
                  Text(
                    text = String.format("%,d", userXp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SleekTextPrimary
                  )
                }
              }
            }
          }
        )

        // Sleek subtle bottom divider: border-b border-slate-800
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SleekBorderMuted)
        )
      }
    },
    bottomBar = {
      // Sleek Navigation Bar: h-16 bg-slate-950 border-t border-slate-800
      Column {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SleekBorderMuted)
        )
        NavigationBar(
          containerColor = SleekNavBg,
          tonalElevation = 0.dp
        ) {
          tabs.forEachIndexed { index, item ->
            NavigationBarItem(
              modifier = Modifier.testTag(item.tag),
              selected = activeTab == index,
              onClick = { activeTab = index },
              icon = {
                Icon(
                  imageVector = item.icon,
                  contentDescription = item.title,
                  modifier = Modifier.size(20.dp)
                )
              },
              label = {
                Text(
                  text = item.title.uppercase(),
                  fontSize = 9.sp,
                  fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                  letterSpacing = 0.4.sp
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SleekCyan,
                selectedTextColor = SleekCyan,
                unselectedIconColor = SleekTextMuted,
                unselectedTextColor = SleekTextMuted,
                indicatorColor = SleekCyan.copy(alpha = 0.15f)
              )
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SleekBg)
        .padding(paddingValues)
    ) {
      when (activeTab) {
        0 -> LabWorkbenchScreen(
          onRewardEarned = { xp, coins ->
            grantReward(xp, coins)
          }
        )
        1 -> ExperimentsScreen(
          completedExperiments = completedExperiments,
          onCompleteExperiment = { exp ->
            if (!completedExperiments.contains(exp.id)) {
              prefs.markExperimentCompleted(exp.id, exp.xpReward, exp.coinsReward)
              completedExperiments = prefs.getCompletedExperiments()
              userXp = prefs.xp
              userCoins = prefs.coins
              userLevel = prefs.level
              grantReward(exp.xpReward, exp.coinsReward, "Completed ${exp.title}!")
            }
          },
          onNavigateToWorkbench = { _ ->
            activeTab = 0
          }
        )
        2 -> MoleculeMakerScreen(
          onRewardEarned = { xp, coins ->
            grantReward(xp, coins, "Synthesized Valid Compound! +$xp XP")
          }
        )
        3 -> EquipmentScreen(
          userCoins = userCoins,
          unlockedEquipment = unlockedEquipment,
          onUnlockEquipment = { item ->
            if (prefs.unlockEquipment(item.id, item.unlockCost)) {
              userCoins = prefs.coins
              unlockedEquipment = prefs.getUnlockedEquipment()
              grantReward(15, 0, "Unlocked ${item.name}!")
              true
            } else {
              false
            }
          },
          onUpgradeEquipment = { item ->
            prefs.upgradeEquipment(item.id, item.upgradeCost)
            userCoins = prefs.coins
            grantReward(10, 0, "Calibrated and Upgraded ${item.name}!")
          }
        )
        4 -> ChallengesScreen(
          completedChallenges = completedChallenges,
          onClaimChallenge = { ch ->
            if (!completedChallenges.contains(ch.id)) {
              prefs.markChallengeCompleted(ch.id, ch.xpReward, ch.coinsReward)
              completedChallenges = prefs.getCompletedChallenges()
              userXp = prefs.xp
              userCoins = prefs.coins
              userLevel = prefs.level
              grantReward(ch.xpReward, ch.coinsReward, "Bounty Claimed: ${ch.title}!")
            }
          }
        )
        5 -> AchievementsScreen(
          userXp = userXp,
          userLevel = userLevel,
          userRank = prefs.rankTitle,
          userCoins = userCoins,
          unlockedAchievements = unlockedAchievements
        )
        6 -> SettingsScreen(
          currentTheme = currentTheme,
          onThemeChanged = onThemeChanged,
          onReviewPrivacy = onReviewPrivacy,
          onResetProgress = {
            prefs.resetAllProgress()
            userXp = prefs.xp
            userCoins = prefs.coins
            userLevel = prefs.level
            completedExperiments = prefs.getCompletedExperiments()
            unlockedEquipment = prefs.getUnlockedEquipment()
            completedChallenges = prefs.getCompletedChallenges()
            unlockedAchievements = prefs.getUnlockedAchievements()
            coroutineScope.launch {
              snackbarHostState.showSnackbar("Laboratory progress reset.")
            }
          }
        )
      }
    }
  }
}
