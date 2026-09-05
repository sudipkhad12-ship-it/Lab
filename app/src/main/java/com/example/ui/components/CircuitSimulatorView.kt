package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class CircuitSubtype {
  DC_OHM,
  POTENTIOMETER,
  AC_RLC,
  TRANSFORMER,
  TRANSISTOR_SWITCH
}

enum class MeterMode {
  VOLTMETER,
  AMMETER,
  MULTIMETER,
  GALVANOMETER
}

@Composable
fun CircuitSimulatorView(
  onCircuitEvaluated: (Boolean, Float) -> Unit = { _, _ -> }
) {
  var selectedSubtype by remember { mutableStateOf(CircuitSubtype.DC_OHM) }
  var selectedMeter by remember { mutableStateOf(MeterMode.MULTIMETER) }

  // Component states
  var voltageDC by remember { mutableFloatStateOf(10.0f) } // Volts
  var resistanceLoad by remember { mutableFloatStateOf(200.0f) } // Ohms
  var potRatio by remember { mutableFloatStateOf(0.5f) } // 0 to 1
  var capacitanceUf by remember { mutableFloatStateOf(100.0f) } // uF
  var inductanceMh by remember { mutableFloatStateOf(10.0f) } // mH
  var acFrequency by remember { mutableFloatStateOf(50.0f) } // Hz
  var switchClosed by remember { mutableStateOf(true) }
  var ledColor by remember { mutableStateOf(Color(0xFF10B981)) }
  var ledBurnedOut by remember { mutableStateOf(false) }
  var transistorBaseCurrentMa by remember { mutableFloatStateOf(0.5f) }

  // Calculate circuit metrics
  val totalResistance = when (selectedSubtype) {
    CircuitSubtype.DC_OHM -> resistanceLoad
    CircuitSubtype.POTENTIOMETER -> resistanceLoad * potRatio + 10f // Potentiometer tap + internal resistance
    CircuitSubtype.AC_RLC -> {
      val omega = 2f * PI.toFloat() * acFrequency
      val xl = omega * (inductanceMh / 1000f)
      val xc = 1f / (omega * (capacitanceUf / 1000000f))
      kotlin.math.sqrt(resistanceLoad * resistanceLoad + (xl - xc) * (xl - xc))
    }
    CircuitSubtype.TRANSFORMER -> resistanceLoad
    CircuitSubtype.TRANSISTOR_SWITCH -> {
      if (transistorBaseCurrentMa > 0.1f) 50f else 100000f // Saturation vs cutoff
    }
  }

  val isShortCircuit = switchClosed && totalResistance < 1.0f
  val currentAmps = if (!switchClosed || isShortCircuit) 0.0f else (voltageDC / totalResistance)
  val currentMa = currentAmps * 1000.0f
  val powerWatts = currentAmps * currentAmps * totalResistance

  // Check LED burnout (current > 50mA without proper current limiting resistor)
  if (switchClosed && currentMa > 50f && selectedSubtype == CircuitSubtype.DC_OHM && resistanceLoad < 50f) {
    ledBurnedOut = true
  }

  // Animation for electron flow / AC waveform
  val infiniteTransition = rememberInfiniteTransition(label = "circuit_anim")
  val electronPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (currentMa > 0.1f) (1500 / (currentMa.coerceIn(1f, 100f))).toInt().coerceAtLeast(150) else 5000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "electron_flow"
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Mode Switcher Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedSubtype == CircuitSubtype.DC_OHM,
        onClick = { selectedSubtype = CircuitSubtype.DC_OHM },
        label = { Text("Ohm's Circuit & LED") },
        leadingIcon = { Icon(Icons.Default.Bolt, null, Modifier.size(16.dp)) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedSubtype == CircuitSubtype.POTENTIOMETER,
        onClick = { selectedSubtype = CircuitSubtype.POTENTIOMETER },
        label = { Text("Potentiometer / Rheostat") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedSubtype == CircuitSubtype.AC_RLC,
        onClick = { selectedSubtype = CircuitSubtype.AC_RLC },
        label = { Text("AC & RLC Resonance") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedSubtype == CircuitSubtype.TRANSFORMER,
        onClick = { selectedSubtype = CircuitSubtype.TRANSFORMER },
        label = { Text("Transformer (Vs/Vp)") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedSubtype == CircuitSubtype.TRANSISTOR_SWITCH,
        onClick = { selectedSubtype = CircuitSubtype.TRANSISTOR_SWITCH },
        label = { Text("NPN Transistor Switch") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
    }

    // Interactive Circuit Board Canvas (Sleek Interface blueprint design)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(270.dp)
        .testTag("circuit_canvas_card"),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
        ) {
          drawCircuitDiagram(
            subtype = selectedSubtype,
            voltage = voltageDC,
            resistance = resistanceLoad,
            potRatio = potRatio,
            switchClosed = switchClosed,
            currentMa = currentMa,
            electronPhase = electronPhase,
            ledColor = if (ledBurnedOut) Color.DarkGray else ledColor,
            meterMode = selectedMeter
          )
        }

        // Top-Left: Overlay status badge
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.9f))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isShortCircuit -> LaserCrimson
                    !switchClosed -> Color.Gray
                    ledBurnedOut -> LaserCrimson
                    else -> NeonEmerald
                  }
                )
            )
            Text(
              text = when {
                isShortCircuit -> "SHORT CIRCUIT!"
                !switchClosed -> "CIRCUIT OPEN"
                ledBurnedOut -> "LED BURNED OUT"
                else -> "ACTIVE CLOSED LOOP"
              },
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Top-Right: Quick Action Buttons (Power & Reset)
        Row(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Power Button ⏻
          IconButton(
            onClick = { switchClosed = !switchClosed },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (switchClosed) Color(0xFF06B6D4) else Color(0xFF1E293B))
              .border(1.dp, Color(0xFF475569), CircleShape)
          ) {
            Text(
              text = "⏻",
              fontSize = 16.sp,
              color = if (switchClosed) Color(0xFF0F172A) else Color.White,
              fontWeight = FontWeight.Bold
            )
          }

          // Reset Button ↺
          IconButton(
            onClick = {
              switchClosed = true
              ledBurnedOut = false
              voltageDC = 9f
              resistanceLoad = 220f
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFF1E293B))
              .border(1.dp, Color(0xFF475569), CircleShape)
          ) {
            Text(
              text = "↺",
              fontSize = 16.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Bottom-Right: Floating Digital Multimeter box (Sleek Interface design)
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp)
            .width(132.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.95f))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
            .clickable {
              selectedMeter = when (selectedMeter) {
                MeterMode.AMMETER -> MeterMode.VOLTMETER
                MeterMode.VOLTMETER -> MeterMode.MULTIMETER
                MeterMode.MULTIMETER -> MeterMode.GALVANOMETER
                MeterMode.GALVANOMETER -> MeterMode.AMMETER
              }
            }
            .padding(8.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedMeter.name,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
              )
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(if (switchClosed && currentMa > 0.1f) NeonEmerald else Color.Gray)
              )
            }

            Spacer(Modifier.height(4.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF020617))
                .padding(vertical = 4.dp, horizontal = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.Bottom) {
                val displayVal = when (selectedMeter) {
                  MeterMode.AMMETER -> String.format("%.3f", currentAmps)
                  MeterMode.VOLTMETER -> String.format("%.2f", if (switchClosed) voltageDC else 0f)
                  MeterMode.MULTIMETER -> String.format("%.1f", currentMa)
                  MeterMode.GALVANOMETER -> String.format("%+.1f", if (switchClosed) currentMa else 0f)
                }
                val unitStr = when (selectedMeter) {
                  MeterMode.AMMETER -> "A"
                  MeterMode.VOLTMETER -> "V"
                  MeterMode.MULTIMETER -> "mA"
                  MeterMode.GALVANOMETER -> "μA"
                }
                Text(
                  text = displayVal,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = NeonEmerald
                )
                Text(
                  text = " $unitStr",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = NeonEmerald.copy(alpha = 0.7f),
                  modifier = Modifier.padding(bottom = 1.dp)
                )
              }
            }
          }
        }
      }
    }

    // Sleek Quick Component Shelf: h-20 bg-slate-900/90 border border-slate-800
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF0F172A))
        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
        .horizontalScroll(rememberScrollState())
        .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Resistor Component Tray Item
      Box(
        modifier = Modifier
          .width(76.dp)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .clickable { resistanceLoad = if (resistanceLoad >= 1000f) 100f else resistanceLoad + 120f }
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .width(28.dp)
              .height(10.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(Color(0xFF475569))
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = "${resistanceLoad.roundToInt()}Ω",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AtomicAmber,
            fontFamily = FontFamily.Monospace
          )
          Text("RESISTOR", fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        }
      }

      // Battery Cell Item
      Box(
        modifier = Modifier
          .width(76.dp)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .clickable { voltageDC = if (voltageDC >= 24f) 3f else voltageDC + 3f }
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("🔋", fontSize = 11.sp)
          Text(
            text = "${voltageDC.roundToInt()}V DC",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricCyan,
            fontFamily = FontFamily.Monospace
          )
          Text("SUPPLY", fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        }
      }

      // LED Output Item
      Box(
        modifier = Modifier
          .width(76.dp)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .clickable {
            ledColor = when (ledColor) {
              Color(0xFF10B981) -> Color(0xFFEF4444)
              Color(0xFFEF4444) -> Color(0xFF00E5FF)
              Color(0xFF00E5FF) -> Color(0xFFF59E0B)
              else -> Color(0xFF10B981)
            }
            ledBurnedOut = false
          }
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (ledBurnedOut) Color.DarkGray else ledColor)
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = if (ledBurnedOut) "BURNED" else "ACTIVE",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (ledBurnedOut) LaserCrimson else NeonEmerald
          )
          Text("LED DIODE", fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        }
      }

      // Switch Item
      Box(
        modifier = Modifier
          .width(76.dp)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .clickable { switchClosed = !switchClosed }
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(if (switchClosed) "🟢" else "🔴", fontSize = 10.sp)
          Text(
            text = if (switchClosed) "CLOSED" else "OPEN",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (switchClosed) NeonEmerald else Color.Gray
          )
          Text("KNIFE SWITCH", fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        }
      }

      // Multimeter Mode Item
      Box(
        modifier = Modifier
          .width(76.dp)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .clickable {
            selectedMeter = if (selectedMeter == MeterMode.AMMETER) MeterMode.VOLTMETER else MeterMode.AMMETER
          }
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("📟", fontSize = 10.sp)
          Text(
            text = selectedMeter.name.take(6),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricCyan
          )
          Text("METER", fontSize = 7.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        }
      }
    }

    // Diagnostics & Mistake Explanations (Critical User Requirement)
    if (isShortCircuit) {
      Card(
        colors = CardDefaults.cardColors(containerColor = LaserCrimson.copy(alpha = 0.15f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LaserCrimson)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = "Error", tint = LaserCrimson)
          Spacer(Modifier.width(10.dp))
          Column {
            Text("Diagnostic: Short Circuit Detected!", color = LaserCrimson, fontWeight = FontWeight.Bold)
            Text(
              "Zero resistance path across the voltage supply causes infinite current draw (I = V/0). In real labs, this melts wires and damages batteries. Increase resistance immediately.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else if (ledBurnedOut) {
      Card(
        colors = CardDefaults.cardColors(containerColor = LaserCrimson.copy(alpha = 0.15f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LaserCrimson)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = "Burnout", tint = LaserCrimson)
          Spacer(Modifier.width(10.dp))
          Column {
            Text("Diagnostic: LED Overcurrent Burnout!", color = LaserCrimson, fontWeight = FontWeight.Bold)
            Text(
              "Current ($currentMa mA) exceeded the 50mA maximum forward rating! An LED must always be protected with a series current-limiting resistor (e.g., 220Ω - 1kΩ).",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
              onClick = { ledBurnedOut = false; resistanceLoad = 220f },
              colors = ButtonDefaults.buttonColors(containerColor = LaserCrimson),
              modifier = Modifier.padding(top = 6.dp)
            ) {
              Text("Replace LED & Add 220Ω Resistor", fontSize = 11.sp)
            }
          }
        }
      }
    } else if (!switchClosed) {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF334155).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Power, contentDescription = "Open switch", tint = AtomicAmber)
          Spacer(Modifier.width(10.dp))
          Text(
            "Circuit switch is open: No closed path for valence electrons to drift through the conductor. Turn the switch ON below.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    // Live Measurements Meter Display (Multimeter / Galvanometer)
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.ElectricMeter, contentDescription = null, tint = ElectricCyan)
            Text("Laboratory Instruments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }

          // Meter selector
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            MeterMode.entries.forEach { mode ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (selectedMeter == mode) ElectricCyan.copy(alpha = 0.25f) else Color.Transparent)
                  .clickable { selectedMeter = mode }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = when (mode) {
                    MeterMode.VOLTMETER -> "V"
                    MeterMode.AMMETER -> "A"
                    MeterMode.MULTIMETER -> "DMM"
                    MeterMode.GALVANOMETER -> "G"
                  },
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = if (selectedMeter == mode) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(Modifier.height(12.dp))

        // Digital Multimeter LCD readouts
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MeterDisplayBox(
            modifier = Modifier.weight(1f),
            label = "VOLTAGE (V)",
            value = String.format("%.2f V", if (switchClosed) voltageDC else 0f),
            color = ElectricCyan
          )
          MeterDisplayBox(
            modifier = Modifier.weight(1f),
            label = "CURRENT (I)",
            value = String.format("%.1f mA", currentMa),
            color = NeonEmerald
          )
          MeterDisplayBox(
            modifier = Modifier.weight(1f),
            label = "POWER (P)",
            value = String.format("%.2f W", powerWatts),
            color = AtomicAmber
          )
        }

        // Resistor 4-Band Color Code Live Display
        if (selectedSubtype == CircuitSubtype.DC_OHM || selectedSubtype == CircuitSubtype.POTENTIOMETER) {
          Spacer(Modifier.height(12.dp))
          ResistorColorBandCard(resistance = resistanceLoad.roundToInt())
        }
      }
    }

    // Interactive Controls & Knobs
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Apparatus Controls", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Master Switch", fontSize = 13.sp)
            Switch(
              checked = switchClosed,
              onCheckedChange = { switchClosed = it },
              colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
            )
          }
        }

        // Voltage Slider
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("DC Power Source (V)", fontSize = 13.sp)
            Text("${voltageDC.roundToInt()} V", fontWeight = FontWeight.Bold, color = ElectricCyan)
          }
          Slider(
            value = voltageDC,
            onValueChange = { voltageDC = it },
            valueRange = 0f..24f,
            steps = 23,
            colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
          )
        }

        // Resistance / Potentiometer Slider
        if (selectedSubtype == CircuitSubtype.DC_OHM || selectedSubtype == CircuitSubtype.AC_RLC) {
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Load Resistor (R)", fontSize = 13.sp)
              Text("${resistanceLoad.roundToInt()} Ω", fontWeight = FontWeight.Bold, color = NeonEmerald)
            }
            Slider(
              value = resistanceLoad,
              onValueChange = { resistanceLoad = it },
              valueRange = 10f..1000f,
              steps = 98,
              colors = SliderDefaults.colors(thumbColor = NeonEmerald, activeTrackColor = NeonEmerald)
            )
          }
        }

        if (selectedSubtype == CircuitSubtype.POTENTIOMETER) {
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Potentiometer Wiper Position", fontSize = 13.sp)
              Text("${(potRatio * 100).roundToInt()}%", fontWeight = FontWeight.Bold, color = AtomicAmber)
            }
            Slider(
              value = potRatio,
              onValueChange = { potRatio = it },
              valueRange = 0.05f..1.0f,
              colors = SliderDefaults.colors(thumbColor = AtomicAmber, activeTrackColor = AtomicAmber)
            )
          }
        }

        if (selectedSubtype == CircuitSubtype.AC_RLC) {
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("AC Signal Generator Frequency", fontSize = 13.sp)
              Text("${acFrequency.roundToInt()} Hz", fontWeight = FontWeight.Bold, color = PlasmaViolet)
            }
            Slider(
              value = acFrequency,
              onValueChange = { acFrequency = it },
              valueRange = 10f..200f,
              colors = SliderDefaults.colors(thumbColor = PlasmaViolet, activeTrackColor = PlasmaViolet)
            )
          }
        }

        if (selectedSubtype == CircuitSubtype.TRANSISTOR_SWITCH) {
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Base Drive Current (Ib)", fontSize = 13.sp)
              Text(String.format("%.2f mA", transistorBaseCurrentMa), fontWeight = FontWeight.Bold, color = ElectricCyan)
            }
            Slider(
              value = transistorBaseCurrentMa,
              onValueChange = { transistorBaseCurrentMa = it },
              valueRange = 0f..2.0f,
              colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
            )
          }
        }
      }
    }
  }
}

@Composable
fun MeterDisplayBox(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  color: Color
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFF090D16))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
      .padding(vertical = 10.dp, horizontal = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
      Spacer(Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
fun ResistorColorBandCard(resistance: Int) {
  // Compute IEC 4-band resistor color codes
  // e.g. 220 -> 2, 2, x10^1 (Red, Red, Brown, Gold)
  val colors = getResistorBands(resistance)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF1E293B))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("4-Band Resistor Color Code", fontSize = 11.sp, color = Color.LightGray)
        Text("$resistance Ω ± 5%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
      }

      // Visual Resistor with color bands
      Box(
        modifier = Modifier
          .width(130.dp)
          .height(26.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFFD4B996)), // Ceramic tan body
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          colors.forEach { bandColor ->
            Box(
              modifier = Modifier
                .width(8.dp)
                .height(26.dp)
                .background(bandColor)
            )
          }
        }
      }
    }
  }
}

fun getResistorBands(resistance: Int): List<Color> {
  val str = resistance.toString()
  val digit1 = str.getOrNull(0)?.digitToIntOrNull() ?: 1
  val digit2 = str.getOrNull(1)?.digitToIntOrNull() ?: 0
  val zeros = str.length - 2

  fun digitColor(d: Int): Color = when (d) {
    0 -> Color(0xFF000000) // Black
    1 -> Color(0xFF8B4513) // Brown
    2 -> Color(0xFFDC2626) // Red
    3 -> Color(0xFFF97316) // Orange
    4 -> Color(0xFFFACC15) // Yellow
    5 -> Color(0xFF16A34A) // Green
    6 -> Color(0xFF2563EB) // Blue
    7 -> Color(0xFF9333EA) // Violet
    8 -> Color(0xFF6B7280) // Gray
    else -> Color(0xFFFFFFFF) // White
  }

  val multiplierColor = digitColor(zeros.coerceAtLeast(0))
  val toleranceGold = Color(0xFFFFD700) // 5%

  return listOf(digitColor(digit1), digitColor(digit2), multiplierColor, toleranceGold)
}

// 2D Canvas Diagram Renderer with Sleek Blueprint Aesthetic
private fun DrawScope.drawCircuitDiagram(
  subtype: CircuitSubtype,
  voltage: Float,
  resistance: Float,
  potRatio: Float,
  switchClosed: Boolean,
  currentMa: Float,
  electronPhase: Float,
  ledColor: Color,
  meterMode: MeterMode
) {
  val w = size.width
  val h = size.height
  val pad = 35f

  // Sleek Blueprint dot grid (radial-gradient(#1e293b 1px, transparent 1px) 24px)
  val dotSpacing = 24.dp.toPx()
  var gx = 12f
  while (gx < w) {
    var gy = 12f
    while (gy < h) {
      drawCircle(Color(0xFF1E293B), radius = 1.3f, center = Offset(gx, gy))
      gy += dotSpacing
    }
    gx += dotSpacing
  }

  // Inner subtle border
  drawRoundRect(
    color = Color(0xFF334155).copy(alpha = 0.4f),
    topLeft = Offset(8f, 8f),
    size = Size(w - 16f, h - 16f),
    cornerRadius = CornerRadius(12f, 12f),
    style = Stroke(width = 1f)
  )

  val isActive = switchClosed && currentMa > 0.1f
  val wireColor = if (isActive) Color(0xFF22D3EE) else Color(0xFF475569)
  val wireStroke = Stroke(width = 3.5f, cap = StrokeCap.Round)

  // Circuit loop rectangle
  val left = pad + 35f
  val right = w - pad - 35f
  val top = pad + 15f
  val bottom = h - pad - 20f

  // Draw conductors
  val path = Path().apply {
    moveTo(left, top)
    lineTo(right, top)
    lineTo(right, bottom)
    lineTo(left, bottom)
    close()
  }

  // Neon wire glow effect when current flows
  if (isActive) {
    drawPath(path, Color(0xFF06B6D4).copy(alpha = 0.35f), style = Stroke(width = 7f, cap = StrokeCap.Round))
  }
  drawPath(path, wireColor, style = wireStroke)

  // Draw Battery on Left branch (Sleek 9V DC Cell appearance)
  val batteryY = (top + bottom) / 2
  val cellWidth = 36f
  val cellHeight = 54f
  drawRoundRect(
    color = Color(0xFF1E293B),
    topLeft = Offset(left - cellWidth / 2f, batteryY - cellHeight / 2f),
    size = Size(cellWidth, cellHeight),
    cornerRadius = CornerRadius(6f, 6f)
  )
  drawRoundRect(
    color = Color(0xFF475569),
    topLeft = Offset(left - cellWidth / 2f, batteryY - cellHeight / 2f),
    size = Size(cellWidth, cellHeight),
    cornerRadius = CornerRadius(6f, 6f),
    style = Stroke(width = 1.5f)
  )
  // Battery terminal plates
  drawLine(Color(0xFFF59E0B), Offset(left - 14f, batteryY - 12f), Offset(left + 14f, batteryY - 12f), strokeWidth = 4f)
  drawLine(Color(0xFF94A3B8), Offset(left - 8f, batteryY + 4f), Offset(left + 8f, batteryY + 4f), strokeWidth = 6f)
  drawLine(Color(0xFFF59E0B), Offset(left - 14f, batteryY + 16f), Offset(left + 14f, batteryY + 16f), strokeWidth = 4f)

  // Draw Switch on Top branch
  val switchX = left + (right - left) * 0.35f
  drawRect(Color(0xFF0F172A), Offset(switchX - 25f, top - 15f), Size(50f, 30f))
  // Switch contacts
  drawCircle(Color.White, radius = 5f, center = Offset(switchX - 18f, top))
  drawCircle(Color.White, radius = 5f, center = Offset(switchX + 18f, top))
  if (switchClosed) {
    drawLine(Color(0xFF4ADE80), Offset(switchX - 18f, top), Offset(switchX + 18f, top), strokeWidth = 4.5f)
  } else {
    // Open angled knife switch
    drawLine(Color(0xFFF59E0B), Offset(switchX - 18f, top), Offset(switchX + 10f, top - 18f), strokeWidth = 4.5f)
  }

  // Draw Resistor / Load on Right branch (Ceramic resistor package)
  val loadY = (top + bottom) / 2
  val resWidth = 24f
  val resHeight = 56f
  drawRoundRect(
    color = Color(0xFF1E293B),
    topLeft = Offset(right - resWidth / 2f, loadY - resHeight / 2f),
    size = Size(resWidth, resHeight),
    cornerRadius = CornerRadius(5f, 5f)
  )
  drawRoundRect(
    color = Color(0xFF475569),
    topLeft = Offset(right - resWidth / 2f, loadY - resHeight / 2f),
    size = Size(resWidth, resHeight),
    cornerRadius = CornerRadius(5f, 5f),
    style = Stroke(width = 1.5f)
  )
  // Resistor Color Bands
  val bands = getResistorBands(resistance.roundToInt())
  val bandSpacing = resHeight / (bands.size + 1)
  bands.forEachIndexed { i, bandColor ->
    val by = (loadY - resHeight / 2f) + (i + 1) * bandSpacing
    drawLine(bandColor, Offset(right - resWidth / 2f, by), Offset(right + resWidth / 2f, by), strokeWidth = 3.5f)
  }

  // Draw LED / Output Indicator on Bottom branch
  val ledX = (left + right) / 2
  drawRect(Color(0xFF0F172A), Offset(ledX - 25f, bottom - 20f), Size(50f, 40f))
  // LED diode triangle
  val diodePath = Path().apply {
    moveTo(ledX - 12f, bottom - 12f)
    lineTo(ledX - 12f, bottom + 12f)
    lineTo(ledX + 8f, bottom)
    close()
  }
  drawPath(diodePath, ledColor)
  drawLine(ledColor, Offset(ledX + 8f, bottom - 12f), Offset(ledX + 8f, bottom + 12f), strokeWidth = 4f)

  // LED Glow aura if current is flowing (Sleek green pulse)
  if (isActive && ledColor != Color.DarkGray) {
    drawCircle(
      color = ledColor.copy(alpha = 0.35f),
      radius = 24f,
      center = Offset(ledX, bottom)
    )
    // Small emission arrows
    drawLine(ledColor, Offset(ledX + 12f, bottom - 10f), Offset(ledX + 22f, bottom - 20f), strokeWidth = 2.5f)
    drawLine(ledColor, Offset(ledX + 16f, bottom - 6f), Offset(ledX + 26f, bottom - 16f), strokeWidth = 2.5f)
  }

  // Draw animated flowing electron particles along the wires when active!
  if (isActive) {
    val perimeter = 2f * (right - left) + 2f * (bottom - top)
    val numElectrons = 16
    for (i in 0 until numElectrons) {
      val frac = ((i.toFloat() / numElectrons) + electronPhase) % 1.0f
      val dist = frac * perimeter
      val (ex, ey) = getPointOnRect(left, top, right, bottom, dist)
      drawCircle(
        color = Color(0xFF22D3EE),
        radius = 3.5f,
        center = Offset(ex, ey)
      )
    }
  }
}

private fun getPointOnRect(left: Float, top: Float, right: Float, bottom: Float, dist: Float): Pair<Float, Float> {
  val topLen = right - left
  val rightLen = bottom - top
  val bottomLen = right - left
  val leftLen = bottom - top

  var d = dist
  if (d <= topLen) return Pair(left + d, top)
  d -= topLen
  if (d <= rightLen) return Pair(right, top + d)
  d -= rightLen
  if (d <= bottomLen) return Pair(right - d, bottom)
  d -= bottomLen
  return Pair(left, bottom - d)
}
