package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChemistrySimulatorView
import com.example.ui.components.CircuitSimulatorView
import com.example.ui.components.MechanicsOpticsSimulatorView
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekBorderMuted
import com.example.ui.theme.SleekCyan
import com.example.ui.theme.SleekSurfaceHeader
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary

@Composable
fun LabWorkbenchScreen(
  onRewardEarned: (Int, Int) -> Unit = { _, _ -> }
) {
  var activeWorkbenchTab by remember { mutableIntStateOf(0) }

  val tabs = listOf(
    "PHYSICS CIRCUITS",
    "MECHANICS & OPTICS",
    "CHEMISTRY LAB"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SleekBg)
  ) {
    // Sleek Interface Sub-header Tabs Bar: flex border-b border-slate-800 bg-slate-900/80
    TabRow(
      selectedTabIndex = activeWorkbenchTab,
      containerColor = SleekSurfaceHeader.copy(alpha = 0.95f),
      contentColor = SleekCyan,
      divider = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SleekBorderMuted)
        )
      },
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[activeWorkbenchTab]),
          color = SleekCyan,
          height = 2.dp
        )
      }
    ) {
      tabs.forEachIndexed { index, title ->
        val selected = activeWorkbenchTab == index
        Tab(
          selected = selected,
          onClick = { activeWorkbenchTab = index },
          modifier = Modifier
            .padding(vertical = 10.dp)
            .testTag("workbench_tab_$index"),
          text = {
            Text(
              text = title,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp,
              letterSpacing = 0.8.sp,
              textAlign = TextAlign.Center,
              color = if (selected) SleekCyan else SleekTextMuted
            )
          }
        )
      }
    }

    // Active Workbench Display
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SleekBg)
    ) {
      when (activeWorkbenchTab) {
        0 -> CircuitSimulatorView(
          onCircuitEvaluated = { isNormal, current ->
            if (isNormal && current in 20f..80f) {
              onRewardEarned(15, 20)
            }
          }
        )
        1 -> MechanicsOpticsSimulatorView()
        2 -> ChemistrySimulatorView(
          onExperimentSuccess = { experimentId ->
            onRewardEarned(25, 30)
          }
        )
      }
    }
  }
}
