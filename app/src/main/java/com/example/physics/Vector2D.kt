package com.example.physics

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-performance 2D Vector for the virtual laboratory physics engine.
 * Supports complete vector algebra: dot product, cross product, normalization,
 * reflection, projection, and distance calculation.
 */
data class Vector2D(val x: Float = 0f, val y: Float = 0f) {
  operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
  operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
  operator fun times(scalar: Float): Vector2D = Vector2D(x * scalar, y * scalar)
  operator fun div(scalar: Float): Vector2D = if (scalar != 0f) Vector2D(x / scalar, y / scalar) else Vector2D()
  operator fun unaryMinus(): Vector2D = Vector2D(-x, -y)

  fun dot(other: Vector2D): Float = x * other.x + y * other.y
  fun cross(other: Vector2D): Float = x * other.y - y * other.x

  fun magnitudeSquared(): Float = x * x + y * y
  fun magnitude(): Float = sqrt(magnitudeSquared())

  fun distanceSquared(other: Vector2D): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
  }

  fun distance(other: Vector2D): Float = sqrt(distanceSquared(other))

  fun normalized(): Vector2D {
    val mag = magnitude()
    return if (mag > 1e-6f) this / mag else Vector2D(0f, 0f)
  }

  fun projectOnto(normal: Vector2D): Vector2D {
    val norm = normal.normalized()
    return norm * this.dot(norm)
  }

  fun reflect(normal: Vector2D): Vector2D {
    val norm = normal.normalized()
    return this - norm * (2f * this.dot(norm))
  }

  fun rotate(radians: Float): Vector2D {
    val cosA = cos(radians)
    val sinA = sin(radians)
    return Vector2D(x * cosA - y * sinA, x * sinA + y * cosA)
  }

  fun angle(): Float = atan2(y, x)

  companion object {
    val ZERO = Vector2D(0f, 0f)
    val UNIT_X = Vector2D(1f, 0f)
    val UNIT_Y = Vector2D(0f, 1f)
  }
}
