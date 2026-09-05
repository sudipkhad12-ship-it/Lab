package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet

data class OnboardingSlide(
  val title: String,
  val subtitle: String,
  val description: String,
  val icon: ImageVector,
  val iconTint: Color,
  val highlights: List<String>
)

@Composable
fun OnboardingScreen(
  onFinish: () -> Unit
) {
  var currentPage by remember { mutableIntStateOf(0) }

  val slides = listOf(
    OnboardingSlide(
      title = "Physics Laboratory",
      subtitle = "Interactive Circuits, Mechanics & Optics",
      description = "Build functional DC and AC circuits, adjust potentiometers, explore lens focal equations, prism light dispersion, and nuclear decay with real-time formulas.",
      icon = Icons.Default.Bolt,
      iconTint = ElectricCyan,
      highlights = listOf("Ohm's Law & Potentiometers", "Ray Optics & Prism Dispersion", "Pendulum & Photoelectric Effect")
    ),
    OnboardingSlide(
      title = "Chemistry & Molecules",
      subtitle = "Titration, Electrolysis & Organic Maker",
      description = "Perform dropwise acid-base titrations with live pH curves, operate Daniell galvanic cells, and build organic compounds with real valency checking and IUPAC naming.",
      icon = Icons.Default.Science,
      iconTint = NeonEmerald,
      highlights = listOf("Acid-Base Titration Curves", "Daniell Cell & Water Electrolysis", "Interactive Organic Compound Maker")
    ),
    OnboardingSlide(
      title = "Laboratory Progression",
      subtitle = "XP, Coins, Upgrades & Challenges",
      description = "Earn Science Coins and XP by completing hands-on experiments. Unlock and calibrate advanced digital instruments from Intern to Chief Lab Director!",
      icon = Icons.Default.MilitaryTech,
      iconTint = AtomicAmber,
      highlights = listOf("Interactive Equipment Shop", "Target Mission Challenges", "Laboratory Ranks & Achievements")
    )
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF090D16), Color(0xFF111827), Color(0xFF0F172A))
        )
      )
      .padding(24.dp)
  ) {
    // Top Bar with Skip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "POWER LAB",
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        color = ElectricCyan,
        letterSpacing = 1.sp
      )

      if (currentPage < slides.size - 1) {
        TextButton(
          onClick = onFinish,
          modifier = Modifier.testTag("skip_onboarding_button")
        ) {
          Text("Skip", color = Color.Gray, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    // Animated Slide Content
    AnimatedContent(
      targetState = currentPage,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      label = "onboarding_slide"
    ) { pageIndex ->
      val slide = slides[pageIndex]

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Hero Icon Card
        Box(
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(slide.iconTint.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = slide.icon,
            contentDescription = null,
            tint = slide.iconTint,
            modifier = Modifier.size(56.dp)
          )
        }

        Spacer(Modifier.height(28.dp))

        Text(
          text = slide.title,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
          text = slide.subtitle,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = slide.iconTint,
          textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))

        Text(
          text = slide.description,
          fontSize = 13.sp,
          color = Color(0xFFCBD5E1),
          textAlign = TextAlign.Center,
          lineHeight = 19.sp,
          modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Key Highlights
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
          slide.highlights.forEach { highlight ->
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = slide.iconTint, modifier = Modifier.size(16.dp))
                Text(highlight, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
              }
            }
          }
        }
      }
    }

    // Bottom Navigation with Page Indicators & Next/Get Started
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Page Dots
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        slides.indices.forEach { index ->
          Box(
            modifier = Modifier
              .size(width = if (currentPage == index) 24.dp else 8.dp, height = 8.dp)
              .clip(CircleShape)
              .background(if (currentPage == index) ElectricCyan else Color(0xFF475569))
          )
        }
      }

      // Next / Get Started Button
      Row(modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = {
            if (currentPage < slides.size - 1) {
              currentPage++
            } else {
              onFinish()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("onboarding_action_button")
        ) {
          Text(
            text = if (currentPage == slides.size - 1) "Enter Laboratory" else "Next Step",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00363F)
          )
          Spacer(Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF00363F),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
