package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExperimentModel
import com.example.data.LabDomain
import com.example.data.LabRepository
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun ExperimentsScreen(
  completedExperiments: Set<String>,
  onCompleteExperiment: (ExperimentModel) -> Unit,
  onNavigateToWorkbench: (LabDomain) -> Unit
) {
  var selectedDomainFilter by remember { mutableStateOf<LabDomain?>(null) }
  var selectedExperiment by remember { mutableStateOf<ExperimentModel?>(null) }

  val filteredList = remember(selectedDomainFilter) {
    if (selectedDomainFilter == null) {
      LabRepository.experiments
    } else {
      LabRepository.experiments.filter { it.domain == selectedDomainFilter }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Top Filter Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedDomainFilter == null,
        onClick = { selectedDomainFilter = null },
        label = { Text("All Experiments (${LabRepository.experiments.size})") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomainFilter == LabDomain.PHYSICS,
        onClick = { selectedDomainFilter = LabDomain.PHYSICS },
        label = { Text("⚡ Physics Practicals") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomainFilter == LabDomain.CHEMISTRY,
        onClick = { selectedDomainFilter = LabDomain.CHEMISTRY },
        label = { Text("🧪 Chemistry Practicals") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonEmerald.copy(alpha = 0.2f))
      )
    }

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredList) { exp ->
        val isCompleted = completedExperiments.contains(exp.id)
        val isExpanded = selectedExperiment?.id == exp.id

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              selectedExperiment = if (isExpanded) null else exp
            }
            .testTag("experiment_item_${exp.id}"),
          colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    .background(if (exp.domain == LabDomain.PHYSICS) ElectricCyan.copy(alpha = 0.2f) else NeonEmerald.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (exp.domain == LabDomain.PHYSICS) Icons.Default.Bolt else Icons.Default.Science,
                    contentDescription = null,
                    tint = if (exp.domain == LabDomain.PHYSICS) ElectricCyan else NeonEmerald,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column {
                  Text(exp.topic, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                  Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }

              if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = NeonEmerald, modifier = Modifier.size(20.dp))
              }
            }

            Text(exp.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Rewards Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.Stars, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                  Text("+${exp.xpReward} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AtomicAmber, modifier = Modifier.size(14.dp))
                  Text("+${exp.coinsReward} Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AtomicAmber)
                }
              }

              Text(
                text = if (isExpanded) "Tap to collapse" else "Tap for theory & live test",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
              )
            }

            // Expanded Scientific Theory, Formulas, and Execution
            if (isExpanded) {
              Spacer(Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF0F172A))
                  .padding(12.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("🎯 Experimental Objective:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElectricCyan)
                  Text(exp.objective, fontSize = 11.sp, color = Color.LightGray)

                  Text("📐 Governing Formulas & Principles:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AtomicAmber)
                  Text(exp.theoryAndFormula, fontSize = 11.sp, color = Color.LightGray, fontFamily = FontFamily.Monospace)

                  Text("🔬 Expected Observations:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonEmerald)
                  Text(exp.observationNotes, fontSize = 11.sp, color = Color.LightGray)

                  Spacer(Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = {
                        onCompleteExperiment(exp)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                      modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                      Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp))
                      Spacer(Modifier.width(4.dp))
                      Text(if (isCompleted) "Completed (+XP)" else "Verify & Record", fontSize = 11.sp)
                    }

                    Button(
                      onClick = {
                        onNavigateToWorkbench(exp.domain)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                      modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                      Icon(Icons.Default.PlayArrow, null, Modifier.size(16.dp))
                      Spacer(Modifier.width(4.dp))
                      Text("Launch Workbench", fontSize = 11.sp, color = Color(0xFF00363F))
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
