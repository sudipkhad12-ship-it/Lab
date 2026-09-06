package com.example.physics

import androidx.compose.ui.graphics.Color

/**
 * Represents a particle or rigid disc in the 2D Physics Engine.
 *
 * @param id Unique particle identifier
 * @param position Current coordinates in 2D space
 * @param velocity Current linear velocity vector
 * @param acceleration External applied acceleration
 * @param mass Mass of the particle (invMass = 1/mass)
 * @param radius Collision boundary radius
 * @param restitution Coefficient of restitution (0 = inelastic, 1 = perfectly elastic)
 * @param friction Surface friction / tangential loss factor
 * @param color Rendering color
 * @param name Display label
 * @param isPinned True if fixed/immobile (infinite mass)
 */
data class Particle2D(
  val id: Int,
  var position: Vector2D,
  var velocity: Vector2D = Vector2D.ZERO,
  var acceleration: Vector2D = Vector2D.ZERO,
  val mass: Float = 1.0f,
  val radius: Float = 12.0f,
  var restitution: Float = 0.85f,
  val friction: Float = 0.05f,
  val color: Color = Color(0xFF22D3EE),
  val name: String = "P$id",
  var isPinned: Boolean = false,
  var charge: Float = 0f
) {
  val invMass: Float
    get() = if (isPinned || mass <= 0f) 0f else 1f / mass

  // Breadcrumb trajectory trail for visualization in the lab
  val trail = ArrayDeque<Vector2D>(20)

  fun addTrailPoint() {
    if (trail.size >= 16) {
      trail.removeFirst()
    }
    trail.addLast(position)
  }

  fun kineticEnergy(): Float {
    return 0.5f * mass * velocity.magnitudeSquared()
  }

  fun momentum(): Vector2D {
    return velocity * mass
  }
}
