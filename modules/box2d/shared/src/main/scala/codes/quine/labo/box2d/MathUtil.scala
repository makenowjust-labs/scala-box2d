package codes.quine.labo.box2d

import scala.util.Random

/** MathUtil looks similar to [[java.lang.Math Math]], but it is specialized for [[scala.Float Float]]. */
object MathUtil {

  /** PI is 3.14... */
  final val PI: Float = 3.14159265358979323846264f

  /** FloatOps enhances Float with Vec2 operations. */
  implicit final class FloatOps(private val a: Float) extends AnyVal {

    /** Calculates cross product between `a` and `v`. */
    def cross(v: Vec2): Vec2 = Vec2(-a * v.y, a * v.x)

    /** Calculates scalar mulplication between `a` and `v`. */
    def *(v: Vec2): Vec2 = Vec2(a * v.x, a * v.y)
  }

  /** Returns an absolute value of `a`. */
  def abs(a: Float): Float = Math.abs(a)

  /** Returns an absolute value of `v`. */
  def abs(v: Vec2): Vec2 = Vec2(abs(v.x), abs(v.y))

  /** Returns an absolute value of `m`. */
  def abs(m: Mat22): Mat22 = Mat22(abs(m.col1), abs(m.col2))

  /** Returns square root of `a`. */
  def sqrt(a: Float): Float = Math.sqrt(a).toFloat

  /** Returns `-1.0` if a is negative, or returns `1.0`. */
  def sign(a: Float): Float = if (a < 0.0f) -1.0f else 1.0f

  /** Returns a minimum value. */
  def min(a: Float, b: Float): Float = if (a < b) a else b

  /** Returns a maximum value. */
  def max(a: Float, b: Float): Float = if (a > b) a else b

  /** Fits a value of `a` between `low` and `high`. */
  def clamp(a: Float, low: Float, high: Float): Float = max(low, min(a, high))

  /** Returns a random value between `-1.0` and `1.0`. */
  def random: Float = {
    val x = Random.nextFloat()
    x * 2.0f - 1.0f
  }

  /** Returns a random value between `low` and `high`. */
  def random(low: Float, high: Float): Float = {
    val x = Random.nextFloat()
    (high - low) * x + low
  }
}
