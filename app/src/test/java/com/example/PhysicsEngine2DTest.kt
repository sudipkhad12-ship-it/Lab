package com.example

import androidx.compose.ui.geometry.Rect
import com.example.physics.ExperimentPreset
import com.example.physics.Particle2D
import com.example.physics.PhysicsEngine2D
import com.example.physics.Vector2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PhysicsEngine2DTest {

  @Test
  fun testVector2DOperations() {
    val v1 = Vector2D(3f, 4f)
    assertEquals(5f, v1.magnitude(), 1e-4f)
    assertEquals(25f, v1.magnitudeSquared(), 1e-4f)

    val v2 = Vector2D(1f, 2f)
    val vSum = v1 + v2
    assertEquals(4f, vSum.x, 1e-4f)
    assertEquals(6f, vSum.y, 1e-4f)

    val vDiff = v1 - v2
    assertEquals(2f, vDiff.x, 1e-4f)
    assertEquals(2f, vDiff.y, 1e-4f)

    // Dot product: 3*1 + 4*2 = 11
    assertEquals(11f, v1.dot(v2), 1e-4f)

    // Normalized
    val norm = v1.normalized()
    assertEquals(1f, norm.magnitude(), 1e-4f)
    assertEquals(0.6f, norm.x, 1e-4f)
    assertEquals(0.8f, norm.y, 1e-4f)

    // Distance
    assertEquals(2.8284f, v1.distance(v2), 1e-3f)

    // Reflection against vertical normal (1, 0)
    val reflected = Vector2D(2f, 3f).reflect(Vector2D(1f, 0f))
    assertEquals(-2f, reflected.x, 1e-4f)
    assertEquals(3f, reflected.y, 1e-4f)
  }

  @Test
  fun testGravityFreeFall() {
    val engine = PhysicsEngine2D(
      gravity = Vector2D(0f, 9.8f),
      bounds = Rect(0f, 0f, 1000f, 1000f),
      airDamping = 1.0f
    )
    val p = engine.spawnParticle(
      pos = Vector2D(100f, 100f),
      vel = Vector2D.ZERO,
      mass = 1.0f,
      radius = 10f
    )

    // Step 1 second
    engine.update(1.0f, substeps = 10)

    // v = v0 + g * t = 0 + 9.8 * 1 = 9.8
    assertEquals(9.8f, p.velocity.y, 0.2f)
    assertTrue("Particle should have dropped below starting position", p.position.y > 100f)
  }

  @Test
  fun testElasticCollisionMomentumConservation() {
    val engine = PhysicsEngine2D(
      gravity = Vector2D.ZERO,
      bounds = Rect(0f, 0f, 1000f, 1000f),
      airDamping = 1.0f
    )

    // Particle 1 moving right, Particle 2 at rest
    val p1 = engine.spawnParticle(
      pos = Vector2D(100f, 200f),
      vel = Vector2D(50f, 0f),
      mass = 2.0f,
      radius = 15f,
      restitution = 1.0f
    )
    val p2 = engine.spawnParticle(
      pos = Vector2D(125f, 200f), // Colliding distance: dist = 25 < r1 + r2 = 30
      vel = Vector2D.ZERO,
      mass = 2.0f,
      radius = 15f,
      restitution = 1.0f
    )

    val initialMomentum = engine.totalMomentum()
    assertEquals(100f, initialMomentum.x, 1e-3f)

    // Step simulation
    engine.update(0.016f, substeps = 4)

    val finalMomentum = engine.totalMomentum()
    // Momentum along x should be conserved: m1*v1 + m2*v2 = initial
    assertEquals(initialMomentum.x, finalMomentum.x, 0.5f)
    assertTrue("Collision should be registered", engine.collisionCount > 0)
    assertTrue("P2 should have gained velocity to the right", p2.velocity.x > 0f)
  }

  @Test
  fun testBoundaryBounce() {
    val engine = PhysicsEngine2D(
      gravity = Vector2D.ZERO,
      bounds = Rect(0f, 0f, 200f, 200f),
      airDamping = 1.0f
    )
    val p = engine.spawnParticle(
      pos = Vector2D(195f, 100f), // hitting right boundary at 200 with radius 10
      vel = Vector2D(40f, 0f),
      mass = 1.0f,
      radius = 10f,
      restitution = 0.8f
    )

    engine.update(0.05f, substeps = 4)

    // Velocity should be reversed and damped by restitution
    assertTrue("Velocity x should reverse after right wall bounce", p.velocity.x < 0f)
    assertTrue("Position should be clamped inside bounds", p.position.x <= 200f - p.radius)
    assertEquals(1, engine.collisionCount)
  }

  @Test
  fun testPresetsLoading() {
    val engine = PhysicsEngine2D()
    engine.loadPreset(ExperimentPreset.NEWTONS_CRADLE, 600f, 400f)
    assertEquals(5, engine.particles.size)

    engine.loadPreset(ExperimentPreset.GRAVITY_DROP, 600f, 400f)
    assertEquals(5, engine.particles.size)

    engine.loadPreset(ExperimentPreset.BROWNIAN_GAS, 600f, 400f)
    assertTrue(engine.particles.size > 10)

    val ke = engine.totalKineticEnergy()
    assertTrue(ke > 0f)
    assertNotNull(engine.totalMomentum())
  }
}
