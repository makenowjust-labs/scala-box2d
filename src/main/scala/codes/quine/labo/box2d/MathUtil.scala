package codes.quine.labo.box2d

import scala.util.Random

object MathUtil {
  final val PI: Float = 3.14159265358979323846264f

  implicit final class FloatOps(private val a: Float) extends AnyVal {
    def cross(v: Vec2): Vec2 = Vec2(-a * v.y, a * v.x)

    def *(v: Vec2): Vec2 = Vec2(a * v.x, a * v.y)
  }

  def abs(a: Float): Float = Math.abs(a)

  def abs(v: Vec2): Vec2 = Vec2(abs(v.x), abs(v.y))

  def abs(m: Mat22): Mat22 = Mat22(abs(m.col1), abs(m.col2))

  def sqrt(a: Float): Float = Math.sqrt(a).toFloat

  def sign(a: Float): Float = if (a < 0.0f) -1.0f else 1.0f

  def min(a: Float, b: Float): Float = if (a < b) a else b

  def max(a: Float, b: Float): Float = if (a > b) a else b

  def clamp(a: Float, low: Float, high: Float): Float = max(low, min(a, high))

  def random: Float = {
    val x = Random.nextFloat()
    x * 2.0f - 1.0f
  }

  def random(low: Float, high: Float): Float = {
    val x = Random.nextFloat()
    (high - low) * x + low
  }
}
