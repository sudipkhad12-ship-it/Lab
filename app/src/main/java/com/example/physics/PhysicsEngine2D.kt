package com.example.physics

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import kotlin.math.min
import kotlin.math.sqrt

enum class GravityPreset(val displayName: String, val gravityY: Float) {
  EARTH("Earth (9.8 m/s²)", 9.8f),
  MOON("Moon (1.62 m/s²)", 1.62f),
  MARS("Mars (3.71 m/s²)", 3.71f),
  ZERO_G("Zero-G (0.0 m/s²)", 0.0f),
  JUPITER("Jupiter (24.8 m/s²)", 24.8f)
}

enum class ExperimentPreset(val title: String, val description: String) {
  NEWTONS_CRADLE("Newton's Cradle", "Linear momentum and elastic collision transfer"),
  GRAVITY_DROP("Galileo Freefall", "Particles of varying masses accelerating under gravity"),
  BROWNIAN_GAS("Gas Diffusion", "High-velocity particle kinetic scattering in a chamber"),
  BILLIARDS_BREAK("2D Scatter Break", "Multi-particle 2D angular collision dynamics"),
  CHAOS_BUMPERS("Fixed Obstacles", "Dynamic particles bouncing off static pinned bumpers")
}

/**
 * 2D Physics Engine for virtual laboratory simulations.
 * Handles:
 * - Semi-implicit Euler integration
 * - Gravitational acceleration & customizable planetary gravity
 * - 2D particle-particle circle collision detection & impulse momentum resolution
 * - Positional correction to resolve penetration
 * - Boundary reflection with restitution and surface friction
 * - Real-time energy and momentum telemetry
 */
class PhysicsEngine2D(
  var gravity: Vector2D = Vector2D(0f, 9.8f * 40f), // Scaled to screen coordinates
  var bounds: Rect = Rect(0f, 0f, 800f, 600f),
  var airDamping: Float = 0.999f,
  var globalRestitution: Float = 0.85f
) {
  val particles = mutableListOf<Particle2D>()
  var collisionCount: Int = 0
    private set

  private var nextParticleId = 1

  fun clear() {
    particles.clear()
    collisionCount = 0
  }

  fun addParticle(particle: Particle2D) {
    particles.add(particle)
  }

  fun spawnParticle(
    pos: Vector2D,
    vel: Vector2D = Vector2D.ZERO,
    mass: Float = 1.0f,
    radius: Float = 14.0f,
    restitution: Float = globalRestitution,
    color: Color = Color(0xFF22D3EE),
    name: String = "P$nextParticleId"
  ): Particle2D {
    val p = Particle2D(
      id = nextParticleId++,
      position = pos,
      velocity = vel,
      mass = mass,
      radius = radius,
      restitution = restitution,
      color = color,
      name = name
    )
    particles.add(p)
    return p
  }

  fun totalKineticEnergy(): Float {
    var sum = 0f
    for (p in particles) {
      sum += p.kineticEnergy()
    }
    return sum
  }

  fun totalMomentum(): Vector2D {
    var sum = Vector2D.ZERO
    for (p in particles) {
      sum += p.momentum()
    }
    return sum
  }

  /**
   * Advances the simulation by dt seconds using sub-stepping for numerical stability.
   */
  fun update(dt: Float, substeps: Int = 4) {
    if (particles.isEmpty() || dt <= 0f) return
    val subDt = dt / substeps.toFloat()

    repeat(substeps) {
      stepSubstep(subDt)
    }

    // Update trails for visualization
    for (p in particles) {
      p.addTrailPoint()
    }
  }

  private fun stepSubstep(dt: Float) {
    // 1. Integration (Semi-Implicit Euler)
    for (p in particles) {
      if (p.isPinned) continue

      // Apply gravity to acceleration
      val totalAcc = p.acceleration + gravity
      p.velocity += totalAcc * dt

      // Air resistance / damping
      p.velocity *= airDamping

      // Position update
      p.position += p.velocity * dt
    }

    // 2. Resolve Boundary Collisions
    for (p in particles) {
      if (p.isPinned) continue
      resolveBoundaryCollision(p)
    }

    // 3. Resolve Particle-to-Particle Collisions
    resolveParticleCollisions()
  }

  private fun resolveBoundaryCollision(p: Particle2D) {
    val r = p.radius
    val e = p.restitution.coerceIn(0f, 1f)

    // Left wall
    if (p.position.x - r < bounds.left) {
      p.position = Vector2D(bounds.left + r, p.position.y)
      if (p.velocity.x < 0f) {
        p.velocity = Vector2D(-p.velocity.x * e, p.velocity.y * (1f - p.friction))
        collisionCount++
      }
    }
    // Right wall
    if (p.position.x + r > bounds.right) {
      p.position = Vector2D(bounds.right - r, p.position.y)
      if (p.velocity.x > 0f) {
        p.velocity = Vector2D(-p.velocity.x * e, p.velocity.y * (1f - p.friction))
        collisionCount++
      }
    }
    // Top wall
    if (p.position.y - r < bounds.top) {
      p.position = Vector2D(p.position.x, bounds.top + r)
      if (p.velocity.y < 0f) {
        p.velocity = Vector2D(p.velocity.x * (1f - p.friction), -p.velocity.y * e)
        collisionCount++
      }
    }
    // Floor
    if (p.position.y + r > bounds.bottom) {
      p.position = Vector2D(p.position.x, bounds.bottom - r)
      if (p.velocity.y > 0f) {
        p.velocity = Vector2D(p.velocity.x * (1f - p.friction), -p.velocity.y * e)
        collisionCount++
      }
    }
  }

  private fun resolveParticleCollisions() {
    val count = particles.size
    for (i in 0 until count) {
      val p1 = particles[i]
      for (j in i + 1 until count) {
        val p2 = particles[j]

        val delta = p2.position - p1.position
        val distSq = delta.magnitudeSquared()
        val minDist = p1.radius + p2.radius

        if (distSq < minDist * minDist) {
          val dist = sqrt(distSq)
          val normal = if (dist > 1e-4f) delta / dist else Vector2D(1f, 0f)

          // 1. Positional correction to resolve penetration
          val penetration = minDist - dist
          val totalInvMass = p1.invMass + p2.invMass
          if (totalInvMass > 0f) {
            val percent = 0.8f // Positional correction factor
            val correction = normal * ((penetration / totalInvMass) * percent)
            if (!p1.isPinned) {
              p1.position -= correction * p1.invMass
            }
            if (!p2.isPinned) {
              p2.position += correction * p2.invMass
            }
          }

          // 2. Relative velocity along collision normal
          val relVel = p2.velocity - p1.velocity
          val velAlongNormal = relVel.dot(normal)

          // Only resolve if particles are moving toward each other
          if (velAlongNormal < 0f && totalInvMass > 0f) {
            val e = min(p1.restitution, p2.restitution)
            val impulseMagnitude = -(1f + e) * velAlongNormal / totalInvMass
            val impulse = normal * impulseMagnitude

            if (!p1.isPinned) {
              p1.velocity -= impulse * p1.invMass
            }
            if (!p2.isPinned) {
              p2.velocity += impulse * p2.invMass
            }

            collisionCount++
          }
        }
      }
    }
  }

  fun loadPreset(preset: ExperimentPreset, width: Float, height: Float) {
    clear()
    bounds = Rect(20f, 20f, width - 20f, height - 20f)
    val cx = width / 2f
    val cy = height / 2f

    when (preset) {
      ExperimentPreset.NEWTONS_CRADLE -> {
        // Aligned balls with the first one incoming with high velocity
        gravity = Vector2D(0f, 0f)
        val radius = 18f
        val startX = cx - 54f
        val y = cy
        // 4 identical balls touching
        for (i in 0 until 4) {
          spawnParticle(
            pos = Vector2D(startX + i * (radius * 2f), y),
            vel = Vector2D.ZERO,
            mass = 2.0f,
            radius = radius,
            restitution = 1.0f,
            color = Color(0xFF22D3EE),
            name = "Ball ${i + 1}"
          )
        }
        // Incoming striker from left
        spawnParticle(
          pos = Vector2D(startX - 100f, y),
          vel = Vector2D(240f, 0f),
          mass = 2.0f,
          radius = radius,
          restitution = 1.0f,
          color = Color(0xFFF59E0B),
          name = "Striker"
        )
      }

      ExperimentPreset.GRAVITY_DROP -> {
        gravity = Vector2D(0f, 9.8f * 40f)
        val colors = listOf(
          Color(0xFF22D3EE),
          Color(0xFF10B981),
          Color(0xFFF59E0B),
          Color(0xFFA855F7),
          Color(0xFFEF4444)
        )
        val masses = listOf(0.5f, 1.0f, 2.0f, 4.0f, 8.0f)
        val radii = listOf(10f, 13f, 16f, 20f, 24f)
        val spacing = (bounds.right - bounds.left) / 6f
        for (i in 0 until 5) {
          spawnParticle(
            pos = Vector2D(bounds.left + spacing * (i + 1), bounds.top + 30f),
            vel = Vector2D.ZERO,
            mass = masses[i],
            radius = radii[i],
            restitution = 0.82f,
            color = colors[i],
            name = "${masses[i]}kg"
          )
        }
      }

      ExperimentPreset.BROWNIAN_GAS -> {
        gravity = Vector2D(0f, 0f)
        // 1 large heavy particle surrounded by rapid gas particles
        spawnParticle(
          pos = Vector2D(cx, cy),
          vel = Vector2D.ZERO,
          mass = 10.0f,
          radius = 26f,
          restitution = 1.0f,
          color = Color(0xFFF59E0B),
          name = "Heavy Mol"
        )
        val colors = listOf(Color(0xFF22D3EE), Color(0xFF10B981), Color(0xFFA855F7))
        for (i in 0 until 14) {
          val angle = (i.toFloat() / 14f) * (2f * Math.PI.toFloat())
          val speed = 150f + (i % 4) * 20f
          val px = cx + kotlin.math.cos(angle) * (50f + (i % 3) * 25f)
          val py = cy + kotlin.math.sin(angle) * (50f + (i % 3) * 25f)
          spawnParticle(
            pos = Vector2D(px, py),
            vel = Vector2D(kotlin.math.cos(angle + 1.2f) * speed, kotlin.math.sin(angle + 1.2f) * speed),
            mass = 0.8f,
            radius = 8f,
            restitution = 1.0f,
            color = colors[i % colors.size],
            name = "Gas $i"
          )
        }
      }

      ExperimentPreset.BILLIARDS_BREAK -> {
        gravity = Vector2D(0f, 9.8f * 10f)
        val radius = 13f
        val triangleX = cx + 60f
        val triangleY = cy
        val ballColors = listOf(
          Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981),
          Color(0xFF22D3EE), Color(0xFFA855F7), Color(0xFFEC4899)
        )
        var ballIdx = 0
        for (row in 0..2) {
          val rowX = triangleX + row * (radius * 1.8f)
          val startY = triangleY - row * radius
          for (col in 0..row) {
            val y = startY + col * (radius * 2.1f)
            spawnParticle(
              pos = Vector2D(rowX, y),
              vel = Vector2D.ZERO,
              mass = 1.5f,
              radius = radius,
              restitution = 0.95f,
              color = ballColors[ballIdx % ballColors.size],
              name = "B$ballIdx"
            )
            ballIdx++
          }
        }
        // Cue ball
        spawnParticle(
          pos = Vector2D(cx - 120f, triangleY),
          vel = Vector2D(320f, 15f),
          mass = 1.5f,
          radius = radius,
          restitution = 0.95f,
          color = Color.White,
          name = "Cue"
        )
      }

      ExperimentPreset.CHAOS_BUMPERS -> {
        gravity = Vector2D(0f, 9.8f * 25f)
        // Two pinned posts and bouncing particles
        val post1 = spawnParticle(
          pos = Vector2D(cx - 60f, cy + 20f),
          mass = 1000f,
          radius = 22f,
          restitution = 0.9f,
          color = Color(0xFF64748B),
          name = "Bumper 1"
        )
        post1.isPinned = true

        val post2 = spawnParticle(
          pos = Vector2D(cx + 60f, cy + 20f),
          mass = 1000f,
          radius = 22f,
          restitution = 0.9f,
          color = Color(0xFF64748B),
          name = "Bumper 2"
        )
        post2.isPinned = true

        val colors = listOf(Color(0xFF22D3EE), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFFA855F7))
        for (i in 0 until 7) {
          spawnParticle(
            pos = Vector2D(cx - 50f + i * 16f, bounds.top + 25f + (i % 2) * 20f),
            vel = Vector2D((i - 3) * 25f, 0f),
            mass = 1.2f,
            radius = 11f,
            restitution = 0.88f,
            color = colors[i % colors.size],
            name = "Ball $i"
          )
        }
      }
    }
  }
}
