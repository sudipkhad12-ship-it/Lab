package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LabDarkBg
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
  onTimeout: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "splash_orbits")
  val rotationPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "atom_rotation"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  LaunchedEffect(Unit) {
    delay(1800)
    onTimeout()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF090D16), Color(0xFF111827), Color(0xFF0F172A))
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Futuristic Animated Atom & Flask Core
      Canvas(modifier = Modifier.size(150.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Central Glowing Nucleus
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(ElectricCyan, ElectricCyan.copy(alpha = 0.1f)),
            center = Offset(cx, cy),
            radius = 28f * pulseScale
          ),
          radius = 28f * pulseScale,
          center = Offset(cx, cy)
        )
        drawCircle(Color.White, radius = 9f, center = Offset(cx, cy))

        // 3 Elliptical Electron Orbit Rings
        val radX = 58f
        val radY = 22f

        listOf(0f, 60f, 120f).forEachIndexed { index, angleOffset ->
          val radAngle = Math.toRadians((rotationPhase + angleOffset).toDouble())
          val orbitColor = when (index) {
            0 -> ElectricCyan
            1 -> NeonEmerald
            else -> PlasmaViolet
          }

          // Draw orbital path
          val orbitSteps = 40
          for (s in 0 until orbitSteps) {
            val a1 = Math.toRadians((s * (360.0 / orbitSteps)))
            val a2 = Math.toRadians(((s + 1) * (360.0 / orbitSteps)))
            val rot = Math.toRadians(angleOffset.toDouble())

            val x1 = cx + (radX * cos(a1) * cos(rot) - radY * sin(a1) * sin(rot)).toFloat()
            val y1 = cy + (radX * cos(a1) * sin(rot) + radY * sin(a1) * cos(rot)).toFloat()
            val x2 = cx + (radX * cos(a2) * cos(rot) - radY * sin(a2) * sin(rot)).toFloat()
            val y2 = cy + (radX * cos(a2) * sin(rot) + radY * sin(a2) * cos(rot)).toFloat()

            drawLine(orbitColor.copy(alpha = 0.4f), Offset(x1, y1), Offset(x2, y2), strokeWidth = 2f)
          }

          // Electron Particle along the orbit
          val rot = Math.toRadians(angleOffset.toDouble())
          val ex = cx + (radX * cos(radAngle) * cos(rot) - radY * sin(radAngle) * sin(rot)).toFloat()
          val ey = cy + (radX * cos(radAngle) * sin(rot) + radY * sin(radAngle) * cos(rot)).toFloat()

          drawCircle(orbitColor, radius = 5.5f, center = Offset(ex, ey))
        }
      }

      Spacer(Modifier.height(24.dp))

      Text(
        text = "POWER LAB",
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 3.sp,
        fontFamily = FontFamily.SansSerif
      )

      Spacer(Modifier.height(6.dp))

      Text(
        text = "VIRTUAL SCIENCE LABORATORY",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ElectricCyan,
        letterSpacing = 2.sp,
        fontFamily = FontFamily.Monospace
      )

      Spacer(Modifier.height(36.dp))

      // Sleek loading indicator
      Box(
        modifier = Modifier
          .width(160.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(Color(0xFF1E293B))
      ) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxSize(),
          color = ElectricCyan,
          trackColor = Color(0xFF1E293B)
        )
      }

      Spacer(Modifier.height(12.dp))

      Text(
        text = "Initializing Physics & Chemistry Engines...",
        fontSize = 11.sp,
        color = Color(0xFF94A3B8),
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
