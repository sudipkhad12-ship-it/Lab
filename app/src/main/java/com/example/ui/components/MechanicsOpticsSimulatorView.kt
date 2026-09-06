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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physics.ExperimentPreset
import com.example.physics.GravityPreset
import com.example.physics.Particle2D
import com.example.physics.PhysicsEngine2D
import com.example.physics.Vector2D
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class PhysicsWorkbenchDomain {
  PARTICLE_PHYSICS,
  PENDULUM,
  OPTICS_LENS,
  PRISM_DISPERSION,
  PHOTOELECTRIC,
  NUCLEAR_DECAY
}

@Composable
fun MechanicsOpticsSimulatorView() {
  var selectedDomain by remember { mutableStateOf(PhysicsWorkbenchDomain.PARTICLE_PHYSICS) }

  // 2D Physics Engine State
  val physicsEngine = remember { PhysicsEngine2D() }
  var isPhysicsRunning by remember { mutableStateOf(true) }
  var currentPreset by remember { mutableStateOf(ExperimentPreset.NEWTONS_CRADLE) }
  var engineStepCount by remember { mutableStateOf(0L) }
  var particleGravityG by remember { mutableFloatStateOf(9.8f) }
  var particleRestitution by remember { mutableFloatStateOf(0.92f) }
  var canvasDimensions by remember { mutableStateOf(Size(600f, 260f)) }

  // Preset load sync
  LaunchedEffect(currentPreset, canvasDimensions) {
    if (canvasDimensions.width > 50f && canvasDimensions.height > 50f) {
      physicsEngine.loadPreset(currentPreset, canvasDimensions.width, canvasDimensions.height)
      physicsEngine.gravity = Vector2D(0f, particleGravityG * 40f)
      physicsEngine.globalRestitution = particleRestitution
      engineStepCount++
    }
  }

  // Live real-time physics tick loop
  LaunchedEffect(isPhysicsRunning, selectedDomain) {
    while (isPhysicsRunning && selectedDomain == PhysicsWorkbenchDomain.PARTICLE_PHYSICS) {
      withFrameNanos { _ ->
        physicsEngine.update(0.016f, substeps = 4)
        engineStepCount++
      }
    }
  }

  // Pendulum State
  var pendulumLengthM by remember { mutableFloatStateOf(1.0f) }
  var pendulumMassKg by remember { mutableFloatStateOf(1.0f) }
  var gravityG by remember { mutableFloatStateOf(9.8f) } // Earth: 9.8, Moon: 1.62, Mars: 3.71, Jupiter: 24.8

  // Optics State
  var lensFocalLengthCm by remember { mutableFloatStateOf(15.0f) }
  var objectDistanceCm by remember { mutableFloatStateOf(30.0f) } // u (in magnitude)
  var prismAngleDeg by remember { mutableFloatStateOf(60.0f) }
  var incidentAngleDeg by remember { mutableFloatStateOf(45.0f) }

  // Photoelectric State
  var lightFreqTeraHz by remember { mutableFloatStateOf(750.0f) } // 750 THz = UV/Blue
  var selectedMetalWorkFunctionEv by remember { mutableFloatStateOf(2.3f) } // Sodium 2.3 eV

  // Nuclear Decay State
  var decayTimeYears by remember { mutableFloatStateOf(2800.0f) }
  val halfLifeYears = 5730.0f // C-14

  // Oscillating animation for Pendulum
  val infiniteTransition = rememberInfiniteTransition(label = "phys_anim")
  val oscillationPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = (2000 * sqrt(pendulumLengthM / (gravityG / 9.8f))).toInt().coerceIn(400, 6000),
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Restart
    ),
    label = "pendulum_swing"
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.PARTICLE_PHYSICS,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.PARTICLE_PHYSICS },
        label = { Text("2D Particle Physics") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.PENDULUM,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.PENDULUM },
        label = { Text("Pendulum SHM") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.OPTICS_LENS,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.OPTICS_LENS },
        label = { Text("Ray Optics & Lens") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.PRISM_DISPERSION,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.PRISM_DISPERSION },
        label = { Text("Prism Rainbow Dispersion") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.PHOTOELECTRIC,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.PHOTOELECTRIC },
        label = { Text("Photoelectric Quantum") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
      FilterChip(
        selected = selectedDomain == PhysicsWorkbenchDomain.NUCLEAR_DECAY,
        onClick = { selectedDomain = PhysicsWorkbenchDomain.NUCLEAR_DECAY },
        label = { Text("Radioactive Decay") },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
      )
    }

    // Interactive 2D Simulation Canvas
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .testTag("mechanics_optics_canvas")
        .pointerInput(selectedDomain) {
          if (selectedDomain == PhysicsWorkbenchDomain.PARTICLE_PHYSICS) {
            detectTapGestures { offset ->
              // Spawn an interactive particle at tap location with an impulse velocity
              val colors = listOf(
                Color(0xFF22D3EE),
                Color(0xFF10B981),
                Color(0xFFF59E0B),
                Color(0xFFA855F7),
                Color(0xFFEF4444)
              )
              val randColor = colors.random()
              val randVelX = ((-140..140).random()).toFloat()
              val randVelY = ((-220..-60).random()).toFloat()
              physicsEngine.spawnParticle(
                pos = Vector2D(offset.x, offset.y),
                vel = Vector2D(randVelX, randVelY),
                mass = (10..35).random() / 10f,
                radius = (10..18).random().toFloat(),
                restitution = particleRestitution,
                color = randColor,
                name = "Tap"
              )
              engineStepCount++
            }
          }
        },
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0F19)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        if (size.width != canvasDimensions.width || size.height != canvasDimensions.height) {
          canvasDimensions = size
          physicsEngine.bounds = Rect(15f, 15f, size.width - 15f, size.height - 15f)
        }

        when (selectedDomain) {
          PhysicsWorkbenchDomain.PARTICLE_PHYSICS -> {
            val _tick = engineStepCount
            drawParticlePhysicsCanvas(physicsEngine)
          }
          PhysicsWorkbenchDomain.PENDULUM -> {
            drawPendulumCanvas(pendulumLengthM, pendulumMassKg, gravityG, oscillationPhase)
          }
          PhysicsWorkbenchDomain.OPTICS_LENS -> {
            drawOpticsLensCanvas(lensFocalLengthCm, objectDistanceCm)
          }
          PhysicsWorkbenchDomain.PRISM_DISPERSION -> {
            drawPrismDispersionCanvas(prismAngleDeg, incidentAngleDeg)
          }
          PhysicsWorkbenchDomain.PHOTOELECTRIC -> {
            drawPhotoelectricCanvas(lightFreqTeraHz, selectedMetalWorkFunctionEv, oscillationPhase)
          }
          PhysicsWorkbenchDomain.NUCLEAR_DECAY -> {
            drawNuclearDecayCanvas(decayTimeYears, halfLifeYears)
          }
        }
      }
    }

    // Formulas & Real-time Live Calculations Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (selectedDomain) {
          PhysicsWorkbenchDomain.PARTICLE_PHYSICS -> {
            val ke = physicsEngine.totalKineticEnergy() / 1000f
            val pMag = physicsEngine.totalMomentum().magnitude() / 100f
            Text(
              "2D Collision Dynamics: m₁v₁ + m₂v₂ = m₁v₁' + m₂v₂' | E_k = ½mv²",
              fontWeight = FontWeight.Bold,
              color = ElectricCyan
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "KINETIC ENERGY", String.format("%.2f J", ke), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "COLLISIONS", "${physicsEngine.collisionCount}", NeonEmerald)
              MeterDisplayBox(Modifier.weight(1f), "MOMENTUM |P|", String.format("%.2f", pMag), AtomicAmber)
            }
          }
          PhysicsWorkbenchDomain.PENDULUM -> {
            val period = 2f * PI.toFloat() * sqrt(pendulumLengthM / gravityG)
            val freq = 1f / period
            Text("Simple Harmonic Motion Formula: T = 2π√(L/g)", fontWeight = FontWeight.Bold, color = ElectricCyan)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "PERIOD (T)", String.format("%.2f s", period), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "FREQUENCY (f)", String.format("%.2f Hz", freq), NeonEmerald)
              MeterDisplayBox(Modifier.weight(1f), "GRAVITY (g)", String.format("%.2f m/s²", gravityG), AtomicAmber)
            }
          }
          PhysicsWorkbenchDomain.OPTICS_LENS -> {
            // Gaussian Lens Formula: 1/v - 1/u = 1/f => 1/v = 1/f + 1/(-u) = 1/f - 1/u
            val u = -objectDistanceCm
            val invV = (1f / lensFocalLengthCm) + (1f / u)
            val v = if (invV != 0f) 1f / invV else 999f
            val m = if (u != 0f) -v / u else 0f

            Text("Gaussian Lens Formula: 1/f = 1/v - 1/u | Magnification: m = -v/u", fontWeight = FontWeight.Bold, color = ElectricCyan)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "IMAGE DIST (v)", String.format("%.1f cm", v), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "MAGNIFICATION", String.format("%.2f ×", m), NeonEmerald)
              MeterDisplayBox(Modifier.weight(1f), "IMAGE NATURE", if (v > 0) "REAL, INVERTED" else "VIRTUAL, ERECT", AtomicAmber)
            }
          }
          PhysicsWorkbenchDomain.PRISM_DISPERSION -> {
            // Snell's Law & Cauchy's Dispersion
            val nFlint = 1.66f
            val minDeviation = 2f * (Math.toDegrees(Math.asin((nFlint * sin(Math.toRadians(prismAngleDeg.toDouble() / 2.0))))).toFloat()) - prismAngleDeg
            Text("Refraction & Cauchy Dispersion: n(λ) = A + B/λ²", fontWeight = FontWeight.Bold, color = PlasmaViolet)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "APEX ANGLE (A)", "${prismAngleDeg.toInt()}°", PlasmaViolet)
              MeterDisplayBox(Modifier.weight(1f), "REFRACTIVE INDEX", String.format("%.2f", nFlint), ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "MIN DEVIATION (Dm)", String.format("%.1f°", minDeviation), NeonEmerald)
            }
          }
          PhysicsWorkbenchDomain.PHOTOELECTRIC -> {
            // E = h*nu
            // h = 4.1357e-15 eV*s
            val hEv = 4.1357e-15f
            val photonEnergyEv = hEv * (lightFreqTeraHz * 1e12f)
            val kineticEnergyEv = (photonEnergyEv - selectedMetalWorkFunctionEv).coerceAtLeast(0f)
            val stoppingPotentialV = kineticEnergyEv

            Text("Einstein's Quantum Equation: K_max = h·ν - Φ = e·Vs", fontWeight = FontWeight.Bold, color = AtomicAmber)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "PHOTON (hν)", String.format("%.2f eV", photonEnergyEv), AtomicAmber)
              MeterDisplayBox(Modifier.weight(1f), "WORK FUNCTION (Φ)", String.format("%.2f eV", selectedMetalWorkFunctionEv), LaserCrimson)
              MeterDisplayBox(Modifier.weight(1f), "STOPPING POT (Vs)", String.format("%.2f V", stoppingPotentialV), NeonEmerald)
            }
          }
          PhysicsWorkbenchDomain.NUCLEAR_DECAY -> {
            // N(t) = N0 * (1/2)^(t / T_half)
            val remainingPercent = (100.0 * 0.5.pow((decayTimeYears / halfLifeYears).toDouble())).toFloat()
            Text("Radioactive Decay Law: N(t) = N₀ · e^(-λt) = N₀ · (½)^(t / T_½)", fontWeight = FontWeight.Bold, color = NeonEmerald)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              MeterDisplayBox(Modifier.weight(1f), "TIME ELAPSED", "${decayTimeYears.toInt()} yrs", ElectricCyan)
              MeterDisplayBox(Modifier.weight(1f), "HALF-LIFE (T½)", "${halfLifeYears.toInt()} yrs", AtomicAmber)
              MeterDisplayBox(Modifier.weight(1f), "REMAINING (N/N₀)", String.format("%.1f %%", remainingPercent), NeonEmerald)
            }
          }
        }
      }
    }

    // Interactive Sliders & Celestial Presets
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        when (selectedDomain) {
          PhysicsWorkbenchDomain.PARTICLE_PHYSICS -> {
            Text("Experiment Scenarios & Presets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ExperimentPreset.values().forEach { preset ->
                FilterChip(
                  selected = currentPreset == preset,
                  onClick = {
                    currentPreset = preset
                    physicsEngine.loadPreset(preset, canvasDimensions.width, canvasDimensions.height)
                    physicsEngine.gravity = Vector2D(0f, particleGravityG * 40f)
                    physicsEngine.globalRestitution = particleRestitution
                    engineStepCount++
                  },
                  label = { Text(preset.title, fontSize = 12.sp) },
                  colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
                )
              }
            }

            Text("Gravitational Environments", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              GravityPreset.values().forEach { gp ->
                FilterChip(
                  selected = particleGravityG == gp.gravityY,
                  onClick = {
                    particleGravityG = gp.gravityY
                    physicsEngine.gravity = Vector2D(0f, particleGravityG * 40f)
                  },
                  label = { Text(gp.displayName, fontSize = 11.sp) }
                )
              }
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Custom Gravity Strength (g)", fontSize = 13.sp)
                Text(String.format("%.1f m/s²", particleGravityG), fontWeight = FontWeight.Bold, color = AtomicAmber)
              }
              Slider(
                value = particleGravityG,
                onValueChange = {
                  particleGravityG = it
                  physicsEngine.gravity = Vector2D(0f, particleGravityG * 40f)
                },
                valueRange = 0f..30f,
                colors = SliderDefaults.colors(thumbColor = AtomicAmber, activeTrackColor = AtomicAmber)
              )
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Collision Elasticity / Restitution (e)", fontSize = 13.sp)
                val label = when {
                  particleRestitution >= 0.98f -> "1.00 (Elastic)"
                  particleRestitution <= 0.05f -> "0.00 (Inelastic)"
                  else -> String.format("%.2f", particleRestitution)
                }
                Text(label, fontWeight = FontWeight.Bold, color = ElectricCyan)
              }
              Slider(
                value = particleRestitution,
                onValueChange = {
                  particleRestitution = it
                  physicsEngine.globalRestitution = it
                  for (p in physicsEngine.particles) {
                    if (!p.isPinned) {
                      p.restitution = it
                    }
                  }
                },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
              )
            }

            // Quick actions (Run/Pause, Reset, Add Particle)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { isPhysicsRunning = !isPhysicsRunning },
                colors = ButtonDefaults.buttonColors(containerColor = if (isPhysicsRunning) LaserCrimson else NeonEmerald),
                modifier = Modifier.weight(1f)
              ) {
                Icon(if (isPhysicsRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(if (isPhysicsRunning) "Pause" else "Run")
              }

              Button(
                onClick = {
                  physicsEngine.loadPreset(currentPreset, canvasDimensions.width, canvasDimensions.height)
                  physicsEngine.gravity = Vector2D(0f, particleGravityG * 40f)
                  physicsEngine.globalRestitution = particleRestitution
                  engineStepCount++
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Reset")
              }

              Button(
                onClick = {
                  val minX = 40
                  val maxX = (canvasDimensions.width - 40f).toInt().coerceAtLeast(60)
                  val randX = (minX..maxX).random().toFloat()
                  val colors = listOf(
                    Color(0xFF22D3EE),
                    Color(0xFF10B981),
                    Color(0xFFF59E0B),
                    Color(0xFFA855F7),
                    Color(0xFFEC4899)
                  )
                  physicsEngine.spawnParticle(
                    pos = Vector2D(randX, 40f),
                    vel = Vector2D((-100..100).random().toFloat(), (20..120).random().toFloat()),
                    mass = (10..30).random() / 10f,
                    radius = (10..16).random().toFloat(),
                    restitution = particleRestitution,
                    color = colors.random()
                  )
                  engineStepCount++
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan.copy(alpha = 0.85f)),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add")
              }
            }
          }
          PhysicsWorkbenchDomain.PENDULUM -> {
            Text("Gravity Field Environments", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(
                "Earth (9.8)" to 9.8f,
                "Moon (1.6)" to 1.62f,
                "Mars (3.7)" to 3.71f,
                "Jupiter (24.8)" to 24.79f
              ).forEach { (label, gVal) ->
                FilterChip(
                  selected = gravityG == gVal,
                  onClick = { gravityG = gVal },
                  label = { Text(label, fontSize = 11.sp) }
                )
              }
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pendulum Cord Length (L)", fontSize = 13.sp)
                Text(String.format("%.2f m", pendulumLengthM), fontWeight = FontWeight.Bold, color = ElectricCyan)
              }
              Slider(
                value = pendulumLengthM,
                onValueChange = { pendulumLengthM = it },
                valueRange = 0.2f..2.5f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
              )
            }
          }

          PhysicsWorkbenchDomain.OPTICS_LENS -> {
            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Object Distance from Lens (u)", fontSize = 13.sp)
                Text("${objectDistanceCm.toInt()} cm", fontWeight = FontWeight.Bold, color = ElectricCyan)
              }
              Slider(
                value = objectDistanceCm,
                onValueChange = { objectDistanceCm = it },
                valueRange = 10f..60f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
              )
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Convex Lens Focal Length (f)", fontSize = 13.sp)
                Text("${lensFocalLengthCm.toInt()} cm", fontWeight = FontWeight.Bold, color = NeonEmerald)
              }
              Slider(
                value = lensFocalLengthCm,
                onValueChange = { lensFocalLengthCm = it },
                valueRange = 5f..30f,
                colors = SliderDefaults.colors(thumbColor = NeonEmerald, activeTrackColor = NeonEmerald)
              )
            }
          }

          PhysicsWorkbenchDomain.PRISM_DISPERSION -> {
            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Incident Light Angle of Entry", fontSize = 13.sp)
                Text("${incidentAngleDeg.toInt()}°", fontWeight = FontWeight.Bold, color = PlasmaViolet)
              }
              Slider(
                value = incidentAngleDeg,
                onValueChange = { incidentAngleDeg = it },
                valueRange = 20f..70f,
                colors = SliderDefaults.colors(thumbColor = PlasmaViolet, activeTrackColor = PlasmaViolet)
              )
            }
          }

          PhysicsWorkbenchDomain.PHOTOELECTRIC -> {
            Text("Cathode Target Metal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(
                "Sodium (2.3 eV)" to 2.3f,
                "Cesium (2.1 eV)" to 2.14f,
                "Zinc (4.3 eV)" to 4.3f,
                "Platinum (6.4 eV)" to 6.35f
              ).forEach { (metalName, workFunc) ->
                FilterChip(
                  selected = selectedMetalWorkFunctionEv == workFunc,
                  onClick = { selectedMetalWorkFunctionEv = workFunc },
                  label = { Text(metalName, fontSize = 11.sp) }
                )
              }
            }

            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Photon Beam Frequency (ν)", fontSize = 13.sp)
                Text("${lightFreqTeraHz.toInt()} THz", fontWeight = FontWeight.Bold, color = AtomicAmber)
              }
              Slider(
                value = lightFreqTeraHz,
                onValueChange = { lightFreqTeraHz = it },
                valueRange = 300f..1500f,
                colors = SliderDefaults.colors(thumbColor = AtomicAmber, activeTrackColor = AtomicAmber)
              )
            }
          }

          PhysicsWorkbenchDomain.NUCLEAR_DECAY -> {
            Column {
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Elapsed Radiometric Decay Time (t)", fontSize = 13.sp)
                Text("${decayTimeYears.toInt()} Years", fontWeight = FontWeight.Bold, color = NeonEmerald)
              }
              Slider(
                value = decayTimeYears,
                onValueChange = { decayTimeYears = it },
                valueRange = 0f..20000f,
                colors = SliderDefaults.colors(thumbColor = NeonEmerald, activeTrackColor = NeonEmerald)
              )
            }
          }
        }
      }
    }
  }
}

// Canvas Drawings for Mechanics & Optics
private fun DrawScope.drawParticlePhysicsCanvas(engine: PhysicsEngine2D) {
  val bounds = engine.bounds

  // Laboratory dot grid background
  val dotSpacing = 24f
  var gx = bounds.left + 12f
  while (gx < bounds.right) {
    var gy = bounds.top + 12f
    while (gy < bounds.bottom) {
      drawCircle(Color(0xFF1E293B), radius = 1.3f, center = Offset(gx, gy))
      gy += dotSpacing
    }
    gx += dotSpacing
  }

  // Chamber Border with laboratory cyan/slate styling
  drawRoundRect(
    color = Color(0xFF334155),
    topLeft = Offset(bounds.left, bounds.top),
    size = Size(bounds.width, bounds.height),
    cornerRadius = CornerRadius(14f, 14f),
    style = Stroke(width = 2.5f)
  )

  // Gravity field indicator arrow in top-left
  val gMag = engine.gravity.y / 40f
  if (gMag > 0.1f) {
    val gStartX = bounds.left + 22f
    val gStartY = bounds.top + 20f
    val gLen = (gMag * 2.6f).coerceIn(14f, 44f)
    drawLine(
      color = Color(0xFFF59E0B).copy(alpha = 0.85f),
      start = Offset(gStartX, gStartY),
      end = Offset(gStartX, gStartY + gLen),
      strokeWidth = 3f,
      cap = StrokeCap.Round
    )
    // Arrowhead
    drawLine(
      color = Color(0xFFF59E0B).copy(alpha = 0.85f),
      start = Offset(gStartX - 4f, gStartY + gLen - 5f),
      end = Offset(gStartX, gStartY + gLen),
      strokeWidth = 3f
    )
    drawLine(
      color = Color(0xFFF59E0B).copy(alpha = 0.85f),
      start = Offset(gStartX + 4f, gStartY + gLen - 5f),
      end = Offset(gStartX, gStartY + gLen),
      strokeWidth = 3f
    )
  }

  // Draw particles
  for (p in engine.particles) {
    // 1. Fading trajectory trail
    if (p.trail.size > 1) {
      val trailList = p.trail.toList()
      for (ti in 0 until trailList.size - 1) {
        val fraction = (ti + 1).toFloat() / trailList.size.toFloat()
        val alpha = fraction * 0.45f
        drawLine(
          color = p.color.copy(alpha = alpha),
          start = Offset(trailList[ti].x, trailList[ti].y),
          end = Offset(trailList[ti + 1].x, trailList[ti + 1].y),
          strokeWidth = (p.radius * 0.35f * fraction).coerceAtLeast(1.5f),
          cap = StrokeCap.Round
        )
      }
    }

    val center = Offset(p.position.x, p.position.y)

    // 2. Velocity vector arrow
    val velMag = p.velocity.magnitude()
    if (velMag > 6f && !p.isPinned) {
      val velScale = 0.08f
      val velEnd = Offset(
        p.position.x + p.velocity.x * velScale,
        p.position.y + p.velocity.y * velScale
      )
      drawLine(
        color = p.color.copy(alpha = 0.65f),
        start = center,
        end = velEnd,
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
      )
    }

    // 3. Particle Body
    if (p.isPinned) {
      // Pinned bumper obstacle
      drawCircle(
        color = Color(0xFF334155),
        radius = p.radius,
        center = center
      )
      drawCircle(
        color = Color(0xFF94A3B8),
        radius = p.radius,
        center = center,
        style = Stroke(width = 3f)
      )
      drawLine(Color(0xFF94A3B8), Offset(center.x - p.radius * 0.6f, center.y), Offset(center.x + p.radius * 0.6f, center.y), strokeWidth = 2.5f)
      drawLine(Color(0xFF94A3B8), Offset(center.x, center.y - p.radius * 0.6f), Offset(center.x, center.y + p.radius * 0.6f), strokeWidth = 2.5f)
    } else {
      // Dynamic bouncing particle disc
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(p.color.copy(alpha = 0.95f), p.color.copy(alpha = 0.45f), Color(0xFF0F172A)),
          center = Offset(center.x - p.radius * 0.3f, center.y - p.radius * 0.3f),
          radius = p.radius * 1.2f
        ),
        radius = p.radius,
        center = center
      )
      drawCircle(
        color = p.color,
        radius = p.radius,
        center = center,
        style = Stroke(width = 2f)
      )
      // Specular shine
      drawCircle(
        color = Color.White.copy(alpha = 0.65f),
        radius = p.radius * 0.25f,
        center = Offset(center.x - p.radius * 0.32f, center.y - p.radius * 0.32f)
      )
    }
  }
}

private fun DrawScope.drawPendulumCanvas(lengthM: Float, massKg: Float, g: Float, phase: Float) {
  val cx = size.width / 2f
  val topY = 30f
  val maxAngleRad = 0.5f // ~28 degrees amplitude
  val angle = maxAngleRad * sin(phase)

  val pixelLength = (lengthM * 90f).coerceIn(60f, 170f)
  val bobX = cx + pixelLength * sin(angle)
  val bobY = topY + pixelLength * cos(angle)

  // Ceiling fixture
  drawLine(Color(0xFF64748B), Offset(cx - 50f, topY), Offset(cx + 50f, topY), strokeWidth = 6f)
  drawCircle(Color.White, radius = 5f, center = Offset(cx, topY))

  // Cord
  drawLine(Color(0xFF38BDF8), Offset(cx, topY), Offset(bobX, bobY), strokeWidth = 3f)

  // Pendulum Bob
  val bobRadius = 16f * sqrt(massKg).coerceIn(12f, 26f)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
      center = Offset(bobX - 4f, bobY - 4f),
      radius = bobRadius
    ),
    radius = bobRadius,
    center = Offset(bobX, bobY)
  )
}

private fun DrawScope.drawOpticsLensCanvas(fCm: Float, uCm: Float) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val scale = 3.2f

  // Principal optical axis
  drawLine(Color(0xFF475569), Offset(20f, cy), Offset(size.width - 20f, cy), strokeWidth = 2f)

  // Convex Lens shape at center
  val lensH = 140f
  val lensW = 24f
  val lensPath = Path().apply {
    moveTo(cx, cy - lensH / 2f)
    quadraticBezierTo(cx + lensW, cy, cx, cy + lensH / 2f)
    quadraticBezierTo(cx - lensW, cy, cx, cy - lensH / 2f)
  }
  drawPath(lensPath, color = Color(0xFF00E5FF).copy(alpha = 0.35f))
  drawPath(lensPath, color = Color(0xFF00E5FF), style = Stroke(width = 2f))

  // Focal points F1 and F2
  val fDist = fCm * scale
  drawCircle(Color(0xFFF59E0B), radius = 4f, center = Offset(cx - fDist, cy))
  drawCircle(Color(0xFFF59E0B), radius = 4f, center = Offset(cx + fDist, cy))

  // Object arrow (upright, height 40)
  val objX = cx - (uCm * scale)
  val objH = 45f
  drawLine(Color(0xFF10B981), Offset(objX, cy), Offset(objX, cy - objH), strokeWidth = 5f)
  // Arrowhead
  drawLine(Color(0xFF10B981), Offset(objX - 6f, cy - objH + 8f), Offset(objX, cy - objH), strokeWidth = 4f)
  drawLine(Color(0xFF10B981), Offset(objX + 6f, cy - objH + 8f), Offset(objX, cy - objH), strokeWidth = 4f)

  // Lens equation image calculation
  val u = -uCm
  val invV = (1f / fCm) + (1f / u)
  if (invV != 0f) {
    val vCm = 1f / invV
    val imgX = cx + (vCm * scale)
    val m = -vCm / u
    val imgH = objH * m

    // Image arrow
    val imgColor = if (vCm > 0) Color(0xFFF43F5E) else Color(0xFFA855F7)
    drawLine(imgColor, Offset(imgX, cy), Offset(imgX, cy + imgH), strokeWidth = 5f)

    // Ray 1: Parallel to axis, refracts through F2
    drawLine(Color(0xFF38BDF8).copy(alpha = 0.7f), Offset(objX, cy - objH), Offset(cx, cy - objH), strokeWidth = 2f)
    drawLine(Color(0xFF38BDF8).copy(alpha = 0.7f), Offset(cx, cy - objH), Offset(imgX, cy + imgH), strokeWidth = 2f)

    // Ray 2: Passes straight through optical center
    drawLine(Color(0xFFF59E0B).copy(alpha = 0.7f), Offset(objX, cy - objH), Offset(imgX, cy + imgH), strokeWidth = 2f)
  }
}

private fun DrawScope.drawPrismDispersionCanvas(apexAngleDeg: Float, incidentAngleDeg: Float) {
  val cx = size.width / 2f
  val cy = size.height / 2f + 15f
  val r = 85f

  // Equilateral triangular prism
  val topPoint = Offset(cx, cy - r)
  val leftPoint = Offset(cx - r * 0.866f, cy + r * 0.5f)
  val rightPoint = Offset(cx + r * 0.866f, cy + r * 0.5f)

  val prismPath = Path().apply {
    moveTo(topPoint.x, topPoint.y)
    lineTo(rightPoint.x, rightPoint.y)
    lineTo(leftPoint.x, leftPoint.y)
    close()
  }
  drawPath(prismPath, color = Color(0xFF1E293B))
  drawPath(prismPath, color = Color(0xFF38BDF8), style = Stroke(width = 3f))

  // Incident white laser beam
  val enterPoint = Offset(cx - 45f, cy)
  drawLine(Color.White, Offset(20f, cy + 25f), enterPoint, strokeWidth = 4f)

  // Refracted rainbow spectrum rays exiting prism
  val colors = listOf(
    Color(0xFFEF4444), // Red (least bent)
    Color(0xFFF97316), // Orange
    Color(0xFFFACC15), // Yellow
    Color(0xFF22C55E), // Green
    Color(0xFF3B82F6), // Blue
    Color(0xFF8B5CF6)  // Violet (most bent)
  )

  colors.forEachIndexed { index, color ->
    val exitPoint = Offset(cx + 38f, cy - 10f + index * 5f)
    // Ray inside prism
    drawLine(color.copy(alpha = 0.8f), enterPoint, exitPoint, strokeWidth = 2f)
    // Dispersed ray exiting into air towards projection screen
    val endY = cy - 25f + (index * 14f)
    drawLine(color, exitPoint, Offset(size.width - 25f, endY), strokeWidth = 3f)
  }
}

private fun DrawScope.drawPhotoelectricCanvas(freqTHz: Float, workFuncEv: Float, phase: Float) {
  val cy = size.height / 2f

  // Vacuum phototube tube outline
  drawRoundRect(
    color = Color(0xFF334155),
    topLeft = Offset(40f, 30f),
    size = Size(size.width - 80f, size.height - 60f),
    cornerRadius = CornerRadius(24f, 24f),
    style = Stroke(width = 3f)
  )

  // Cathode plate (left)
  val cathodeX = 80f
  drawLine(Color(0xFF94A3B8), Offset(cathodeX, 60f), Offset(cathodeX, size.height - 60f), strokeWidth = 10f)

  // Anode plate (right)
  val anodeX = size.width - 80f
  drawLine(Color(0xFF94A3B8), Offset(anodeX, 60f), Offset(anodeX, size.height - 60f), strokeWidth = 10f)

  // Incoming photons (wavy light rays)
  val hEv = 4.1357e-15f
  val photonEnergyEv = hEv * (freqTHz * 1e12f)
  val canEject = photonEnergyEv > workFuncEv

  val photonColor = if (freqTHz > 700f) Color(0xFFA855F7) else Color(0xFFF59E0B)
  for (i in 0..2) {
    val startY = 70f + i * 40f
    drawLine(photonColor, Offset(10f, startY - 20f), Offset(cathodeX, startY), strokeWidth = 3f)
  }

  // Ejected electrons streaming to anode if energy exceeds work function
  if (canEject) {
    for (i in 0..4) {
      val electronProgress = ((phase / (2f * PI.toFloat())) + (i * 0.2f)) % 1f
      val ex = cathodeX + electronProgress * (anodeX - cathodeX)
      val ey = 70f + i * 25f
      drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(ex, ey))
    }
  }
}

private fun DrawScope.drawNuclearDecayCanvas(timeYears: Float, halfLife: Float) {
  val pad = 35f
  val w = size.width - pad * 2f
  val h = size.height - pad * 2f

  // Axes
  drawLine(Color.Gray, Offset(pad, pad), Offset(pad, pad + h), strokeWidth = 2f)
  drawLine(Color.Gray, Offset(pad, pad + h), Offset(pad + w, pad + h), strokeWidth = 2f)

  // Exponential decay curve
  val path = Path()
  for (xStep in 0..100) {
    val t = (xStep / 100f) * 20000f
    val fraction = 0.5.pow((t / halfLife).toDouble()).toFloat()
    val px = pad + (xStep / 100f) * w
    val py = pad + h - (fraction * h)
    if (xStep == 0) path.moveTo(px, py) else path.lineTo(px, py)
  }
  drawPath(path, color = Color(0xFF10B981), style = Stroke(width = 3.5f))

  // Current time position cursor
  val currentFraction = (timeYears / 20000f).coerceIn(0f, 1f)
  val curX = pad + currentFraction * w
  val curRemaining = 0.5.pow((timeYears / halfLife).toDouble()).toFloat()
  val curY = pad + h - (curRemaining * h)

  drawCircle(Color(0xFFF59E0B), radius = 7f, center = Offset(curX, curY))
  drawLine(Color(0xFFF59E0B).copy(alpha = 0.6f), Offset(curX, pad + h), Offset(curX, curY), strokeWidth = 2f)
}
