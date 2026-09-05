package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet
import kotlin.math.log10
import kotlin.math.sin

enum class ChemApparatusDomain {
  TITRATION,
  DANIELL_CELL,
  ELECTROLYSIS,
  PRECIPITATION_BURNING
}

enum class IndicatorChoice {
  PHENOLPHTHALEIN,
  METHYL_ORANGE,
  BROMOTHYMOL_BLUE
}

@Composable
fun ChemistrySimulatorView(
  onExperimentSuccess: (String) -> Unit = {}
) {
  var selectedDomain by remember { mutableStateOf(ChemApparatusDomain.TITRATION) }

  // Titration State (50mL burette, 25mL 0.1M HCl analyte, 0.1M NaOH titrant)
  var volumeAddedMl by remember { mutableFloatStateOf(0.0f) }
  var selectedIndicator by remember { mutableStateOf(IndicatorChoice.PHENOLPHTHALEIN) }

  // Daniell Cell State
  var concZnM by remember { mutableFloatStateOf(1.0f) }
  var concCuM by remember { mutableFloatStateOf(1.0f) }

  // Electrolysis State
  var electrolysisVoltage by remember { mutableFloatStateOf(6.0f) }
  var electrolysisTimeSec by remember { mutableFloatStateOf(30.0f) }

  // Precipitation / Heating State
  var selectedPrecipitateTest by remember { mutableStateOf("AgCl") } // AgCl, BaSO4, Fe(SCN)3, Cu(OH)2
  var burnerTemperatureC by remember { mutableFloatStateOf(25.0f) }

  // Animation for bubbles and stir bar
  val infiniteTransition = rememberInfiniteTransition(label = "chem_anim")
  val bubblePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "bubble_phase"
  )

  // Compute Titration pH
  // Initial 25 mL of 0.1M HCl (0.0025 mol H+)
  // V_added of 0.1M NaOH (V_added * 0.1 * 1e-3 mol OH-)
  val molesH = 0.0025
  val molesOH = (volumeAddedMl / 1000.0) * 0.1
  val totalVolL = (25.0 + volumeAddedMl) / 1000.0

  val currentPh = when {
    volumeAddedMl < 24.95f -> {
      val excessH = (molesH - molesOH) / totalVolL
      (-log10(excessH.coerceAtLeast(1e-14))).toFloat()
    }
    volumeAddedMl in 24.95f..25.05f -> 7.0f // Neutral equivalence point
    else -> {
      val excessOH = (molesOH - molesH) / totalVolL
      val pOH = (-log10(excessOH.coerceAtLeast(1e-14))).toFloat()
      (14.0f - pOH).coerceIn(7f, 13.8f)
    }
  }

  // Solution Color in Flask based on Indicator and pH
  val flaskSolutionColor = when (selectedIndicator) {
    IndicatorChoice.PHENOLPHTHALEIN -> {
      if (currentPh < 8.2f) Color(0x2238BDF8) // clear / slightly bluish tint
      else Color(0xCCF43F5E) // vivid pink / magenta
    }
    IndicatorChoice.METHYL_ORANGE -> {
      if (currentPh < 3.1f) Color(0xEEEF4444) // red
      else if (currentPh < 4.4f) Color(0xEEF97316) // orange
      else Color(0xEEFACC15) // yellow
    }
    IndicatorChoice.BROMOTHYMOL_BLUE -> {
      if (currentPh < 6.0f) Color(0xEEFACC15) // yellow
      else if (currentPh < 7.6f) Color(0xEE10B981) // green
      else Color(0xEE2563EB) // blue
    }
  }

  // Daniell Cell EMF via Nernst Equation
  // E_cell = E0 - (0.0592 / 2) * log10([Zn2+] / [Cu2+])
  val standardEmf = 1.10f
  val cellEmf = (standardEmf - (0.0592f / 2f) * log10(concZnM / concCuM)).coerceIn(0.8f, 1.3f)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Mode switcher
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedDomain == ChemApparatusDomain.TITRATION,
        onClick = { selectedDomain = ChemApparatusDomain.TITRATION },
        label = { Text("Acid-Base Titration") },
        leadingIcon = { Icon(Icons.Default.Science, null, Modifier.size(16.dp)) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == ChemApparatusDomain.DANIELL_CELL,
        onClick = { selectedDomain = ChemApparatusDomain.DANIELL_CELL },
        label = { Text("Daniell Galvanic Cell") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == ChemApparatusDomain.ELECTROLYSIS,
        onClick = { selectedDomain = ChemApparatusDomain.ELECTROLYSIS },
        label = { Text("Water Electrolysis (2:1)") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == ChemApparatusDomain.PRECIPITATION_BURNING,
        onClick = { selectedDomain = ChemApparatusDomain.PRECIPITATION_BURNING },
        label = { Text("Salt Reactions & Burner") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
    }

    // Interactive Chemistry Bench Canvas
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .testTag("chem_bench_canvas"),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        when (selectedDomain) {
          ChemApparatusDomain.TITRATION -> {
            drawTitrationApparatus(volumeAddedMl, flaskSolutionColor, currentPh)
          }
          ChemApparatusDomain.DANIELL_CELL -> {
            drawDaniellCellApparatus(concZnM, concCuM, cellEmf, bubblePhase)
          }
          ChemApparatusDomain.ELECTROLYSIS -> {
            drawElectrolysisApparatus(electrolysisVoltage, electrolysisTimeSec, bubblePhase)
          }
          ChemApparatusDomain.PRECIPITATION_BURNING -> {
            drawPrecipitationAndBurner(selectedPrecipitateTest, burnerTemperatureC, bubblePhase)
          }
        }
      }
    }

    // Live Analytical Readings & Formulas Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (selectedDomain) {
          ChemApparatusDomain.TITRATION -> {
            Text(
              text = "Reaction: HCl (aq) + NaOH (aq) ➔ NaCl (aq) + H₂O (l)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ElectricCyan
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "TITRANT (NaOH)", String.format("%.2f mL", volumeAddedMl), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "SOLUTION pH", String.format("%.2f", currentPh), if (currentPh in 6.9f..7.1f) NeonEmerald else LaserCrimson)
              MeterDisplayBox(Modifier.weight(1f), "EQUIVALENCE", if (volumeAddedMl in 24.9f..25.1f) "REACHED!" else "25.00 mL", AtomicAmber)
            }
          }
          ChemApparatusDomain.DANIELL_CELL -> {
            Text(
              text = "Nernst: E_cell = 1.10V - (0.0592 / 2) · log([Zn²⁺] / [Cu²⁺])",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NeonEmerald
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "CELL EMF", String.format("%.3f V", cellEmf), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "[Zn²⁺] ANODE", String.format("%.2f M", concZnM), LaserCrimson)
              MeterDisplayBox(Modifier.weight(1f), "[Cu²⁺] CATHODE", String.format("%.2f M", concCuM), AtomicAmber)
            }
          }
          ChemApparatusDomain.ELECTROLYSIS -> {
            // Cathode: 2H+ + 2e- -> H2(g)
            // Anode: 2H2O -> O2 + 4H+ + 4e-
            val h2VolumeMl = (electrolysisTimeSec * (electrolysisVoltage / 6f) * 0.4f).coerceAtMost(30f)
            val o2VolumeMl = h2VolumeMl / 2f
            Text(
              text = "Electrolysis: 2H₂O (l) ➔ 2H₂ (g) [Cathode] + O₂ (g) [Anode]",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ElectricCyan
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "H₂ GAS VOLUME", String.format("%.1f mL", h2VolumeMl), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "O₂ GAS VOLUME", String.format("%.1f mL", o2VolumeMl), NeonEmerald)
              MeterDisplayBox(Modifier.weight(1f), "STOICHIOMETRY", "2 : 1 Ratio", AtomicAmber)
            }
          }
          ChemApparatusDomain.PRECIPITATION_BURNING -> {
            val reactionSummary = when (selectedPrecipitateTest) {
              "AgCl" -> "AgNO₃ + NaCl ➔ AgCl↓ (Curdy White) + NaNO₃"
              "BaSO4" -> "BaCl₂ + Na₂SO₄ ➔ BaSO₄↓ (Heavy White) + 2NaCl"
              "Fe(SCN)3" -> "FeCl₃ + 3KSCN ➔ Fe(SCN)₃ (Blood Red Complex) + 3KCl"
              else -> "CuSO₄ + 2NaOH ➔ Cu(OH)₂↓ (Pale Blue) + Na₂SO₄"
            }
            Text(reactionSummary, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AtomicAmber)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "PRECIPITATE", selectedPrecipitateTest, NeonEmerald)
              MeterDisplayBox(Modifier.weight(1f), "BURNER TEMP", "${burnerTemperatureC.toInt()} °C", LaserCrimson)
              MeterDisplayBox(Modifier.weight(1f), "STATUS", if (burnerTemperatureC > 100f) "DECOMPOSING" else "STABLE", ElectricCyan)
            }
          }
        }
      }
    }

    // Apparatus Interactive Controls
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        when (selectedDomain) {
          ChemApparatusDomain.TITRATION -> {
            Text("Titration Controls", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            // Indicator selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(
                "Phenolphthalein" to IndicatorChoice.PHENOLPHTHALEIN,
                "Methyl Orange" to IndicatorChoice.METHYL_ORANGE,
                "Bromothymol Blue" to IndicatorChoice.BROMOTHYMOL_BLUE
              ).forEach { (name, choice) ->
                FilterChip(
                  selected = selectedIndicator == choice,
                  onClick = { selectedIndicator = choice },
                  label = { Text(name, fontSize = 11.sp) }
                )
              }
            }

            // Dispense buttons & Stopcock Slider
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { volumeAddedMl = (volumeAddedMl + 0.1f).coerceAtMost(50f) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
              ) {
                Icon(Icons.Default.Opacity, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Drop (+0.1mL)", fontSize = 11.sp)
              }
              Button(
                onClick = { volumeAddedMl = (volumeAddedMl + 1.0f).coerceAtMost(50f) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Stream (+1mL)", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { volumeAddedMl = 0f },
                modifier = Modifier.weight(0.8f)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, Modifier.size(16.dp))
                Text("Reset", fontSize = 11.sp)
              }
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Burette Stopcock Dispensed Volume", fontSize = 13.sp)
                Text(String.format("%.2f mL", volumeAddedMl), fontWeight = FontWeight.Bold, color = ElectricCyan)
              }
              Slider(
                value = volumeAddedMl,
                onValueChange = { volumeAddedMl = it },
                valueRange = 0f..50f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
              )
            }
          }

          ChemApparatusDomain.DANIELL_CELL -> {
            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("[Zn²⁺] Zinc Ion Concentration", fontSize = 13.sp)
                Text(String.format("%.2f M", concZnM), fontWeight = FontWeight.Bold, color = LaserCrimson)
              }
              Slider(
                value = concZnM,
                onValueChange = { concZnM = it },
                valueRange = 0.01f..2.0f,
                colors = SliderDefaults.colors(thumbColor = LaserCrimson, activeTrackColor = LaserCrimson)
              )
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("[Cu²⁺] Copper Ion Concentration", fontSize = 13.sp)
                Text(String.format("%.2f M", concCuM), fontWeight = FontWeight.Bold, color = AtomicAmber)
              }
              Slider(
                value = concCuM,
                onValueChange = { concCuM = it },
                valueRange = 0.01f..2.0f,
                colors = SliderDefaults.colors(thumbColor = AtomicAmber, activeTrackColor = AtomicAmber)
              )
            }
          }

          ChemApparatusDomain.ELECTROLYSIS -> {
            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DC Cell Voltage", fontSize = 13.sp)
                Text(String.format("%.1f V", electrolysisVoltage), fontWeight = FontWeight.Bold, color = ElectricCyan)
              }
              Slider(
                value = electrolysisVoltage,
                onValueChange = { electrolysisVoltage = it },
                valueRange = 1.5f..15.0f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
              )
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Electrolysis Duration", fontSize = 13.sp)
                Text("${electrolysisTimeSec.toInt()} s", fontWeight = FontWeight.Bold, color = NeonEmerald)
              }
              Slider(
                value = electrolysisTimeSec,
                onValueChange = { electrolysisTimeSec = it },
                valueRange = 5f..120f,
                colors = SliderDefaults.colors(thumbColor = NeonEmerald, activeTrackColor = NeonEmerald)
              )
            }
          }

          ChemApparatusDomain.PRECIPITATION_BURNING -> {
            Text("Reagent Tests", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("AgCl", "BaSO4", "Fe(SCN)3", "Cu(OH)2").forEach { test ->
                FilterChip(
                  selected = selectedPrecipitateTest == test,
                  onClick = { selectedPrecipitateTest = test },
                  label = { Text(test, fontSize = 11.sp) }
                )
              }
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bunsen Burner Heating Temperature", fontSize = 13.sp)
                Text("${burnerTemperatureC.toInt()} °C", fontWeight = FontWeight.Bold, color = LaserCrimson)
              }
              Slider(
                value = burnerTemperatureC,
                onValueChange = { burnerTemperatureC = it },
                valueRange = 25f..600f,
                colors = SliderDefaults.colors(thumbColor = LaserCrimson, activeTrackColor = LaserCrimson)
              )
            }
          }
        }
      }
    }
  }
}

// Canvas Drawings for Chemistry Apparatus
private fun DrawScope.drawTitrationApparatus(volumeAddedMl: Float, solutionColor: Color, pH: Float) {
  val cx = size.width * 0.4f

  // Retort Stand
  drawLine(Color(0xFF64748B), Offset(cx - 60f, size.height - 20f), Offset(cx + 100f, size.height - 20f), strokeWidth = 8f)
  drawLine(Color(0xFF64748B), Offset(cx - 40f, 20f), Offset(cx - 40f, size.height - 20f), strokeWidth = 6f)
  // Clamp arm holding burette
  drawLine(Color(0xFF64748B), Offset(cx - 40f, 60f), Offset(cx, 60f), strokeWidth = 4f)

  // Burette (tall narrow glass cylinder with subdivisions)
  val buretteTop = 25f
  val buretteBottom = 130f
  val buretteWidth = 16f

  drawRoundRect(
    color = Color(0xFF1E293B),
    topLeft = Offset(cx - buretteWidth / 2f, buretteTop),
    size = Size(buretteWidth, buretteBottom - buretteTop),
    cornerRadius = CornerRadius(4f, 4f)
  )
  drawRoundRect(
    color = Color(0xFF38BDF8),
    topLeft = Offset(cx - buretteWidth / 2f, buretteTop),
    size = Size(buretteWidth, buretteBottom - buretteTop),
    cornerRadius = CornerRadius(4f, 4f),
    style = Stroke(width = 2f)
  )

  // Titrant liquid inside burette (drops as volume is added)
  val liquidRemainingHeight = (1f - (volumeAddedMl / 50f)) * (buretteBottom - buretteTop - 10f)
  drawRect(
    color = Color(0x7700E5FF),
    topLeft = Offset(cx - buretteWidth / 2f + 2f, buretteBottom - liquidRemainingHeight),
    size = Size(buretteWidth - 4f, liquidRemainingHeight)
  )

  // Stopcock tap
  drawCircle(Color(0xFFF59E0B), radius = 5f, center = Offset(cx, buretteBottom + 8f))

  // Burette tip
  drawLine(Color(0xFF38BDF8), Offset(cx, buretteBottom + 8f), Offset(cx, buretteBottom + 25f), strokeWidth = 2.5f)

  // Falling drop if dispensed
  if (volumeAddedMl > 0.1f && volumeAddedMl < 49.9f) {
    drawCircle(Color(0xFF00E5FF), radius = 3f, center = Offset(cx, buretteBottom + 35f))
  }

  // Erlenmeyer Flask at bottom
  val flaskTop = buretteBottom + 45f
  val flaskBottom = size.height - 25f
  val flaskBaseHalfW = 42f

  val flaskPath = Path().apply {
    moveTo(cx - 10f, flaskTop)
    lineTo(cx + 10f, flaskTop)
    lineTo(cx + 10f, flaskTop + 15f)
    lineTo(cx + flaskBaseHalfW, flaskBottom)
    lineTo(cx - flaskBaseHalfW, flaskBottom)
    lineTo(cx - 10f, flaskTop + 15f)
    close()
  }

  // Colored solution inside flask
  val solutionPath = Path().apply {
    moveTo(cx - 28f, flaskBottom - 35f)
    lineTo(cx + 28f, flaskBottom - 35f)
    lineTo(cx + flaskBaseHalfW - 3f, flaskBottom - 3f)
    lineTo(cx - flaskBaseHalfW + 3f, flaskBottom - 3f)
    close()
  }
  drawPath(solutionPath, color = solutionColor)

  // Flask Glass Outline
  drawPath(flaskPath, color = Color(0xFF94A3B8), style = Stroke(width = 2.5f))

  // Digital pH Titration Curve Mini-Graph on the right side of the canvas
  val graphLeft = size.width * 0.65f
  val graphRight = size.width - 25f
  val graphTop = 35f
  val graphBottom = size.height - 35f

  drawLine(Color.Gray, Offset(graphLeft, graphTop), Offset(graphLeft, graphBottom), strokeWidth = 2f)
  drawLine(Color.Gray, Offset(graphLeft, graphBottom), Offset(graphRight, graphBottom), strokeWidth = 2f)

  // S-Curve path
  val curvePath = Path()
  for (v in 0..50) {
    val x = graphLeft + (v / 50f) * (graphRight - graphLeft)
    // Sigmoid function for S-curve
    val sPh = when {
      v < 25 -> 1.0f + 2.0f / (1f + kotlin.math.exp((24f - v) * 0.8f))
      v == 25 -> 7.0f
      else -> 13.0f - 2.0f / (1f + kotlin.math.exp((v - 26f) * 0.8f))
    }
    val y = graphBottom - (sPh / 14f) * (graphBottom - graphTop)
    if (v == 0) curvePath.moveTo(x, y) else curvePath.lineTo(x, y)
  }
  drawPath(curvePath, color = Color(0xFF10B981), style = Stroke(width = 2.5f))

  // Current titration point marker on curve
  val curGraphX = graphLeft + (volumeAddedMl / 50f) * (graphRight - graphLeft)
  val curGraphY = graphBottom - (pH / 14f) * (graphBottom - graphTop)
  drawCircle(Color(0xFFF43F5E), radius = 6f, center = Offset(curGraphX, curGraphY))
}

private fun DrawScope.drawDaniellCellApparatus(znM: Float, cuM: Float, emf: Float, phase: Float) {
  val cy = size.height * 0.6f
  val beakerW = 75f
  val beakerH = 85f

  // Beaker 1: Zinc Half-cell (Left)
  val b1X = size.width * 0.28f
  drawRect(Color(0x3338BDF8), Offset(b1X - beakerW / 2f, cy - beakerH / 2f), Size(beakerW, beakerH))
  drawRect(Color(0xFF94A3B8), Offset(b1X - beakerW / 2f, cy - beakerH / 2f), Size(beakerW, beakerH), style = Stroke(width = 2.5f))
  // Zinc electrode (silvery gray)
  drawRect(Color(0xFF94A3B8), Offset(b1X - 8f, cy - beakerH / 2f - 30f), Size(16f, beakerH + 10f))

  // Beaker 2: Copper Half-cell (Right)
  val b2X = size.width * 0.72f
  drawRect(Color(0x550284C7), Offset(b2X - beakerW / 2f, cy - beakerH / 2f), Size(beakerW, beakerH)) // CuSO4 is blue
  drawRect(Color(0xFF94A3B8), Offset(b2X - beakerW / 2f, cy - beakerH / 2f), Size(beakerW, beakerH), style = Stroke(width = 2.5f))
  // Copper electrode (reddish copper)
  drawRect(Color(0xFFD97706), Offset(b2X - 8f, cy - beakerH / 2f - 30f), Size(16f, beakerH + 10f))

  // Salt Bridge (U-shape connecting the two beakers)
  val bridgePath = Path().apply {
    moveTo(b1X + 15f, cy + 10f)
    lineTo(b1X + 15f, cy - beakerH / 2f - 15f)
    lineTo(b2X - 15f, cy - beakerH / 2f - 15f)
    lineTo(b2X - 15f, cy + 10f)
  }
  drawPath(bridgePath, color = Color(0xCCF8FAFC), style = Stroke(width = 10f, cap = StrokeCap.Round))

  // Wires and Voltmeter at the top
  val vmX = size.width * 0.5f
  val vmY = 40f
  drawLine(Color(0xFFF59E0B), Offset(b1X, cy - beakerH / 2f - 30f), Offset(vmX - 25f, vmY), strokeWidth = 3f)
  drawLine(Color(0xFFF59E0B), Offset(b2X, cy - beakerH / 2f - 30f), Offset(vmX + 25f, vmY), strokeWidth = 3f)

  // Voltmeter meter dial
  drawCircle(Color(0xFF1E293B), radius = 24f, center = Offset(vmX, vmY))
  drawCircle(Color(0xFF00E5FF), radius = 24f, center = Offset(vmX, vmY), style = Stroke(width = 2f))
  // Needle
  drawLine(Color(0xFFF43F5E), Offset(vmX, vmY), Offset(vmX + 14f, vmY - 12f), strokeWidth = 2.5f)

  // Electron flow animation along wire
  val electronX = b1X + phase * (b2X - b1X)
  val electronY = vmY - 10f * sin(phase * 3.14159f)
  drawCircle(Color(0xFF00E5FF), radius = 4f, center = Offset(electronX, electronY))
}

private fun DrawScope.drawElectrolysisApparatus(voltage: Float, timeSec: Float, phase: Float) {
  val cx = size.width / 2f
  val cy = size.height * 0.55f

  // Electrolysis jar / beaker
  drawRoundRect(
    color = Color(0x4438BDF8),
    topLeft = Offset(cx - 90f, cy - 60f),
    size = Size(180f, 130f),
    cornerRadius = CornerRadius(12f, 12f)
  )
  drawRoundRect(
    color = Color(0xFF64748B),
    topLeft = Offset(cx - 90f, cy - 60f),
    size = Size(180f, 130f),
    cornerRadius = CornerRadius(12f, 12f),
    style = Stroke(width = 2.5f)
  )

  // Two inverted test tubes (Cathode H2 on left, Anode O2 on right)
  val tubeW = 30f
  val tubeH = 110f
  val tube1X = cx - 45f
  val tube2X = cx + 45f

  // Tube 1 (Cathode - H2 gas collects twice as fast)
  val h2Level = (timeSec * 0.5f).coerceAtMost(55f)
  drawRect(Color(0xFF1E293B), Offset(tube1X - tubeW / 2f, cy - 90f), Size(tubeW, tubeH))
  drawRect(Color(0xFF00E5FF).copy(alpha = 0.3f), Offset(tube1X - tubeW / 2f, cy - 90f), Size(tubeW, h2Level))
  drawRect(Color(0xFF94A3B8), Offset(tube1X - tubeW / 2f, cy - 90f), Size(tubeW, tubeH), style = Stroke(width = 2f))

  // Tube 2 (Anode - O2 gas collects half)
  val o2Level = h2Level / 2f
  drawRect(Color(0xFF1E293B), Offset(tube2X - tubeW / 2f, cy - 90f), Size(tubeW, tubeH))
  drawRect(Color(0xFF10B981).copy(alpha = 0.3f), Offset(tube2X - tubeW / 2f, cy - 90f), Size(tubeW, o2Level))
  drawRect(Color(0xFF94A3B8), Offset(tube2X - tubeW / 2f, cy - 90f), Size(tubeW, tubeH), style = Stroke(width = 2f))

  // Bubbles rising up the tubes when voltage > 1.5V
  if (voltage > 1.5f) {
    // H2 bubbles (more dense)
    for (i in 0..4) {
      val by = cy + 40f - ((phase + i * 0.2f) % 1f) * 90f
      drawCircle(Color(0xFF00E5FF), radius = 3.5f, center = Offset(tube1X, by))
    }
    // O2 bubbles (less dense)
    for (i in 0..2) {
      val by = cy + 40f - ((phase + i * 0.33f) % 1f) * 90f
      drawCircle(Color(0xFF10B981), radius = 3.5f, center = Offset(tube2X, by))
    }
  }
}

private fun DrawScope.drawPrecipitationAndBurner(test: String, temp: Float, phase: Float) {
  val cx = size.width / 2f
  val cy = size.height * 0.45f

  // Bunsen Burner base at bottom
  val burnerX = cx
  val burnerY = size.height - 35f
  drawRect(Color(0xFF64748B), Offset(burnerX - 35f, burnerY - 10f), Size(70f, 15f))
  drawRect(Color(0xFF94A3B8), Offset(burnerX - 8f, burnerY - 55f), Size(16f, 45f))

  // Bunsen flame if temp > 30C
  if (temp > 30f) {
    val flameH = (temp / 600f * 40f).coerceIn(15f, 45f)
    val flameColor = if (temp > 300f) Color(0xFF00E5FF) else Color(0xFFF59E0B)
    val flamePath = Path().apply {
      moveTo(burnerX - 8f, burnerY - 55f)
      quadraticBezierTo(burnerX, burnerY - 55f - flameH, burnerX + 8f, burnerY - 55f)
      close()
    }
    drawPath(flamePath, color = flameColor)
  }

  // Crucible / beaker on tripod
  val beakerW = 80f
  val beakerH = 70f
  val beakerY = cy - 20f

  val precipitateColor = when (test) {
    "AgCl" -> Color(0xFFF8FAFC) // curdy white
    "BaSO4" -> Color(0xFFE2E8F0) // dense white
    "Fe(SCN)3" -> Color(0xFF991B1B) // blood red
    else -> Color(0xFF38BDF8) // pale blue
  }

  drawRect(Color(0x3338BDF8), Offset(cx - beakerW / 2f, beakerY), Size(beakerW, beakerH))
  // Precipitate precipitate layer at bottom
  drawRect(precipitateColor, Offset(cx - beakerW / 2f + 2f, beakerY + beakerH - 25f), Size(beakerW - 4f, 23f))
  drawRect(Color(0xFF94A3B8), Offset(cx - beakerW / 2f, beakerY), Size(beakerW, beakerH), style = Stroke(width = 2.5f))

  // Tripod legs
  drawLine(Color(0xFF64748B), Offset(cx - beakerW / 2f, beakerY + beakerH), Offset(cx - 50f, size.height - 35f), strokeWidth = 3f)
  drawLine(Color(0xFF64748B), Offset(cx + beakerW / 2f, beakerY + beakerH), Offset(cx + 50f, size.height - 35f), strokeWidth = 3f)
}
