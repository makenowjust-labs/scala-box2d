package codes.quine.labo.box2d

final class Vec2(var x: Float, var y: Float) {
  def set(x: Float, y: Float): Unit = {
    this.x = x
    this.y = y
  }

  def unary_- : Vec2 = new Vec2(-x, -y)

  def +=(v: Vec2): Unit = {
    x += v.x
    y += v.y
  }

  def -=(v: Vec2): Unit = {
    x -= v.x
    y -= v.y
  }

  def *=(a: Float): Unit = {
    x *= a
    y *= a
  }

  def length: Float = Math.hypot(x, y).toFloat

  override def equals(obj: Any): Boolean =
    obj match {
      case obj: Vec2 => x == obj.x && y == obj.y
      case _         => false
    }

  override def toString: String = s"Vec2($x, $y)"
}

object Vec2 {
  def apply(x: Float, y: Float): Vec2 = new Vec2(x, y)

  def unapply(v: Vec2): Option[(Float, Float)] = Some((v.x, v.y))
}
