package codes.quine.labo.box2d

final class Mat22(val col1: Vec2, val col2: Vec2) {
  def transpose: Mat22 =
    Mat22(Vec2(col1.x, col2.x), Vec2(col1.y, col2.y))

  def invert: Mat22 = {
    val a = col1.x
    val b = col2.x
    val c = col1.y
    val d = col2.y
    val det = a * d - b * c
    assert(det != 0.0f)
    val invDet = 1.0f / det
    Mat22(invDet * d, -invDet * b, -invDet * c, invDet * a)
  }

  def *(v: Vec2): Vec2 =
    Vec2(col1.x * v.x + col2.x * v.y, col1.y * v.x + col2.y * v.y)

  def +(m: Mat22): Mat22 =
    Mat22(col1 + m.col1, col2 + m.col2)

  def *(m: Mat22): Mat22 =
    Mat22(this * m.col1, this * m.col2)

  override def equals(obj: Any): Boolean =
    obj match {
      case obj: Mat22 => col1 == obj.col1 && col2 == obj.col2
      case _          => false
    }

  override def toString: String = s"Mat22(${col1.x}, ${col2.x}, ${col1.y}, ${col2.y})"
}

object Mat22 {
  def apply(col1: Vec2, col2: Vec2): Mat22 = new Mat22(col1, col2)

  def apply(a: Float, b: Float, c: Float, d: Float): Mat22 = new Mat22(Vec2(a, c), Vec2(b, d))

  def rotation(angle: Float): Mat22 = {
    val c = Math.cos(angle).toFloat
    val s = Math.sin(angle).toFloat
    Mat22(c, -s, s, c)
  }

  def unapply(m: Mat22): Option[(Float, Float, Float, Float)] =
    Some((m.col1.x, m.col2.x, m.col1.y, m.col2.y))
}
